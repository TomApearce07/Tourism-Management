package com.viettour.controller.publicweb;

import com.viettour.dao.BookingDAO;
import com.viettour.model.Account;
import com.viettour.model.Booking;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Nhận kết quả thanh toán sandbox (URL /customer/payment-return và /api/ipn-handler trong web.xml).
 *
 * Quy tắc nghiệp vụ:
 *  - Phải đăng nhập, và chỉ CHỦ ĐƠN mới được xác nhận / hủy thanh toán đơn của mình.
 *  - Chỉ đơn đang UNPAID mới được đổi trạng thái (đơn PAID, REFUNDED... không bị ghi đè).
 *  - Thanh toán thành công: UNPAID -> WAITING_APPROVAL (chờ Provider duyệt).
 *  - Khách hủy giao dịch: UNPAID -> REFUNDED (history.jsp hiển thị "Đã hủy / Hoàn tiền").
 *
 * Lưu ý: /api/ipn-handler không nằm trong filter đăng nhập nên servlet tự kiểm tra session.
 * Đây là sandbox mô phỏng. Khi dùng cổng thanh toán thật phải thay bằng kiểm tra chữ ký (HMAC)
 * do cổng gửi về, không để khóa bí mật trong HTML.
 */
public class PaymentReturnServlet extends HttpServlet {

    private static final String SANDBOX_KEY = "VT_SANDBOX_KEY_2026";
    private static final String MSG_ATTR = "paymentSuccessMsg";

    /** Trình duyệt quay về sau khi thanh toán: ?bookingId=7&vnp_ResponseCode=00 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int bookingId = parseInt(request.getParameter("bookingId"));
        boolean success = "00".equals(request.getParameter("vnp_ResponseCode"));
        process(request, response, bookingId, success);
    }

    /** Form sandbox: secretKey, orderId, status = SUCCESS hoặc FAILED. */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String secretKey = request.getParameter("secretKey");
        if (secretKey == null || !SANDBOX_KEY.equals(secretKey)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("Lỗi bảo mật: Sai Secret Key!");
            return;
        }

        int bookingId = parseInt(request.getParameter("orderId"));
        boolean success = "SUCCESS".equals(request.getParameter("status"));
        process(request, response, bookingId, success);
    }

    private void process(HttpServletRequest request, HttpServletResponse response,
            int bookingId, boolean success) throws IOException {

        String base = request.getContextPath();

        HttpSession session = request.getSession(false);
        Account account = (session == null) ? null : (Account) session.getAttribute("account");
        if (account == null) {
            response.sendRedirect(base + "/login");
            return;
        }
        if (bookingId <= 0) {
            response.sendRedirect(base + "/customer/history?payment=failed");
            return;
        }

        BookingDAO bookingDAO = new BookingDAO();
        try {
            Booking booking = bookingDAO.getBookingById(bookingId);

            // Đơn phải tồn tại, thuộc về người đang đăng nhập và chưa thanh toán
            boolean valid = booking != null
                    && booking.getCustomerId() == account.getAccountId()
                    && "UNPAID".equalsIgnoreCase(booking.getPaymentStatus());
            if (!valid) {
                response.sendRedirect(base + "/customer/history?payment=failed");
                return;
            }

            if (success) {
                bookingDAO.updatePaymentStatus(bookingId, "WAITING_APPROVAL");
                session.setAttribute(MSG_ATTR, "Giao dịch QR Sandbox #BK-" + bookingId
                        + " thành công! Đơn hàng đang chờ Nhà cung cấp duyệt.");
                response.sendRedirect(base + "/customer/history?payment=waiting");
            } else {
                bookingDAO.updatePaymentStatus(bookingId, "REFUNDED");
                session.setAttribute(MSG_ATTR, "Bạn đã hủy giao dịch thanh toán cho đơn #BK-" + bookingId + ".");
                response.sendRedirect(base + "/customer/history?payment=cancelled");
            }
        } finally {
            bookingDAO.close();
        }
    }

    /** Trả về -1 khi thiếu hoặc không phải số, thay vì để servlet ném lỗi 500. */
    private int parseInt(String value) {
        try {
            return Integer.parseInt(value == null ? "" : value.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

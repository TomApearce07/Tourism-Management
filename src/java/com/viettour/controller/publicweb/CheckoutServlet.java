package com.viettour.controller.publicweb;

import com.viettour.dao.BookingDAO;
import com.viettour.dao.TourDAO;
import com.viettour.model.Account;
import com.viettour.model.Booking;
import com.viettour.model.Tour;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;

/**
 * Đặt tour (URL /customer/checkout khai báo trong web.xml).
 *
 * Quy tắc nghiệp vụ:
 *  - Chỉ Customer (RoleID = 1) được đặt tour.
 *  - Chỉ đặt được tour đã APPROVED, số khách từ 1 đến số chỗ còn trống.
 *  - Tổng tiền luôn tính ở server (không tin số tiền từ trình duyệt).
 *  - Đơn mới có trạng thái UNPAID. Số chỗ chỉ bị trừ khi Provider duyệt đơn.
 */
public class CheckoutServlet extends HttpServlet {

    private static final String CHECKOUT_JSP = "/views/customer/checkout.jsp";
    private static final int ROLE_CUSTOMER = 1;

    // Cột SpecialRequests trong DB là NVARCHAR(500)
    private static final int MAX_SPECIAL_REQUESTS = 500;

    private static final String PHONE_REGEX = "^0[0-9]{9,10}$";
    private static final String ID_CARD_REGEX = "^[A-Za-z0-9]{8,20}$";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isCustomer(request)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        int tourId = parseInt(request.getParameter("tourId"), -1);
        int guests = parseInt(request.getParameter("guests"), 1);

        TourDAO tourDAO = new TourDAO();
        try {
            Tour tour = tourDAO.getTourById(tourId);
            if (!isBookable(tour)) {
                response.sendRedirect(request.getContextPath() + "/tours");
                return;
            }
            // Ép số khách về khoảng hợp lệ thay vì báo lỗi (giống nút +/- ở trang chi tiết tour)
            if (guests < 1) {
                guests = 1;
            }
            if (guests > tour.getAvailableSeats()) {
                guests = tour.getAvailableSeats();
            }
            forwardCheckout(request, response, tour, guests, null);
        } finally {
            tourDAO.close();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isCustomer(request)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        Account account = (Account) request.getSession().getAttribute("account");

        int tourId = parseInt(request.getParameter("tourId"), -1);
        int guests = parseInt(request.getParameter("guestsCount"), 0);

        String guestFullName = clean(request.getParameter("guestFullName"));
        String customerPhone = clean(request.getParameter("customerPhone"));
        String customerIdCard = clean(request.getParameter("customerIdCard"));
        String bookingChannel = clean(request.getParameter("bookingChannel"));
        String specialRequests = clean(request.getParameter("specialRequests"));
        String paymentMethod = clean(request.getParameter("paymentMethod"));

        TourDAO tourDAO = new TourDAO();
        BookingDAO bookingDAO = new BookingDAO();
        try {
            Tour tour = tourDAO.getTourById(tourId);
            if (!isBookable(tour)) {
                response.sendRedirect(request.getContextPath() + "/tours");
                return;
            }

            // ===== Kiểm tra dữ liệu =====
            String error = null;
            if (guests < 1) {
                error = "Số lượng khách phải từ 1 trở lên!";
            } else if (guests > tour.getAvailableSeats()) {
                error = "Tour chỉ còn " + tour.getAvailableSeats() + " chỗ trống, bạn không thể đặt " + guests + " khách!";
            }
            if (guestFullName.isEmpty()) {
                guestFullName = account.getFullName();   // bỏ trống thì lấy tên chủ tài khoản
            }
            if (guestFullName.length() > 100) {
                guestFullName = guestFullName.substring(0, 100);
            }
            if (error == null && !customerPhone.matches(PHONE_REGEX)) {
                error = "Số điện thoại phải bắt đầu bằng 0 và gồm 10 hoặc 11 chữ số!";
            }
            if (error == null && !customerIdCard.matches(ID_CARD_REGEX)) {
                error = "Số CCCD / Passport gồm 8-20 ký tự chữ hoặc số!";
            }
            if (error == null && !"VNPAY_SANDBOX".equals(paymentMethod) && !"MOMO_SANDBOX".equals(paymentMethod)) {
                error = "Vui lòng chọn phương thức thanh toán hợp lệ!";
            }
            if (error != null) {
                int shown = Math.max(1, Math.min(guests, tour.getAvailableSeats()));
                forwardCheckout(request, response, tour, shown, error);
                return;
            }

            // Chỉ nhận các kênh bán hàng đã định nghĩa
            if (!"OTA_KLOOK_AGODA".equals(bookingChannel) && !"ZALO_HOTLINE".equals(bookingChannel)
                    && !"AGENCY".equals(bookingChannel)) {
                bookingChannel = "WEBSITE";
            }

            // ===== Tính tiền ở server =====
            BigDecimal totalAmount = calculateDynamicTotal(tour.getPrice(), guests);
            BigDecimal totalCost = tour.getUnitCost().multiply(new BigDecimal(guests));
            BigDecimal profit = totalAmount.subtract(totalCost);

            // ===== Ghi chú đơn hàng (các trường ngăn cách bằng " | ") =====
            StringBuilder note = new StringBuilder();
            note.append("Hành khách: ").append(noPipe(guestFullName))
                    .append(" | SĐT: ").append(customerPhone)
                    .append(" | CCCD: ").append(customerIdCard)
                    .append(" | Kênh: ").append(bookingChannel)
                    .append(" | [Giá vốn: ").append(totalCost.toPlainString())
                    .append("đ, Lợi nhuận: ").append(profit.toPlainString()).append("đ]");

            // Phần ghi chú của khách bị cắt bớt để tổng độ dài không vượt cột trong DB
            String label = " | Ghi chú: ";
            int room = MAX_SPECIAL_REQUESTS - note.length() - label.length();
            if (!specialRequests.isEmpty() && room > 0) {
                String extra = noPipe(specialRequests);
                if (extra.length() > room) {
                    extra = extra.substring(0, room);
                }
                note.append(label).append(extra);
            }
            String noteText = note.toString();
            if (noteText.length() > MAX_SPECIAL_REQUESTS) {
                noteText = noteText.substring(0, MAX_SPECIAL_REQUESTS);
            }

            // ===== Tạo đơn =====
            Booking booking = new Booking();
            booking.setCustomerId(account.getAccountId());
            booking.setTourId(tourId);
            booking.setGuestsCount(guests);
            booking.setSpecialRequests(noteText);
            booking.setTotalAmount(totalAmount);
            booking.setPaymentMethod(paymentMethod);
            booking.setPaymentStatus("UNPAID");
            booking.setCustomerPhone(customerPhone);
            booking.setCustomerIdCard(customerIdCard);
            booking.setBookingChannel(bookingChannel);

            int bookingId = bookingDAO.insertBooking(booking);
            if (bookingId <= 0) {
                // insertBooking trả về giá trị <= 0 khi ghi DB thất bại: không chuyển sang bước thanh toán
                forwardCheckout(request, response, tour, guests,
                        "Không thể tạo đơn hàng lúc này. Vui lòng thử lại sau!");
                return;
            }

            // Chuyển thẳng tới trang giao diện thanh toán sandbox (chỉ là màn hình mô phỏng).
            // Trạng thái đơn chỉ đổi khi người dùng bấm nút ở trang đó (PaymentReturnServlet kiểm tra chủ đơn và trạng thái).
            response.sendRedirect(request.getContextPath()
                    + "/views/customer/sandbox-payment.jsp?orderId=" + bookingId
                    + "&amount=" + totalAmount.toPlainString()
                    + "&tourId=" + tourId
                    + "&guests=" + guests);
        } finally {
            tourDAO.close();
            bookingDAO.close();
        }
    }

    /**
     * Giá = đơn giá x số khách x hệ số mùa x hệ số đoàn, làm tròn về số nguyên.
     *  - Hệ số mùa theo tháng đặt: tháng 6-8 và 12 là 1.10; tháng 9-10 là 0.95; còn lại là 1.
     *  - Hệ số đoàn: từ 10 khách là 0.90; từ 5 khách là 0.95; còn lại là 1.
     * (Giữ nguyên công thức của bản cũ.)
     */
    private BigDecimal calculateDynamicTotal(BigDecimal price, int guests) {
        BigDecimal base = price.multiply(new BigDecimal(guests));

        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        BigDecimal seasonFactor = BigDecimal.ONE;
        if ((month >= 6 && month <= 8) || month == 12) {
            seasonFactor = new BigDecimal("1.10");
        } else if (month == 9 || month == 10) {
            seasonFactor = new BigDecimal("0.95");
        }

        BigDecimal groupFactor = BigDecimal.ONE;
        if (guests >= 10) {
            groupFactor = new BigDecimal("0.90");
        } else if (guests >= 5) {
            groupFactor = new BigDecimal("0.95");
        }

        return base.multiply(seasonFactor).multiply(groupFactor).setScale(0, RoundingMode.HALF_UP);
    }

    /** Đặt các thuộc tính mà checkout.jsp cần rồi forward (kèm thông báo lỗi nếu có). */
    private void forwardCheckout(HttpServletRequest request, HttpServletResponse response,
            Tour tour, int guests, String error) throws ServletException, IOException {
        BigDecimal totalAmount = calculateDynamicTotal(tour.getPrice(), guests);
        BigDecimal totalCost = tour.getUnitCost().multiply(new BigDecimal(guests));

        request.setAttribute("tour", tour);
        request.setAttribute("guests", Integer.valueOf(guests));
        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("totalCost", totalCost);
        request.setAttribute("estimatedProfit", totalAmount.subtract(totalCost));
        if (error != null) {
            request.setAttribute("error", error);
        }
        request.getRequestDispatcher(CHECKOUT_JSP).forward(request, response);
    }

    /** Tour phải tồn tại, đã được duyệt và còn ít nhất 1 chỗ. */
    private boolean isBookable(Tour tour) {
        return tour != null
                && "APPROVED".equalsIgnoreCase(tour.getApprovalStatus())
                && tour.getAvailableSeats() >= 1;
    }

    private boolean isCustomer(HttpServletRequest request) {
        Account account = (Account) request.getSession().getAttribute("account");
        return account != null && account.getRoleId() == ROLE_CUSTOMER;
    }

    private int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value == null ? "" : value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    /** Dấu "|" là ký tự ngăn cách trong ghi chú nên thay bằng "/" để khách không giả được trường khác. */
    private String noPipe(String value) {
        return value.replace('|', '/');
    }
}

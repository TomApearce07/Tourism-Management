package com.viettour.controller.provider;

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
import java.util.List;

/**
 * Provider xem và duyệt đơn đặt tour (URL /provider/booking-orders trong web.xml).
 *
 * Quy tắc nghiệp vụ:
 *  - Provider chỉ xử lý được đơn thuộc tour của chính mình.
 *  - Chỉ đơn đang WAITING_APPROVAL (khách đã thanh toán) mới duyệt hoặc từ chối được,
 *    nên bấm lại / gửi lại form không trừ chỗ hai lần.
 *  - Duyệt: trừ chỗ rồi chuyển PAID. Nếu không còn đủ chỗ thì đơn chuyển REFUNDED (hoàn tiền).
 *  - Từ chối: chuyển REFUNDED.
 */
public class ProviderBookingServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Account account = (Account) request.getSession().getAttribute("account");

        BookingDAO bookingDAO = new BookingDAO();
        try {
            List<?> orders = bookingDAO.getBookingsByProvider(account.getAccountId());
            request.setAttribute("orders", orders);
        } finally {
            bookingDAO.close();
        }
        request.getRequestDispatcher("/views/provider/booking-orders.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Account account = (Account) request.getSession().getAttribute("account");
        String action = request.getParameter("action");

        int bookingId;
        try {
            bookingId = Integer.parseInt(request.getParameter("bookingId").trim());
        } catch (RuntimeException e) {          // thiếu tham số hoặc không phải số
            response.sendRedirect(request.getContextPath() + "/provider/booking-orders");
            return;
        }

        BookingDAO bookingDAO = new BookingDAO();
        TourDAO tourDAO = new TourDAO();
        try {
            Booking booking = bookingDAO.getBookingById(bookingId);
            Tour tour = (booking == null) ? null : tourDAO.getTourById(booking.getTourId());

            boolean allowed = booking != null
                    && tour != null
                    && tour.getProviderId() == account.getAccountId()            // đúng chủ tour
                    && "WAITING_APPROVAL".equalsIgnoreCase(booking.getPaymentStatus()); // chưa xử lý

            if (allowed) {
                if ("approve".equalsIgnoreCase(action)) {
                    boolean enoughSeats = tourDAO.deductAvailableSeats(booking.getTourId(), booking.getGuestsCount());
                    bookingDAO.updatePaymentStatus(bookingId, enoughSeats ? "PAID" : "REFUNDED");
                } else if ("reject".equalsIgnoreCase(action)) {
                    bookingDAO.updatePaymentStatus(bookingId, "REFUNDED");
                }
            }
        } finally {
            bookingDAO.close();
            tourDAO.close();
        }
        response.sendRedirect(request.getContextPath() + "/provider/booking-orders");
    }
}

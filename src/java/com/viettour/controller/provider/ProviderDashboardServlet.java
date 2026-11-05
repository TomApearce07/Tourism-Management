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
import java.math.BigDecimal;
import java.util.List;

/** Bảng điều khiển của Provider: danh sách tour, số tour đang bán, số đơn đã thanh toán, doanh thu. */
public class ProviderDashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Account account = (Account) request.getSession().getAttribute("account");
        int providerId = account.getAccountId();

        TourDAO tourDAO = new TourDAO();
        BookingDAO bookingDAO = new BookingDAO();
        try {
            List<?> myTours = tourDAO.getToursByProvider(providerId);
            List<?> orders = bookingDAO.getBookingsByProvider(providerId);
            BigDecimal revenue = bookingDAO.getProviderRevenue(providerId);

            long activeTours = 0;
            for (Object o : myTours) {
                if ("APPROVED".equalsIgnoreCase(((Tour) o).getApprovalStatus())) {
                    activeTours++;
                }
            }
            long paidBookings = 0;
            for (Object o : orders) {
                if ("PAID".equalsIgnoreCase(((Booking) o).getPaymentStatus())) {
                    paidBookings++;
                }
            }

            request.setAttribute("myTours", myTours);
            request.setAttribute("activeToursCount", Long.valueOf(activeTours));
            request.setAttribute("paidBookingsCount", Long.valueOf(paidBookings));
            request.setAttribute("providerRevenue", revenue);
        } finally {
            tourDAO.close();
            bookingDAO.close();
        }
        request.getRequestDispatcher("/views/provider/my-tours.jsp").forward(request, response);
    }
}

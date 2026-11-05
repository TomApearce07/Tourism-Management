package com.viettour.controller.manager;

import com.viettour.dao.BookingDAO;
import com.viettour.model.Booking;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/** Manager xem toàn bộ đơn đặt tour trên nền tảng. */
public class ManageBookingsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        BookingDAO bookingDAO = new BookingDAO();
        try {
            List<Booking> all = bookingDAO.getAllBookings();
            bookingDAO.attachPassengers(all, true);          // Manager: CCCD bị che, không có ngày sinh
            request.setAttribute("allBookings", all);
        } finally {
            bookingDAO.close();
        }
        request.getRequestDispatcher("/views/manager/all-bookings.jsp").forward(request, response);
    }
}

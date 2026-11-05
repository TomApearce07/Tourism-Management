package com.viettour.controller.manager;

import com.viettour.dao.BookingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Manager xem toàn bộ đơn đặt tour trên nền tảng. */
public class ManageBookingsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        BookingDAO bookingDAO = new BookingDAO();
        try {
            request.setAttribute("allBookings", bookingDAO.getAllBookings());
        } finally {
            bookingDAO.close();
        }
        request.getRequestDispatcher("/views/manager/all-bookings.jsp").forward(request, response);
    }
}

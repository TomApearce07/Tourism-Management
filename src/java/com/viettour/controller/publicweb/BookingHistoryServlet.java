package com.viettour.controller.publicweb;

import com.viettour.dao.BookingDAO;
import com.viettour.model.Account;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Lịch sử đặt tour của khách đang đăng nhập (URL /customer/history trong web.xml). */
public class BookingHistoryServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Account account = (Account) request.getSession().getAttribute("account");

        BookingDAO bookingDAO = new BookingDAO();
        try {
            request.setAttribute("bookingList", bookingDAO.getBookingsByCustomer(account.getAccountId()));
        } finally {
            bookingDAO.close();
        }
        request.getRequestDispatcher("/views/customer/history.jsp").forward(request, response);
    }
}

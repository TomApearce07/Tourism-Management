package com.viettour.controller.publicweb;

import com.viettour.dao.BookingDAO;
import com.viettour.dao.NotificationDAO;
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
        NotificationDAO notiDAO = new NotificationDAO();
        try {
            request.setAttribute("notifications", notiDAO.getUnread(account.getAccountId()));   // thông báo hủy tour / xác nhận
            notiDAO.markAllRead(account.getAccountId());
            request.setAttribute("bookingList", bookingDAO.getBookingsByCustomer(account.getAccountId()));
        } finally {
            bookingDAO.close();
            notiDAO.close();
        }
        request.getRequestDispatcher("/views/customer/history.jsp").forward(request, response);
    }
}

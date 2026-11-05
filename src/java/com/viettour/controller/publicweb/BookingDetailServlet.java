package com.viettour.controller.publicweb;

import com.viettour.dao.AccountDAO;
import com.viettour.dao.BookingDAO;
import com.viettour.dao.TourDAO;
import com.viettour.model.Account;
import com.viettour.model.Booking;
import com.viettour.model.Tour;
import com.viettour.util.InputUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Chi tiết một đơn đặt tour của khách (URL /customer/booking-detail?id=... trong web.xml).
 *
 * Quy tắc nghiệp vụ:
 *  - Chỉ CHỦ ĐƠN xem được (so customerId của đơn với tài khoản đang đăng nhập); sai thì về trang lịch sử.
 *  - Hiện thông tin liên hệ của Nhà cung cấp (tên công ty, SĐT, email, địa chỉ) và danh sách hành khách đầy đủ.
 */
public class BookingDetailServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Account account = (Account) request.getSession().getAttribute("account");
        int id = InputUtil.parseInt(request.getParameter("id"), -1);

        BookingDAO bookingDAO = new BookingDAO();
        TourDAO tourDAO = new TourDAO();
        AccountDAO accountDAO = new AccountDAO();
        try {
            Booking booking = bookingDAO.getBookingById(id);
            if (booking == null || booking.getCustomerId() != account.getAccountId()) {
                response.sendRedirect(request.getContextPath() + "/customer/history");
                return;
            }
            Tour tour = tourDAO.getTourById(booking.getTourId());
            List<Booking> one = new ArrayList<>();
            one.add(booking);
            bookingDAO.attachPassengers(one, false);        // chủ đơn được xem đầy đủ

            request.setAttribute("booking", booking);
            request.setAttribute("tour", tour);
            request.setAttribute("provider", tour == null ? null : accountDAO.getAccountById(tour.getProviderId()));
        } finally {
            bookingDAO.close();
            tourDAO.close();
            accountDAO.close();
        }
        request.getRequestDispatcher("/views/customer/booking-detail.jsp").forward(request, response);
    }
}

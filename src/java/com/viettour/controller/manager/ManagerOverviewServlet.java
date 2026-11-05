package com.viettour.controller.manager;

import com.viettour.dao.BookingDAO;
import com.viettour.dao.TourDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Tổng quan của Manager: số tour chờ duyệt, đang bán, doanh thu nền tảng, các tour chờ duyệt gần đây. */
public class ManagerOverviewServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        TourDAO tourDAO = new TourDAO();
        BookingDAO bookingDAO = new BookingDAO();
        try {
            request.setAttribute("pendingCount", Integer.valueOf(tourDAO.countToursByStatus("PENDING")));
            request.setAttribute("activeCount", Integer.valueOf(tourDAO.countToursByStatus("APPROVED")));
            request.setAttribute("totalRevenue", bookingDAO.getTotalPlatformRevenue());
            request.setAttribute("recentPendingTours", tourDAO.getToursByStatus("PENDING"));
        } finally {
            tourDAO.close();
            bookingDAO.close();
        }
        request.getRequestDispatcher("/views/manager/overview.jsp").forward(request, response);
    }
}

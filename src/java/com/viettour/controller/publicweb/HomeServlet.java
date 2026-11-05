package com.viettour.controller.publicweb;

import com.viettour.dao.TourDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Trang chủ: hiển thị các tour đã được duyệt (URL "" và /home trong web.xml). */
public class HomeServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        TourDAO tourDAO = new TourDAO();
        try {
            request.setAttribute("featuredTours", tourDAO.getApprovedTours());
        } finally {
            tourDAO.close();
        }
        request.getRequestDispatcher("/views/public/home.jsp").forward(request, response);
    }
}

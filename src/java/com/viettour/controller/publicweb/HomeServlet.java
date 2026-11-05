package com.viettour.controller.publicweb;

import com.viettour.dao.TourDAO;
import com.viettour.dao.TourDepartureDAO;
import com.viettour.model.Tour;
import java.util.List;
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
        TourDepartureDAO depDAO = new TourDepartureDAO();
        try {
            List<Tour> tours = tourDAO.getApprovedTours();
            depDAO.attachDepartures(tours, null);
            request.setAttribute("featuredTours", tours);
        } finally {
            tourDAO.close();
            depDAO.close();
        }
        request.getRequestDispatcher("/views/public/home.jsp").forward(request, response);
    }
}

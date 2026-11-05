package com.viettour.controller.manager;

import com.viettour.dao.TourDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Danh sách toàn bộ tour để Manager quản lý và duyệt. */
public class ManageToursServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        TourDAO tourDAO = new TourDAO();
        try {
            request.setAttribute("allTours", tourDAO.getAllToursForManager());
            request.setAttribute("pendingCount", Integer.valueOf(tourDAO.countToursByStatus("PENDING")));
            request.setAttribute("approvedCount", Integer.valueOf(tourDAO.countToursByStatus("APPROVED")));
        } finally {
            tourDAO.close();
        }
        request.getRequestDispatcher("/views/manager/all-tours.jsp").forward(request, response);
    }
}

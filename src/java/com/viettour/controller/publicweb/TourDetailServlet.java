package com.viettour.controller.publicweb;

import com.viettour.dao.TourDAO;
import com.viettour.model.Tour;
import com.viettour.util.InputUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Chi tiết tour (URL /tour-detail?id=... trong web.xml).
 * Khách chỉ xem được tour đã được duyệt; id sai hoặc tour chưa duyệt thì quay lại danh sách tour.
 */
public class TourDetailServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = InputUtil.parseInt(request.getParameter("id"), -1);

        TourDAO tourDAO = new TourDAO();
        Tour tour;
        try {
            tour = tourDAO.getTourById(id);
        } finally {
            tourDAO.close();
        }

        if (tour == null || !"APPROVED".equalsIgnoreCase(tour.getApprovalStatus())) {
            response.sendRedirect(request.getContextPath() + "/tours");
            return;
        }
        request.setAttribute("tour", tour);
        request.getRequestDispatcher("/views/public/tour-detail.jsp").forward(request, response);
    }
}

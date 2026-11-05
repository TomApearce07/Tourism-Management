package com.viettour.controller.publicweb;

import com.viettour.dao.TourDAO;
import com.viettour.dao.TourDepartureDAO;
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
        TourDepartureDAO depDAO = new TourDepartureDAO();
        Tour tour;
        try {
            tour = tourDAO.getTourById(id);
            if (tour != null) {
                request.setAttribute("departures", depDAO.getOpenDepartures(id));   // ngày còn chỗ, từ hôm nay
            }
        } finally {
            tourDAO.close();
            depDAO.close();
        }

        if (tour == null || !"APPROVED".equalsIgnoreCase(tour.getApprovalStatus())) {
            response.sendRedirect(request.getContextPath() + "/tours");
            return;
        }
        request.setAttribute("tour", tour);
        request.setAttribute("selectedDepartureId", Integer.valueOf(InputUtil.parseInt(request.getParameter("departureId"), -1)));
        request.getRequestDispatcher("/views/public/tour-detail.jsp").forward(request, response);
    }
}

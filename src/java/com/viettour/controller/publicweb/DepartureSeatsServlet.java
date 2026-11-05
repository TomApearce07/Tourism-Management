package com.viettour.controller.publicweb;

import com.viettour.dao.TourDepartureDAO;
import com.viettour.model.TourDeparture;
import com.viettour.util.InputUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * JSON nhẹ cho trang chi tiết tour tự cập nhật số chỗ (URL /api/departure-seats?tourId=...).
 * Công khai, chỉ trả số chỗ và số khách đăng ký (không có dữ liệu cá nhân). Chỉ GET.
 */
public class DepartureSeatsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int tourId = InputUtil.parseInt(request.getParameter("tourId"), -1);
        TourDepartureDAO dao = new TourDepartureDAO();
        List<TourDeparture> list;
        try {
            list = dao.getOpenDeparturesIncludingFull(tourId);
        } finally {
            dao.close();
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            TourDeparture d = list.get(i);
            if (i > 0) {
                sb.append(',');
            }
            sb.append("{\"departureId\":").append(d.getDepartureId())
                    .append(",\"seatsLeft\":").append(d.getSeatsLeft())
                    .append(",\"totalSeats\":").append(d.getTotalSeats())
                    .append(",\"registered\":").append(d.getRegistered())
                    .append(",\"required\":").append(d.getRequiredGuests()).append('}');
        }
        sb.append(']');
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.getWriter().write(sb.toString());
    }
}

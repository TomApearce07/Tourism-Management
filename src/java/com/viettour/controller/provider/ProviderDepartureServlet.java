package com.viettour.controller.provider;

import com.viettour.dao.TourDAO;
import com.viettour.dao.TourDepartureDAO;
import com.viettour.model.Account;
import com.viettour.model.Tour;
import com.viettour.model.TourDeparture;
import com.viettour.util.DepartureFormUtil;
import com.viettour.util.InputUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provider quản lý ngày khởi hành của tour của mình (URL /provider/departures, chỉ nhận POST).
 *
 * action = add    : thêm một hoặc nhiều ngày (departureDate[] + departureSeats[])
 *          update : đổi tổng số chỗ của một ngày (không được thấp hơn số chỗ đã giữ/đã bán)
 *          close  : đóng ngày (không bán nữa, vẫn giữ lịch sử đơn)
 *          open   : mở lại ngày đã đóng
 *
 * Thay đổi ngày khởi hành KHÔNG đưa tour về PENDING (chỉ sửa nội dung tour mới cần duyệt lại).
 */
public class ProviderDepartureServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Account account = (Account) request.getSession().getAttribute("account");
        String base = request.getContextPath();
        int tourId = InputUtil.parseInt(request.getParameter("tourId"), -1);
        String action = InputUtil.clean(request.getParameter("action"));

        TourDAO tourDAO = new TourDAO();
        TourDepartureDAO depDAO = new TourDepartureDAO();
        try {
            Tour tour = tourDAO.getTourById(tourId);
            if (tour == null || tour.getProviderId() != account.getAccountId()) {   // chỉ tour của chính mình
                response.sendRedirect(base + "/provider/dashboard");
                return;
            }

            String error = null;
            if ("add".equals(action)) {
                List<TourDeparture> list = new ArrayList<>();
                error = DepartureFormUtil.parse(request, list, true);
                if (error == null) {
                    for (TourDeparture d : list) {
                        if (!depDAO.insert(tourId, d.getDepartureDate(), d.getTotalSeats())) {
                            error = "Không thể thêm ngày " + d.getDepartureDate() + " (có thể đã tồn tại).";
                            break;
                        }
                    }
                }
            } else {
                int depId = InputUtil.parseInt(request.getParameter("departureId"), -1);
                TourDeparture dep = depDAO.getDeparture(depId);
                if (dep == null || dep.getTourId() != tourId) {
                    error = "Không tìm thấy ngày khởi hành!";
                } else if ("update".equals(action)) {
                    int seats = InputUtil.parseInt(request.getParameter("totalSeats"), -1);
                    if (seats < 1 || seats > DepartureFormUtil.MAX_SEATS) {
                        error = "Số chỗ phải từ 1 đến " + DepartureFormUtil.MAX_SEATS + "!";
                    } else if (!depDAO.updateTotalSeats(depId, tourId, seats)) {
                        error = "Không thể giảm số chỗ xuống dưới số chỗ đã giữ/đã bán (" + dep.getSeatsTaken() + ")!";
                    }
                } else if ("close".equals(action)) {
                    depDAO.setStatus(depId, tourId, "CLOSED");
                } else if ("open".equals(action)) {
                    depDAO.setStatus(depId, tourId, "OPEN");
                }
            }

            if (error != null) {
                request.getSession().setAttribute("providerError", error);
            }
            response.sendRedirect(base + "/provider/tour-detail?id=" + tourId + (error == null ? "&dep=ok" : ""));
        } finally {
            tourDAO.close();
            depDAO.close();
        }
    }
}

package com.viettour.controller.manager;

import com.viettour.dao.AccountDAO;
import com.viettour.dao.StatsDAO;
import com.viettour.model.TourStat;
import com.viettour.util.InputUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Manager xem thống kê từng tour (URL /manager/tour-stats trong web.xml).
 * Tham số tùy chọn: providerId, status (APPROVED/PENDING/REJECTED), tourId (xem chi tiết theo ngày khởi hành).
 */
public class ManagerTourStatsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int providerId = InputUtil.parseInt(request.getParameter("providerId"), -1);
        String status = InputUtil.clean(request.getParameter("status")).toUpperCase();
        if (!"APPROVED".equals(status) && !"PENDING".equals(status) && !"REJECTED".equals(status)) {
            status = "";
        }
        int tourId = InputUtil.parseInt(request.getParameter("tourId"), -1);

        StatsDAO statsDAO = new StatsDAO();
        AccountDAO accountDAO = new AccountDAO();
        try {
            List<TourStat> stats = statsDAO.getTourStats(providerId, status);
            int sold = 0;
            BigDecimal revenue = BigDecimal.ZERO;
            BigDecimal cost = BigDecimal.ZERO;
            for (TourStat s : stats) {
                sold += s.getGuestsSold();
                revenue = revenue.add(s.getRevenue());
                cost = cost.add(s.getCost());
            }
            request.setAttribute("tourStats", stats);
            request.setAttribute("totalSold", Integer.valueOf(sold));
            request.setAttribute("totalRevenue", revenue);
            request.setAttribute("totalCost", cost);
            request.setAttribute("totalProfit", revenue.subtract(cost));
            request.setAttribute("providers", accountDAO.getAccountsByRole(2));
            request.setAttribute("selProvider", Integer.valueOf(providerId));
            request.setAttribute("selStatus", status);
            if (tourId > 0) {
                for (TourStat s : stats) {
                    if (s.getTourId() == tourId) {                 // chỉ xem chi tiết tour đang nằm trong danh sách lọc
                        request.setAttribute("detailTour", s);
                        request.setAttribute("departureStats", statsDAO.getDepartureStats(tourId));
                        break;
                    }
                }
            }
        } finally {
            statsDAO.close();
            accountDAO.close();
        }
        request.getRequestDispatcher("/views/manager/tour-stats.jsp").forward(request, response);
    }
}

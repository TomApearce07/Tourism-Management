package com.viettour.controller.manager;

import com.viettour.dao.AccountDAO;
import com.viettour.dao.TourDAO;
import com.viettour.model.Account;
import com.viettour.model.Tour;
import com.viettour.util.InputUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Manager xem chi tiết một tour kèm thông tin nhà cung cấp (URL /manager/tour-detail?id=...). */
public class ManagerTourDetailServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = InputUtil.parseInt(request.getParameter("id"), -1);

        TourDAO tourDAO = new TourDAO();
        AccountDAO accountDAO = new AccountDAO();
        try {
            Tour tour = tourDAO.getTourById(id);
            if (tour == null) {
                response.sendRedirect(request.getContextPath() + "/manager/manage-tours");
                return;
            }
            Account provider = accountDAO.getAccountById(tour.getProviderId());
            request.setAttribute("tour", tour);
            request.setAttribute("provider", provider);
        } finally {
            tourDAO.close();
            accountDAO.close();
        }
        request.getRequestDispatcher("/views/manager/manager-tour-detail.jsp").forward(request, response);
    }
}

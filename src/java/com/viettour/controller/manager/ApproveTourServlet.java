package com.viettour.controller.manager;

import com.viettour.dao.AccountDAO;
import com.viettour.dao.StatsDAO;
import com.viettour.dao.TourDAO;
import com.viettour.model.Account;
import com.viettour.model.Tour;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Manager duyệt Tour và duyệt / khóa Nhà cung cấp.
 * URL /manager/approve-tour, /manager/review-tour, /manager/providers khai báo trong web.xml.
 *
 * Quy tắc nghiệp vụ:
 *  - Có tham số accountId: thao tác trên tài khoản. Manager CHỈ được thao tác với Provider
 *    (RoleID = 2), không thể khóa Admin, Manager khác hoặc Customer.
 *  - Không có accountId: thao tác duyệt tour (approve -> APPROVED, reject -> REJECTED).
 */
public class ApproveTourServlet extends HttpServlet {

    private static final int ROLE_PROVIDER = 2;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        AccountDAO accountDAO = new AccountDAO();
        StatsDAO statsDAO = new StatsDAO();
        try {
            request.setAttribute("providerList", accountDAO.getAccountsByRole(ROLE_PROVIDER));
            request.setAttribute("providerStats", statsDAO.getProviderStats());   // Map<AccountID, ProviderStat>
        } finally {
            accountDAO.close();
            statsDAO.close();
        }
        request.getRequestDispatcher("/views/manager/manage-providers.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accountIdParam = request.getParameter("accountId");
        String action = request.getParameter("action");

        if (accountIdParam != null && !accountIdParam.trim().isEmpty()) {
            handleProviderAction(request, response, accountIdParam.trim(), action);
        } else {
            handleTourAction(request, response, action);
        }
    }

    /** Duyệt / từ chối / khóa / mở khóa một Nhà cung cấp. */
    private void handleProviderAction(HttpServletRequest request, HttpServletResponse response,
            String accountIdParam, String action) throws IOException {

        int accountId;
        try {
            accountId = Integer.parseInt(accountIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/manager/providers");
            return;
        }

        AccountDAO accountDAO = new AccountDAO();
        try {
            Account target = accountDAO.getAccountById(accountId);

            if (target != null && target.getRoleId() == ROLE_PROVIDER) {
                if ("approve".equalsIgnoreCase(action) || "activate".equalsIgnoreCase(action)) {
                    accountDAO.updateAccountStatus(accountId, "ACTIVE");
                } else if ("lock".equalsIgnoreCase(action) || "reject".equalsIgnoreCase(action)) {
                    accountDAO.updateAccountStatus(accountId, "LOCKED");
                }
            }
        } finally {
            accountDAO.close();
        }
        response.sendRedirect(request.getContextPath() + "/manager/providers");
    }

    /** Duyệt hoặc từ chối một Tour. */
    private void handleTourAction(HttpServletRequest request, HttpServletResponse response,
            String action) throws IOException {

        int tourId;
        try {
            tourId = Integer.parseInt(request.getParameter("tourId").trim());
        } catch (RuntimeException e) {          // thiếu tham số hoặc không phải số
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã tour không hợp lệ!");
            return;
        }

        String newStatus = null;
        if ("approve".equalsIgnoreCase(action)) {
            newStatus = "APPROVED";
        } else if ("reject".equalsIgnoreCase(action)) {
            newStatus = "REJECTED";
        }
        if (newStatus == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Hành động không hợp lệ!");
            return;
        }

        TourDAO tourDAO = new TourDAO();
        try {
            Tour tour = tourDAO.getTourById(tourId);
            if (tour == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy tour!");
                return;
            }
            tourDAO.updateApprovalStatus(tourId, newStatus);
        } finally {
            tourDAO.close();
        }
        response.sendRedirect(request.getContextPath() + "/manager/manage-tours?statusUpdated=true");
    }
}

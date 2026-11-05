package com.viettour.controller.manager;

import com.viettour.dao.AccountDAO;
import com.viettour.model.Account;
import com.viettour.util.InputUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Duyệt / từ chối nhà cung cấp (URL /manager/pending-approvals trong web.xml).
 *
 * Bản cũ nhận nguyên giá trị "status" từ form rồi ghi thẳng vào DB, nên gửi status bất kỳ
 * (ví dụ "ADMIN") là tài khoản có trạng thái sai. Bản này chỉ chấp nhận ACTIVE hoặc LOCKED,
 * và chỉ áp dụng cho tài khoản Provider (RoleID = 2).
 */
public class PendingApprovalsServlet extends HttpServlet {

    private static final int ROLE_PROVIDER = 2;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/manager/manage-tours");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int accountId = InputUtil.parseInt(request.getParameter("accountId"), -1);
        String action = InputUtil.clean(request.getParameter("action"));
        String status = InputUtil.clean(request.getParameter("status"));

        String newStatus = null;
        if ("reject".equalsIgnoreCase(action) || "rejected".equalsIgnoreCase(action)
                || "LOCKED".equalsIgnoreCase(status) || "REJECTED".equalsIgnoreCase(status)) {
            newStatus = "LOCKED";
        } else if ("approve".equalsIgnoreCase(action) || "ACTIVE".equalsIgnoreCase(status) || status.isEmpty()) {
            newStatus = "ACTIVE";
        }

        if (newStatus != null) {
            AccountDAO accountDAO = new AccountDAO();
            try {
                Account target = accountDAO.getAccountById(accountId);
                if (target != null && target.getRoleId() == ROLE_PROVIDER) {
                    accountDAO.updateAccountStatus(accountId, newStatus);
                }
            } finally {
                accountDAO.close();
            }
        }
        response.sendRedirect(request.getContextPath() + "/manager/manage-tours");
    }
}

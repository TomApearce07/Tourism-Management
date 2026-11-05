package com.viettour.controller.admin;

import com.viettour.dao.AccountDAO;
import com.viettour.model.Account;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Admin khóa / mở khóa tài khoản (URL /admin/toggle-account trong web.xml, chỉ nhận POST).
 *
 * Quy tắc nghiệp vụ:
 *  - Trạng thái mới được suy ra từ trạng thái HIỆN TẠI trong DB, không tin giá trị
 *    "currentStatus" từ form (form có thể bị sửa).
 *  - ACTIVE -> LOCKED; mọi trạng thái khác (LOCKED, PENDING) -> ACTIVE.
 *  - Admin không thể tự khóa chính mình (chặn ở server, không chỉ ẩn nút trên giao diện).
 */
public class ToggleAccountServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Account admin = (Account) request.getSession().getAttribute("account");

        int targetId;
        try {
            targetId = Integer.parseInt(request.getParameter("accountId").trim());
        } catch (RuntimeException e) {          // thiếu tham số hoặc không phải số
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }

        AccountDAO accountDAO = new AccountDAO();
        try {
            Account target = accountDAO.getAccountById(targetId);

            if (target != null && target.getAccountId() != admin.getAccountId()) {
                String newStatus = "ACTIVE".equalsIgnoreCase(target.getStatus()) ? "LOCKED" : "ACTIVE";
                accountDAO.updateAccountStatus(targetId, newStatus);
            }
        } finally {
            accountDAO.close();
        }
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
}

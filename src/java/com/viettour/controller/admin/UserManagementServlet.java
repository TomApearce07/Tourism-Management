package com.viettour.controller.admin;

import com.viettour.dao.AccountDAO;
import com.viettour.model.Account;
import com.viettour.util.InputUtil;
import com.viettour.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Admin xem danh sách tài khoản và tạo tài khoản Manager (URL /admin/users trong web.xml).
 *
 * Tài khoản Manager mới luôn ở trạng thái ACTIVE, mật khẩu được băm bằng PasswordUtil
 * (cùng cách LoginServlet và RegisterServlet đang dùng).
 */
public class UserManagementServlet extends HttpServlet {

    private static final String USERS_JSP = "/views/admin/user-management.jsp";
    private static final int ROLE_MANAGER = 3;

    private static final String USERNAME_REGEX = "^[A-Za-z0-9_.]{3,50}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_REGEX = "^0[0-9]{9,10}$";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        AccountDAO accountDAO = new AccountDAO();
        try {
            request.setAttribute("userList", accountDAO.getAllAccounts());
        } finally {
            accountDAO.close();
        }
        request.getRequestDispatcher(USERS_JSP).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!"createManager".equalsIgnoreCase(InputUtil.clean(request.getParameter("action")))) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }

        String fullName = InputUtil.clean(request.getParameter("fullName"));
        String username = InputUtil.clean(request.getParameter("username"));
        String email = InputUtil.clean(request.getParameter("email")).toLowerCase();
        String phone = InputUtil.clean(request.getParameter("phone"));
        String password = request.getParameter("password");
        if (password == null) {
            password = "";
        }

        String error = null;
        if (fullName.isEmpty() || fullName.length() > 100) {
            error = "Họ tên không được để trống và tối đa 100 ký tự!";
        } else if (!username.matches(USERNAME_REGEX)) {
            error = "Username gồm 3-50 ký tự: chữ không dấu, số, dấu gạch dưới hoặc dấu chấm!";
        } else if (email.length() > 100 || !email.matches(EMAIL_REGEX)) {
            error = "Địa chỉ email không hợp lệ!";
        } else if (!phone.matches(PHONE_REGEX)) {
            error = "Số điện thoại phải bắt đầu bằng 0 và gồm 10 hoặc 11 chữ số!";
        } else if (password.length() < 6 || password.length() > 100) {
            error = "Mật khẩu phải từ 6 đến 100 ký tự!";
        }

        AccountDAO accountDAO = new AccountDAO();
        try {
            if (error == null && accountDAO.isEmailOrUsernameExist(email, username)) {
                error = "Email hoặc Username (" + email + " / " + username + ") đã tồn tại trên hệ thống!";
            }
            if (error != null) {
                request.setAttribute("errorMsg", error);
                request.setAttribute("userList", accountDAO.getAllAccounts());
                request.getRequestDispatcher(USERS_JSP).forward(request, response);
                return;
            }

            Account manager = new Account();
            manager.setUsername(username);
            manager.setPassword(PasswordUtil.hashPassword(password));
            manager.setFullName(fullName);
            manager.setEmail(email);
            manager.setPhone(phone);
            manager.setRoleId(ROLE_MANAGER);
            manager.setStatus("ACTIVE");

            if (!accountDAO.register(manager)) {
                request.setAttribute("errorMsg", "Không thể tạo tài khoản lúc này. Vui lòng thử lại!");
                request.setAttribute("userList", accountDAO.getAllAccounts());
                request.getRequestDispatcher(USERS_JSP).forward(request, response);
                return;
            }
        } finally {
            accountDAO.close();
        }
        response.sendRedirect(request.getContextPath() + "/admin/users?created=true");
    }
}

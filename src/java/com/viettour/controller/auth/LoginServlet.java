package com.viettour.controller.auth;

import com.viettour.dao.AccountDAO;
import com.viettour.model.Account;
import com.viettour.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Đăng nhập (URL /login khai báo trong web.xml, không dùng annotation).
 * Form gửi lên: "email" (có thể là email hoặc username) và "password".
 */
public class LoginServlet extends HttpServlet {

    private static final String LOGIN_JSP = "/views/auth/login.jsp";

    private static final int ROLE_PROVIDER = 2;
    private static final int ROLE_MANAGER = 3;
    private static final int ROLE_ADMIN = 4;

    // Thời gian chờ của session: 30 phút
    private static final int SESSION_TIMEOUT_SECONDS = 30 * 60;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Đã đăng nhập rồi thì không hiện lại form, chuyển thẳng tới trang của vai trò
        HttpSession session = request.getSession(false);
        if (session != null) {
            Account current = (Account) session.getAttribute("account");
            if (current != null && "ACTIVE".equalsIgnoreCase(current.getStatus())) {
                redirectByRole(request, response, current);
                return;
            }
        }
        request.getRequestDispatcher(LOGIN_JSP).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String identifier = request.getParameter("email");
        String password = request.getParameter("password");
        identifier = (identifier == null) ? "" : identifier.trim();
        password = (password == null) ? "" : password;

        if (identifier.isEmpty() || password.isEmpty()) {
            showError(request, response, "Vui lòng nhập đầy đủ Email/Tên đăng nhập và Mật khẩu!");
            return;
        }

        AccountDAO accountDAO = new AccountDAO();
        Account account;
        try {
            account = accountDAO.login(identifier, PasswordUtil.hashPassword(password));   // DB lưu mã băm SHA-256
        } finally {
            accountDAO.close();     // đóng kết nối DB sau khi dùng xong
        }

        if (account == null) {
            showError(request, response, "Email hoặc mật khẩu không chính xác!");
            return;
        }
        if ("LOCKED".equalsIgnoreCase(account.getStatus())) {
            showError(request, response, "Tài khoản của bạn đã bị khóa bởi Quản trị viên!");
            return;
        }
        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            // Trạng thái PENDING: Provider chưa được Manager duyệt
            showError(request, response, "Tài khoản của bạn đang chờ phê duyệt hoặc chưa kích hoạt!");
            return;
        }

        // Hủy session cũ rồi tạo session mới để tránh tấn công cố định session (session fixation)
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);

        // Không giữ mật khẩu trong session
        account.setPassword(null);
        session.setAttribute("account", account);

        redirectByRole(request, response, account);
    }

    /** Admin -> quản lý user, Manager -> tổng quan, Provider -> dashboard, Customer -> trang chủ. */
    private void redirectByRole(HttpServletRequest request, HttpServletResponse response, Account account)
            throws IOException {
        String base = request.getContextPath();
        switch (account.getRoleId()) {
            case ROLE_ADMIN:
                response.sendRedirect(base + "/admin/users");
                break;
            case ROLE_MANAGER:
                response.sendRedirect(base + "/manager/overview");
                break;
            case ROLE_PROVIDER:
                response.sendRedirect(base + "/provider/dashboard");
                break;
            default:
                response.sendRedirect(base + "/home");
                break;
        }
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher(LOGIN_JSP).forward(request, response);
    }
}

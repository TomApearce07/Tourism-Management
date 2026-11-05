package com.viettour.filter;

import com.viettour.dao.AccountDAO;
import com.viettour.model.Account;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Phân quyền theo vai trò (khai báo trong web.xml cho /admin/*, /manager/*, /provider/*, /customer/*).
 *
 *  - /admin/*    : chỉ Admin (RoleID = 4)
 *  - /manager/*  : Manager (3) hoặc Admin (4)
 *  - /provider/* : chỉ Provider (2)
 *  - /customer/* : mọi tài khoản đã đăng nhập
 *
 * Điểm khác bản cũ:
 *  - Mỗi request đọc lại tài khoản từ DB, nên khi Admin khóa tài khoản hoặc đổi vai trò thì có hiệu lực
 *    ngay, không phải chờ session hết hạn.
 *  - Phân quyền dựa trên servletPath (không dùng contains trên toàn bộ URI).
 */
public class AuthFilter implements Filter {

    private static final int ROLE_PROVIDER = 2;
    private static final int ROLE_MANAGER = 3;
    private static final int ROLE_ADMIN = 4;

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        Account sessionAccount = (session == null) ? null : (Account) session.getAttribute("account");
        if (sessionAccount == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Đọc lại từ DB để biết trạng thái và vai trò mới nhất
        Account account;
        AccountDAO accountDAO = new AccountDAO();
        try {
            account = accountDAO.getAccountById(sessionAccount.getAccountId());
        } finally {
            accountDAO.close();
        }
        if (account == null || !"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            session.invalidate();                         // bị khóa hoặc đã bị xóa: buộc đăng xuất
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        account.setPassword(null);                        // không giữ mật khẩu trong session
        session.setAttribute("account", account);

        String path = request.getServletPath();
        int role = account.getRoleId();

        if (path.startsWith("/admin/") && role != ROLE_ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ Quản trị viên hệ thống mới có quyền truy cập!");
            return;
        }
        if (path.startsWith("/manager/") && role != ROLE_MANAGER && role != ROLE_ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Khu vực dành riêng cho Quản lý vận hành!");
            return;
        }
        if (path.startsWith("/provider/") && role != ROLE_PROVIDER) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Khu vực dành riêng cho Nhà cung cấp Tour!");
            return;
        }
        chain.doFilter(request, response);
    }
}

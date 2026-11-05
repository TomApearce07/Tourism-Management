package com.viettour.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

/**
 * Chặn tấn công CSRF mà không cần sửa từng form: với các yêu cầu thay đổi dữ liệu (POST, PUT, DELETE),
 * nếu trình duyệt gửi kèm Origin (hoặc Referer) thì địa chỉ đó phải trùng với máy chủ đang phục vụ.
 * Trang web của kẻ tấn công không thể làm trình duyệt gửi Origin giả.
 *
 * Yêu cầu không có cả Origin lẫn Referer (công cụ dòng lệnh, máy chủ gọi máy chủ) vẫn được chấp nhận.
 */
public class SameOriginFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (isStateChanging(request.getMethod()) && !isSameOrigin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Yêu cầu bị từ chối vì xuất phát từ trang web khác.");
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isStateChanging(String method) {
        return "POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method);
    }

    static boolean isSameOrigin(HttpServletRequest request) {
        String host = request.getHeader("Host");
        String origin = request.getHeader("Origin");
        String source = (origin != null) ? origin : request.getHeader("Referer");

        if (source == null) {
            return true;                    // không phải yêu cầu từ trình duyệt
        }
        if (host == null) {
            return false;
        }
        try {
            String authority = new URI(source).getAuthority();
            return authority != null && authority.equalsIgnoreCase(host);
        } catch (java.net.URISyntaxException e) {
            return false;                   // kể cả Origin: null
        }
    }
}

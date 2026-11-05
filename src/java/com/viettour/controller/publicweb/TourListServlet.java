package com.viettour.controller.publicweb;

import com.viettour.dao.TourDAO;
import com.viettour.util.InputUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Danh sách tour, có tìm kiếm theo từ khóa (URL /tours trong web.xml). */
public class TourListServlet extends HttpServlet {

    private static final int MAX_KEYWORD_LENGTH = 100;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = InputUtil.clean(request.getParameter("keyword"));
        if (keyword.length() > MAX_KEYWORD_LENGTH) {
            keyword = keyword.substring(0, MAX_KEYWORD_LENGTH);
        }

        TourDAO tourDAO = new TourDAO();
        try {
            if (keyword.isEmpty()) {
                request.setAttribute("tourList", tourDAO.getApprovedTours());
            } else {
                request.setAttribute("tourList", tourDAO.searchApprovedTours(keyword));
            }
        } finally {
            tourDAO.close();
        }
        request.getRequestDispatcher("/views/public/tour-list.jsp").forward(request, response);
    }
}

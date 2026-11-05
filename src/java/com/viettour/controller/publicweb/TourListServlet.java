package com.viettour.controller.publicweb;

import com.viettour.dao.TourDAO;
import com.viettour.dao.TourDepartureDAO;
import com.viettour.model.Tour;
import com.viettour.util.InputUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Danh sách tour (URL /tours trong web.xml), tìm theo chữ (keyword) và/hoặc ngày khởi hành (date = yyyy-MM-dd).
 * Ngày sai định dạng hoặc ở quá khứ thì bỏ lọc ngày và báo "Ngày không hợp lệ".
 */
public class TourListServlet extends HttpServlet {

    private static final int MAX_KEYWORD_LENGTH = 100;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = InputUtil.clean(request.getParameter("keyword"));
        if (keyword.length() > MAX_KEYWORD_LENGTH) {
            keyword = keyword.substring(0, MAX_KEYWORD_LENGTH);
        }

        LocalDate date = null;
        String dateText = InputUtil.clean(request.getParameter("date"));
        if (!dateText.isEmpty()) {
            try {
                date = LocalDate.parse(dateText);
                if (date.isBefore(LocalDate.now())) {
                    date = null;
                    request.setAttribute("dateError", "Ngày khởi hành phải từ hôm nay trở đi.");
                }
            } catch (DateTimeParseException e) {
                request.setAttribute("dateError", "Ngày không hợp lệ.");
            }
        }

        TourDAO tourDAO = new TourDAO();
        TourDepartureDAO depDAO = new TourDepartureDAO();
        try {
            List<Tour> tours = tourDAO.searchTours(keyword, date);
            depDAO.attachDepartures(tours, date);       // ngày khởi hành + số chỗ từng ngày
            request.setAttribute("tourList", tours);
        } finally {
            tourDAO.close();
            depDAO.close();
        }
        request.setAttribute("keyword", keyword);
        request.setAttribute("dateValue", date == null ? "" : date.toString());
        request.getRequestDispatcher("/views/public/tour-list.jsp").forward(request, response);
    }
}

package com.viettour.controller.provider;

import com.viettour.dao.BookingDAO;
import com.viettour.dao.TourDAO;
import com.viettour.dao.TourDepartureDAO;
import com.viettour.model.Account;
import com.viettour.model.Tour;
import com.viettour.model.TourDeparture;
import com.viettour.util.DepartureFormUtil;
import com.viettour.util.InputUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provider quản lý ngày khởi hành của tour của mình (URL /provider/departures, chỉ nhận POST).
 *
 * action = add    : thêm một hoặc nhiều ngày (departureDate[] + departureSeats[])
 *          update : đổi tổng số chỗ của một ngày (không được thấp hơn số chỗ đã giữ/đã bán)
 *          close  : đóng ngày (không bán nữa, vẫn giữ lịch sử đơn)
 *          open   : mở lại ngày đã đóng
 *          cancel : HỦY MỘT ngày khởi hành (không có hủy cả tour) (bắt buộc nhập lý do): hoàn tiền ngay + thông báo lý do cho mọi khách đã đặt
 *          approveAll: xác nhận ngày đủ người -> mọi đơn chờ duyệt thành PAID (chỉ khi tới hạn quyết định và đủ % tối thiểu)
 *
 * Thay đổi ngày khởi hành KHÔNG đưa tour về PENDING (chỉ sửa nội dung tour mới cần duyệt lại).
 */
public class ProviderDepartureServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Account account = (Account) request.getSession().getAttribute("account");
        String base = request.getContextPath();
        int tourId = InputUtil.parseInt(request.getParameter("tourId"), -1);
        String action = InputUtil.clean(request.getParameter("action"));

        TourDAO tourDAO = new TourDAO();
        TourDepartureDAO depDAO = new TourDepartureDAO();
        BookingDAO bookingDAO = new BookingDAO();
        String okFlag = "ok";
        try {
            Tour tour = tourDAO.getTourById(tourId);
            if (tour == null || tour.getProviderId() != account.getAccountId()) {   // chỉ tour của chính mình
                response.sendRedirect(base + "/provider/dashboard");
                return;
            }

            String error = null;
            if ("add".equals(action)) {
                List<TourDeparture> list = new ArrayList<>();
                error = DepartureFormUtil.parse(request, list, true);
                if (error == null) {
                    for (TourDeparture d : list) {
                        if (!depDAO.insert(tourId, d.getDepartureDate(), d.getTotalSeats())) {
                            error = "Không thể thêm ngày " + d.getDepartureDate() + " (có thể đã tồn tại).";
                            break;
                        }
                    }
                }
            } else {
                int depId = InputUtil.parseInt(request.getParameter("departureId"), -1);
                TourDeparture dep = depDAO.getDeparture(depId);
                if (dep == null || dep.getTourId() != tourId) {
                    error = "Không tìm thấy ngày khởi hành!";
                } else if ("CANCELLED".equals(dep.getStatus())) {
                    error = "Ngày khởi hành này đã bị hủy, không thể thay đổi.";
                } else if ("update".equals(action)) {
                    int seats = InputUtil.parseInt(request.getParameter("totalSeats"), -1);
                    if (seats < 1 || seats > DepartureFormUtil.MAX_SEATS) {
                        error = "Số chỗ phải từ 1 đến " + DepartureFormUtil.MAX_SEATS + "!";
                    } else if (!depDAO.updateTotalSeats(depId, tourId, seats)) {
                        error = "Không thể giảm số chỗ xuống dưới số chỗ đã giữ/đã bán (" + dep.getSeatsTaken() + ")!";
                    }
                } else if ("cancel".equals(action)) {
                    String reason = InputUtil.clean(request.getParameter("reason"));
                    if (reason.length() < 5 || reason.length() > 400) {
                        error = "Vui lòng nhập lý do ngừng cung cấp (5-400 ký tự)!";
                    } else {
                        int n = bookingDAO.cancelDeparture(depId, tourId, reason);
                        if (n < 0) {
                            error = "Không thể hủy ngày khởi hành này (đã bị hủy hoặc lỗi hệ thống).";
                        } else {
                            okFlag = "cancelled&n=" + n;
                        }
                    }
                } else if ("approveAll".equals(action)) {
                    if (!dep.isDecisionOpen()) {
                        error = "Chỉ được xác nhận từ " + dep.getDecisionDateText() + " (" + TourDeparture.DECISION_DAYS + " ngày trước khởi hành).";
                    } else if (!dep.isThresholdMet()) {
                        error = "Mới có " + dep.getRegistered() + "/" + dep.getRequiredGuests() + " khách tối thiểu, chưa đủ điều kiện khởi hành.";
                    } else if ("CANCELLED".equals(dep.getStatus())) {
                        error = "Ngày khởi hành đã bị hủy.";
                    } else {
                        int n = bookingDAO.approveDeparture(depId);
                        okFlag = n < 0 ? "ok" : "approved&n=" + n;
                        if (n < 0) {
                            error = "Không thể xác nhận lúc này, vui lòng thử lại!";
                        }
                    }
                } else if ("close".equals(action)) {
                    depDAO.setStatus(depId, tourId, "CLOSED");
                } else if ("open".equals(action)) {
                    depDAO.setStatus(depId, tourId, "OPEN");
                }
            }

            if (error != null) {
                request.getSession().setAttribute("providerError", error);
            }
            response.sendRedirect(base + "/provider/tour-detail?id=" + tourId + (error == null ? "&dep=" + okFlag : ""));
        } finally {
            tourDAO.close();
            depDAO.close();
            bookingDAO.close();
        }
    }
}

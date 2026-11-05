package com.viettour.util;

import com.viettour.model.TourDeparture;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Đọc và kiểm tra các dòng "ngày khởi hành + số chỗ" (departureDate[] / departureSeats[]) do Provider nhập. */
public final class DepartureFormUtil {

    public static final int MAX_SEATS = 100000;
    private static final int MAX_ROWS = 60;

    private DepartureFormUtil() {
    }

    /**
     * @param required true: phải có ít nhất 1 ngày (dùng khi tạo tour)
     * @return thông báo lỗi, hoặc null nếu hợp lệ (các dòng hợp lệ được thêm vào out)
     */
    public static String parse(HttpServletRequest request, List<TourDeparture> out, boolean required) {
        String[] dates = request.getParameterValues("departureDate");
        String[] seats = request.getParameterValues("departureSeats");
        if (dates == null) {
            dates = new String[0];
        }
        if (seats == null) {
            seats = new String[0];
        }
        if (dates.length != seats.length) {
            return "Dữ liệu ngày khởi hành không hợp lệ!";
        }
        if (dates.length > MAX_ROWS) {
            return "Mỗi lần chỉ nhập tối đa " + MAX_ROWS + " ngày khởi hành!";
        }
        LocalDate today = LocalDate.now();
        Set<LocalDate> seen = new HashSet<>();
        for (int i = 0; i < dates.length; i++) {
            String d = InputUtil.clean(dates[i]);
            String s = InputUtil.clean(seats[i]);
            if (d.isEmpty() && s.isEmpty()) {
                continue;                       // dòng trống: bỏ qua
            }
            LocalDate date;
            try {
                date = LocalDate.parse(d);      // yyyy-MM-dd (giá trị của <input type="date">)
            } catch (DateTimeParseException e) {
                return "Ngày khởi hành không hợp lệ (dòng " + (i + 1) + ")!";
            }
            if (date.isBefore(today)) {
                return "Ngày khởi hành phải từ hôm nay trở đi (dòng " + (i + 1) + ")!";
            }
            if (!seen.add(date)) {
                return "Ngày khởi hành bị trùng: " + date + "!";
            }
            int n = InputUtil.parseInt(s, -1);
            if (n < 1 || n > MAX_SEATS) {
                return "Số chỗ mỗi ngày phải từ 1 đến " + MAX_SEATS + " (dòng " + (i + 1) + ")!";
            }
            TourDeparture dep = new TourDeparture();
            dep.setDepartureDate(Date.valueOf(date));
            dep.setTotalSeats(n);
            out.add(dep);
        }
        if (required && out.isEmpty()) {
            return "Vui lòng nhập ít nhất một ngày khởi hành và số chỗ!";
        }
        return null;
    }
}

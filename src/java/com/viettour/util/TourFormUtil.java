package com.viettour.util;

import com.viettour.model.Tour;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/** Đọc và kiểm tra form tạo / sửa tour của Provider (dùng chung cho CreateTourServlet và ProviderTourDetailServlet). */
public final class TourFormUtil {

    private static final int MAX_SEATS = 100000;

    private TourFormUtil() {
    }

    /**
     * Đọc các trường văn bản và số của form vào đối tượng Tour.
     *
     * @param minSeats số chỗ tối thiểu hợp lệ (tạo mới: 1; chỉnh sửa: 0 để tạm đóng bán)
     * @return thông báo lỗi nếu dữ liệu sai, hoặc null nếu hợp lệ
     */
    public static String populate(HttpServletRequest request, Tour tour, int minSeats) {
        String tourName = InputUtil.clean(request.getParameter("tourName"));
        String location = InputUtil.clean(request.getParameter("location"));
        String duration = InputUtil.clean(request.getParameter("duration"));
        String itinerary = InputUtil.clean(request.getParameter("itinerary"));

        if (tourName.isEmpty() || tourName.length() > 200) {
            return "Tên tour không được để trống và tối đa 200 ký tự!";
        }
        if (location.isEmpty() || location.length() > 100) {
            return "Điểm đến không được để trống và tối đa 100 ký tự!";
        }
        if (duration.isEmpty() || duration.length() > 50) {
            return "Thời lượng không được để trống và tối đa 50 ký tự!";
        }
        if (itinerary.isEmpty() || itinerary.length() > 10000) {
            return "Lịch trình không được để trống và tối đa 10.000 ký tự!";
        }

        BigDecimal price = InputUtil.parseMoney(request.getParameter("price"));
        BigDecimal transport = InputUtil.parseMoney(request.getParameter("transportCost"));
        BigDecimal hotel = InputUtil.parseMoney(request.getParameter("hotelCost"));
        BigDecimal meal = InputUtil.parseMoney(request.getParameter("mealGuideCost"));
        if (price == null || price.signum() <= 0) {
            return "Giá bán phải là số lớn hơn 0!";
        }
        if (transport == null || hotel == null || meal == null) {
            return "Các khoản chi phí phải là số không âm!";
        }
        if (price.compareTo(transport.add(hotel).add(meal)) < 0) {
            return "Giá bán phải lớn hơn hoặc bằng tổng chi phí (vận chuyển + khách sạn + ăn uống/hướng dẫn)!";
        }

        int seats = InputUtil.parseInt(request.getParameter("availableSeats"), -1);
        if (seats < minSeats || seats > MAX_SEATS) {
            return "Số chỗ phải từ " + minSeats + " đến " + MAX_SEATS + "!";
        }

        tour.setTourName(tourName);
        tour.setLocation(location);
        tour.setDuration(duration);
        tour.setItinerary(itinerary);
        tour.setPrice(price);
        tour.setAvailableSeats(seats);
        tour.setTransportCost(transport);
        tour.setHotelCost(hotel);
        tour.setMealGuideCost(meal);
        return null;
    }
}

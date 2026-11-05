package com.viettour.model;

import java.sql.Date;

/** Một ngày khởi hành của tour (bảng TourDepartures + số chỗ còn lại lấy từ view vw_DepartureSeats). */
public class TourDeparture {

    private int departureId;
    private int tourId;
    private Date departureDate;
    private int totalSeats;
    private int seatsLeft;
    private String status;
    private int registered;          // số khách đã đăng ký thực tế = đơn WAITING_APPROVAL + PAID
    private int minFillPercent;      // % tối thiểu (lấy từ tour)
    private int durationDays = 1;    // số ngày của tour (để tính ngày về)

    /** Provider phải quyết định (duyệt / hủy) trong vòng số ngày này trước ngày khởi hành. */
    public static final int DECISION_DAYS = 2;

    public TourDeparture() {
    }

    public int getDepartureId() {
        return departureId;
    }

    public void setDepartureId(int departureId) {
        this.departureId = departureId;
    }

    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getSeatsLeft() {
        return seatsLeft;
    }

    public void setSeatsLeft(int seatsLeft) {
        this.seatsLeft = seatsLeft;
    }

    /** Số chỗ đã giữ/đã bán = tổng chỗ - chỗ còn lại. */
    public int getSeatsTaken() {
        return totalSeats - seatsLeft;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /** Ngày khởi hành dạng dd/MM/yyyy để hiển thị trên JSP (rỗng nếu chưa có). */
    public String getDateText() {
        return getDepartureDate() == null ? "" : getDepartureDate().toLocalDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public int getRegistered() {
        return registered;
    }

    public void setRegistered(int registered) {
        this.registered = registered;
    }

    public int getMinFillPercent() {
        return minFillPercent;
    }

    public void setMinFillPercent(int minFillPercent) {
        this.minFillPercent = minFillPercent;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    /** Số khách tối thiểu cần có = làm tròn lên (tổng chỗ x %). */
    public int getRequiredGuests() {
        return (int) Math.ceil(totalSeats * minFillPercent / 100.0);
    }

    public boolean isThresholdMet() {
        return registered >= getRequiredGuests();
    }

    /** Ngày bắt đầu được quyết định = ngày khởi hành - DECISION_DAYS. */
    public String getDecisionDateText() {
        return departureDate == null ? "" : departureDate.toLocalDate().minusDays(DECISION_DAYS)
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public boolean isDecisionOpen() {
        return departureDate != null
                && !java.time.LocalDate.now().isBefore(departureDate.toLocalDate().minusDays(DECISION_DAYS));
    }

    /** "06/11 – 08/11" cho tour nhiều ngày, hoặc "06/11/2026" nếu chỉ 1 ngày. */
    public String getRangeText() {
        if (departureDate == null) {
            return "";
        }
        java.time.LocalDate start = departureDate.toLocalDate();
        if (durationDays <= 1) {
            return start.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        java.time.format.DateTimeFormatter f = java.time.format.DateTimeFormatter.ofPattern("dd/MM");
        return start.format(f) + " – " + start.plusDays(durationDays - 1).format(f);
    }
}

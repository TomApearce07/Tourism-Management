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
}

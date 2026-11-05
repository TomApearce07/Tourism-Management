package com.viettour.model;

import java.math.BigDecimal;

/** Số liệu thống kê (chỉ đọc) cho Manager. */
public class DepartureStat {

    private String dateText;
    private int totalSeats;
    private int seatsLeft;
    private int sold;
    private String status;

    public String getDateText() {
        return dateText;
    }

    public void setDateText(String dateText) {
        this.dateText = dateText;
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

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /** Tỷ lệ lấp đầy (%) = (tổng chỗ - chỗ còn lại) / tổng chỗ. */
    public double getFillRate() {
        return totalSeats <= 0 ? 0 : Math.round(1000.0 * (totalSeats - seatsLeft) / totalSeats) / 10.0;
    }
}

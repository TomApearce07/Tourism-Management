package com.viettour.model;

import java.math.BigDecimal;

/** Số liệu thống kê (chỉ đọc) cho Manager. */
public class TourStat {

    private int tourId;
    private String tourName;
    private String providerName;
    private String approvalStatus;
    private int guestsSold;
    private int guestsWaiting;
    private int cancelledOrders;
    private BigDecimal revenue;
    private BigDecimal cost;

    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public int getGuestsSold() {
        return guestsSold;
    }

    public void setGuestsSold(int guestsSold) {
        this.guestsSold = guestsSold;
    }

    public int getGuestsWaiting() {
        return guestsWaiting;
    }

    public void setGuestsWaiting(int guestsWaiting) {
        this.guestsWaiting = guestsWaiting;
    }

    public int getCancelledOrders() {
        return cancelledOrders;
    }

    public void setCancelledOrders(int cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    /** Lợi nhuận = doanh thu - chi phí (chi phí tính theo giá vốn hiện tại của tour). */
    public BigDecimal getProfit() {
        return revenue.subtract(cost);
    }
}

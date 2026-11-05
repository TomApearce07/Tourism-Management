package com.viettour.model;

import java.math.BigDecimal;

/** Số liệu thống kê (chỉ đọc) cho Manager. */
public class ProviderStat {

    private int providerId;
    private int totalTours;
    private int approvedTours;
    private int guestsSold;
    private int paidOrders;
    private int waitingOrders;
    private BigDecimal revenue;

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public int getTotalTours() {
        return totalTours;
    }

    public void setTotalTours(int totalTours) {
        this.totalTours = totalTours;
    }

    public int getApprovedTours() {
        return approvedTours;
    }

    public void setApprovedTours(int approvedTours) {
        this.approvedTours = approvedTours;
    }

    public int getGuestsSold() {
        return guestsSold;
    }

    public void setGuestsSold(int guestsSold) {
        this.guestsSold = guestsSold;
    }

    public int getPaidOrders() {
        return paidOrders;
    }

    public void setPaidOrders(int paidOrders) {
        this.paidOrders = paidOrders;
    }

    public int getWaitingOrders() {
        return waitingOrders;
    }

    public void setWaitingOrders(int waitingOrders) {
        this.waitingOrders = waitingOrders;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}

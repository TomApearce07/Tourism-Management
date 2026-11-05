package com.viettour.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Tour {

    private int tourId;
    private String tourName;
    private String location;
    private String duration;
    private BigDecimal price;
    private int availableSeats;
    private String itinerary;
    private String imageUrl;
    private int providerId;
    private String approvalStatus;
    private Timestamp createdAt;
    private String providerName;
    private BigDecimal transportCost = BigDecimal.ZERO;
    private BigDecimal hotelCost = BigDecimal.ZERO;
    private BigDecimal mealGuideCost = BigDecimal.ZERO;
    private int minFillPercent;                                  // % chỗ tối thiểu phải có người đăng ký thì ngày khởi hành mới chạy
    private List<TourDeparture> departures = new ArrayList<>();   // ngày khởi hành còn chỗ (chỉ nạp ở trang danh sách)

    public Tour() {
    }

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getItinerary() {
        return itinerary;
    }

    public void setItinerary(String itinerary) {
        this.itinerary = itinerary;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public BigDecimal getTransportCost() {
        return transportCost != null ? transportCost : BigDecimal.ZERO;
    }

    public void setTransportCost(BigDecimal transportCost) {
        this.transportCost = transportCost;
    }

    public BigDecimal getHotelCost() {
        return hotelCost != null ? hotelCost : BigDecimal.ZERO;
    }

    public void setHotelCost(BigDecimal hotelCost) {
        this.hotelCost = hotelCost;
    }

    public BigDecimal getMealGuideCost() {
        return mealGuideCost != null ? mealGuideCost : BigDecimal.ZERO;
    }

    public void setMealGuideCost(BigDecimal mealGuideCost) {
        this.mealGuideCost = mealGuideCost;
    }

    public BigDecimal getUnitCost() {
        return getTransportCost().add(getHotelCost()).add(getMealGuideCost());
    }

    public BigDecimal getUnitProfit() {
        if (price == null) {
            return BigDecimal.ZERO;
        }
        return price.subtract(getUnitCost());
    }

    public int getMinFillPercent() {
        return minFillPercent;
    }

    public void setMinFillPercent(int minFillPercent) {
        this.minFillPercent = minFillPercent;
    }

    public List<TourDeparture> getDepartures() {
        return departures;
    }

    public void setDepartures(List<TourDeparture> departures) {
        this.departures = departures;
    }

    /** Số ngày của tour, đọc từ chuỗi thời lượng ("3 ngày 2 đêm" -> 3). Không đọc được thì coi là 1. */
    public int getDurationDays() {
        return parseDurationDays(duration);
    }

    public static int parseDurationDays(String duration) {
        if (duration != null) {
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)\\s*ng[àa]y", java.util.regex.Pattern.CASE_INSENSITIVE).matcher(duration);
            if (m.find()) {
                int d = Integer.parseInt(m.group(1));
                return Math.max(1, Math.min(d, 60));
            }
        }
        return 1;
    }
}

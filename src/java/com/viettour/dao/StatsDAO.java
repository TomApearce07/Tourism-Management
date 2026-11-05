package com.viettour.dao;

import com.viettour.model.DepartureStat;
import com.viettour.model.ProviderStat;
import com.viettour.model.TourStat;
import com.viettour.util.DBContext;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Thống kê cho Manager. "Đã bán" = đơn PAID (Provider đã xác nhận);
 * đơn WAITING_APPROVAL được tính riêng là "chờ duyệt".
 */
public class StatsDAO extends DBContext {

    /** Thống kê theo từng Provider, khóa là AccountID. Provider chưa có tour/đơn vẫn có dòng (toàn số 0). */
    public Map<Integer, ProviderStat> getProviderStats() {
        Map<Integer, ProviderStat> map = new LinkedHashMap<>();
        String sql = "SELECT p.AccountID, "
                + "COUNT(DISTINCT t.TourID) AS TotalTours, "
                + "COUNT(DISTINCT CASE WHEN t.ApprovalStatus='APPROVED' THEN t.TourID END) AS ApprovedTours, "
                + "ISNULL(SUM(CASE WHEN b.PaymentStatus='PAID' THEN b.GuestsCount END),0) AS GuestsSold, "
                + "COUNT(CASE WHEN b.PaymentStatus='PAID' THEN 1 END) AS PaidOrders, "
                + "COUNT(CASE WHEN b.PaymentStatus='WAITING_APPROVAL' THEN 1 END) AS WaitingOrders, "
                + "ISNULL(SUM(CASE WHEN b.PaymentStatus='PAID' THEN b.TotalAmount END),0) AS Revenue "
                + "FROM Accounts p LEFT JOIN Tours t ON t.ProviderID = p.AccountID "
                + "LEFT JOIN Bookings b ON b.TourID = t.TourID "
                + "WHERE p.RoleID = 2 GROUP BY p.AccountID";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ProviderStat s = new ProviderStat();
                s.setProviderId(rs.getInt("AccountID"));
                s.setTotalTours(rs.getInt("TotalTours"));
                s.setApprovedTours(rs.getInt("ApprovedTours"));
                s.setGuestsSold(rs.getInt("GuestsSold"));
                s.setPaidOrders(rs.getInt("PaidOrders"));
                s.setWaitingOrders(rs.getInt("WaitingOrders"));
                s.setRevenue(rs.getBigDecimal("Revenue"));
                map.put(s.getProviderId(), s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    /** Thống kê theo tour; providerId <= 0 và status rỗng = không lọc. */
    public List<TourStat> getTourStats(int providerId, String status) {
        List<TourStat> list = new ArrayList<>();
        String sql = "SELECT t.TourID, t.TourName, a.FullName AS ProviderName, t.ApprovalStatus, "
                + "ISNULL(SUM(CASE WHEN b.PaymentStatus='PAID' THEN b.GuestsCount END),0) AS GuestsSold, "
                + "ISNULL(SUM(CASE WHEN b.PaymentStatus='WAITING_APPROVAL' THEN b.GuestsCount END),0) AS GuestsWaiting, "
                + "COUNT(CASE WHEN b.PaymentStatus='REFUNDED' THEN 1 END) AS CancelledOrders, "
                + "ISNULL(SUM(CASE WHEN b.PaymentStatus='PAID' THEN b.TotalAmount END),0) AS Revenue, "
                + "ISNULL(SUM(CASE WHEN b.PaymentStatus='PAID' THEN b.GuestsCount END),0) "
                + "  * (t.TransportCost + t.HotelCost + t.MealGuideCost) AS Cost "
                + "FROM Tours t JOIN Accounts a ON a.AccountID = t.ProviderID "
                + "LEFT JOIN Bookings b ON b.TourID = t.TourID "
                + "WHERE (? <= 0 OR t.ProviderID = ?) AND (? = '' OR t.ApprovalStatus = ?) "
                + "GROUP BY t.TourID, t.TourName, a.FullName, t.ApprovalStatus, t.TransportCost, t.HotelCost, t.MealGuideCost "
                + "ORDER BY Revenue DESC, t.TourID DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, providerId);
            ps.setInt(2, providerId);
            ps.setString(3, status);
            ps.setString(4, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TourStat s = new TourStat();
                    s.setTourId(rs.getInt("TourID"));
                    s.setTourName(rs.getString("TourName"));
                    s.setProviderName(rs.getString("ProviderName"));
                    s.setApprovalStatus(rs.getString("ApprovalStatus"));
                    s.setGuestsSold(rs.getInt("GuestsSold"));
                    s.setGuestsWaiting(rs.getInt("GuestsWaiting"));
                    s.setCancelledOrders(rs.getInt("CancelledOrders"));
                    BigDecimal rev = rs.getBigDecimal("Revenue");
                    BigDecimal cost = rs.getBigDecimal("Cost");
                    s.setRevenue(rev == null ? BigDecimal.ZERO : rev);
                    s.setCost(cost == null ? BigDecimal.ZERO : cost);
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Thống kê từng ngày khởi hành của một tour (đã bán, chỗ còn lại, tỷ lệ lấp đầy). */
    public List<DepartureStat> getDepartureStats(int tourId) {
        List<DepartureStat> list = new ArrayList<>();
        String sql = "SELECT v.DepartureDate, v.TotalSeats, v.SeatsLeft, v.Status, "
                + "ISNULL(SUM(CASE WHEN b.PaymentStatus='PAID' THEN b.GuestsCount END),0) AS Sold "
                + "FROM vw_DepartureSeats v LEFT JOIN Bookings b ON b.DepartureID = v.DepartureID "
                + "WHERE v.TourID = ? "
                + "GROUP BY v.DepartureDate, v.TotalSeats, v.SeatsLeft, v.Status ORDER BY v.DepartureDate";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tourId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DepartureStat d = new DepartureStat();
                    d.setDateText(rs.getDate("DepartureDate").toLocalDate()
                            .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    d.setTotalSeats(rs.getInt("TotalSeats"));
                    d.setSeatsLeft(Math.max(0, rs.getInt("SeatsLeft")));
                    d.setSold(rs.getInt("Sold"));
                    d.setStatus(rs.getString("Status"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
        }
    }
}

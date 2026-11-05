package com.viettour.dao;

import com.viettour.model.Tour;
import com.viettour.model.TourDeparture;
import com.viettour.util.DBContext;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ngày khởi hành của tour. Số chỗ còn lại luôn đọc từ view vw_DepartureSeats
 * (tính từ các đơn đặt) nên không bao giờ lệch với dữ liệu đơn hàng.
 */
public class TourDepartureDAO extends DBContext {

    /** Kèm số khách đã đăng ký thực tế (WAITING_APPROVAL + PAID) và % tối thiểu của tour. */
    private static final String SELECT_VIEW
            = "SELECT v.DepartureID, v.TourID, v.DepartureDate, v.TotalSeats, v.Status, v.SeatsLeft, t.MinFillPercent, t.Duration, "
            + "ISNULL((SELECT SUM(b.GuestsCount) FROM Bookings b WHERE b.DepartureID = v.DepartureID "
            + "AND b.PaymentStatus IN ('WAITING_APPROVAL','PAID')), 0) AS Registered "
            + "FROM vw_DepartureSeats v JOIN Tours t ON t.TourID = v.TourID ";

    /** Các ngày khách đặt được: OPEN, từ hôm nay trở đi, còn ít nhất 1 chỗ. */
    public List<TourDeparture> getOpenDepartures(int tourId) {
        return query(SELECT_VIEW + "WHERE v.TourID = ? AND v.Status = 'OPEN' AND v.DepartureDate >= CAST(GETDATE() AS DATE) "
                + "AND v.SeatsLeft > 0 ORDER BY v.DepartureDate ASC", tourId);
    }

    /** Tất cả ngày khởi hành của tour (cho Provider quản lý), kể cả ngày đã đóng / đã qua. */
    public List<TourDeparture> getAllDepartures(int tourId) {
        return query(SELECT_VIEW + "WHERE v.TourID = ? ORDER BY v.DepartureDate ASC", tourId);
    }

    public TourDeparture getDeparture(int departureId) {
        List<TourDeparture> list = query(SELECT_VIEW + "WHERE v.DepartureID = ?", departureId);
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean insert(int tourId, Date date, int totalSeats) {
        String sql = "INSERT INTO TourDepartures (TourID, DepartureDate, TotalSeats) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tourId);
            ps.setDate(2, date);
            ps.setInt(3, totalSeats);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();   // trùng ngày (UQ_Dep_TourDate) cũng rơi vào đây
        }
        return false;
    }

    /** Đổi tổng số chỗ; chỉ thành công khi số mới >= số chỗ đã giữ/đã bán của ngày đó. */
    public boolean updateTotalSeats(int departureId, int tourId, int newTotal) {
        String sql = "UPDATE TourDepartures SET TotalSeats = ? WHERE DepartureID = ? AND TourID = ? "
                + "AND ? >= (SELECT TotalSeats - SeatsLeft FROM vw_DepartureSeats WHERE DepartureID = ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newTotal);
            ps.setInt(2, departureId);
            ps.setInt(3, tourId);
            ps.setInt(4, newTotal);
            ps.setInt(5, departureId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Đóng / mở lại một ngày khởi hành (không xóa để giữ lịch sử đơn). */
    public boolean setStatus(int departureId, int tourId, String status) {
        String sql = "UPDATE TourDepartures SET Status = ? WHERE DepartureID = ? AND TourID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, departureId);
            ps.setInt(3, tourId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<TourDeparture> query(String sql, int param) {
        List<TourDeparture> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TourDeparture d = new TourDeparture();
                    d.setDepartureId(rs.getInt("DepartureID"));
                    d.setTourId(rs.getInt("TourID"));
                    d.setDepartureDate(rs.getDate("DepartureDate"));
                    d.setTotalSeats(rs.getInt("TotalSeats"));
                    d.setStatus(rs.getString("Status"));
                    d.setSeatsLeft(Math.max(0, rs.getInt("SeatsLeft")));
                    d.setRegistered(rs.getInt("Registered"));
                    d.setMinFillPercent(rs.getInt("MinFillPercent"));
                    d.setDurationDays(com.viettour.model.Tour.parseDurationDays(rs.getString("Duration")));
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

    /** Cho API số chỗ realtime: các ngày OPEN từ hôm nay, kể cả ngày đã hết chỗ (để trang tự vô hiệu hóa). */
    public List<TourDeparture> getOpenDeparturesIncludingFull(int tourId) {
        return query(SELECT_VIEW + "WHERE v.TourID = ? AND v.Status = 'OPEN' AND v.DepartureDate >= CAST(GETDATE() AS DATE) "
                + "ORDER BY v.DepartureDate ASC", tourId);
    }

    /**
     * Nạp ngày khởi hành còn chỗ cho cả danh sách tour bằng 1 truy vấn.
     *
     * @param onlyDate khác null: chỉ giữ đúng ngày này (khi khách lọc theo ngày)
     */
    public void attachDepartures(List<Tour> tours, java.time.LocalDate onlyDate) {
        if (tours == null || tours.isEmpty()) {
            return;
        }
        java.util.Map<Integer, Tour> byId = new java.util.HashMap<>();
        StringBuilder in = new StringBuilder();
        for (Tour t : tours) {
            byId.put(t.getTourId(), t);
            t.setDepartures(new ArrayList<>());
            in.append(in.length() == 0 ? "?" : ",?");
        }
        String sql = SELECT_VIEW + "WHERE v.TourID IN (" + in + ") AND v.Status = 'OPEN' "
                + "AND v.DepartureDate >= CAST(GETDATE() AS DATE) AND v.SeatsLeft > 0"
                + (onlyDate != null ? " AND v.DepartureDate = ?" : "") + " ORDER BY v.DepartureDate ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int i = 1;
            for (Tour t : tours) {
                ps.setInt(i++, t.getTourId());
            }
            if (onlyDate != null) {
                ps.setDate(i, Date.valueOf(onlyDate));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TourDeparture d = new TourDeparture();
                    d.setDepartureId(rs.getInt("DepartureID"));
                    d.setTourId(rs.getInt("TourID"));
                    d.setDepartureDate(rs.getDate("DepartureDate"));
                    d.setTotalSeats(rs.getInt("TotalSeats"));
                    d.setStatus(rs.getString("Status"));
                    d.setSeatsLeft(Math.max(0, rs.getInt("SeatsLeft")));
                    d.setRegistered(rs.getInt("Registered"));
                    d.setMinFillPercent(rs.getInt("MinFillPercent"));
                    d.setDurationDays(Tour.parseDurationDays(rs.getString("Duration")));
                    byId.get(d.getTourId()).getDepartures().add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

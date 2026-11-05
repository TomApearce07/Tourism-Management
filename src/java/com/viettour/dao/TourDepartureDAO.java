package com.viettour.dao;

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

    private static final String SELECT_VIEW
            = "SELECT DepartureID, TourID, DepartureDate, TotalSeats, Status, SeatsLeft FROM vw_DepartureSeats ";

    /** Các ngày khách đặt được: OPEN, từ hôm nay trở đi, còn ít nhất 1 chỗ. */
    public List<TourDeparture> getOpenDepartures(int tourId) {
        return query(SELECT_VIEW + "WHERE TourID = ? AND Status = 'OPEN' AND DepartureDate >= CAST(GETDATE() AS DATE) "
                + "AND SeatsLeft > 0 ORDER BY DepartureDate ASC", tourId);
    }

    /** Tất cả ngày khởi hành của tour (cho Provider quản lý), kể cả ngày đã đóng / đã qua. */
    public List<TourDeparture> getAllDepartures(int tourId) {
        return query(SELECT_VIEW + "WHERE TourID = ? ORDER BY DepartureDate ASC", tourId);
    }

    public TourDeparture getDeparture(int departureId) {
        List<TourDeparture> list = query(SELECT_VIEW + "WHERE DepartureID = ?", departureId);
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

package com.viettour.dao;

import com.viettour.model.Booking;
import com.viettour.model.Passenger;
import com.viettour.util.DBContext;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class BookingDAO extends DBContext {

    public int insertBooking(Booking b) {
        String sql = "INSERT INTO Bookings (CustomerID, TourID, GuestsCount, SpecialRequests, TotalAmount, PaymentMethod, PaymentStatus, CustomerPhone, CustomerIDCard, BookingChannel, DepartureID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, b.getCustomerId());
            ps.setInt(2, b.getTourId());
            ps.setInt(3, b.getGuestsCount());
            ps.setString(4, b.getSpecialRequests());
            ps.setBigDecimal(5, b.getTotalAmount());
            ps.setString(6, b.getPaymentMethod());
            ps.setString(7, b.getPaymentStatus());
            ps.setString(8, b.getCustomerPhone());
            ps.setString(9, b.getCustomerIdCard());
            ps.setString(10, b.getBookingChannel() != null ? b.getBookingChannel() : "WEBSITE");
            if (b.getDepartureId() > 0) {
                ps.setInt(11, b.getDepartureId());
            } else {
                ps.setNull(11, java.sql.Types.INTEGER);
            }
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void updatePaymentStatus(int bookingId, String status) {
        String sql = "UPDATE Bookings SET PaymentStatus = ? WHERE BookingID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Booking getBookingById(int bookingId) {
        String sql = "SELECT b.*, dep.DepartureDate, c.FullName AS CustomerName, t.TourName, p.FullName AS ProviderName "
                + "FROM Bookings b "
                + "JOIN Accounts c ON b.CustomerID = c.AccountID "
                + "JOIN Tours t ON b.TourID = t.TourID "
                + "JOIN Accounts p ON t.ProviderID = p.AccountID "
                + "LEFT JOIN TourDepartures dep ON b.DepartureID = dep.DepartureID "
                + "WHERE b.BookingID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBooking(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BigDecimal getTotalPlatformRevenue() {
        String sql = "SELECT COALESCE(SUM(TotalAmount), 0) FROM Bookings WHERE PaymentStatus = 'PAID'";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getProviderRevenue(int providerId) {
        String sql = "SELECT COALESCE(SUM(b.TotalAmount), 0) FROM Bookings b "
                + "JOIN Tours t ON b.TourID = t.TourID "
                + "WHERE b.PaymentStatus = 'PAID' AND t.ProviderID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, providerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public List<Booking> getBookingsByCustomer(int customerId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, dep.DepartureDate, c.FullName AS CustomerName, t.TourName, p.FullName AS ProviderName "
                + "FROM Bookings b "
                + "JOIN Accounts c ON b.CustomerID = c.AccountID "
                + "JOIN Tours t ON b.TourID = t.TourID "
                + "JOIN Accounts p ON t.ProviderID = p.AccountID "
                + "LEFT JOIN TourDepartures dep ON b.DepartureID = dep.DepartureID "
                + "WHERE b.CustomerID = ? ORDER BY b.BookingDate DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToBooking(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Booking> getBookingsByProvider(int providerId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, dep.DepartureDate, c.FullName AS CustomerName, t.TourName, p.FullName AS ProviderName "
                + "FROM Bookings b "
                + "JOIN Accounts c ON b.CustomerID = c.AccountID "
                + "JOIN Tours t ON b.TourID = t.TourID "
                + "JOIN Accounts p ON t.ProviderID = p.AccountID "
                + "LEFT JOIN TourDepartures dep ON b.DepartureID = dep.DepartureID "
                + "WHERE t.ProviderID = ? ORDER BY b.BookingDate DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, providerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToBooking(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, dep.DepartureDate, c.FullName AS CustomerName, t.TourName, p.FullName AS ProviderName "
                + "FROM Bookings b "
                + "JOIN Accounts c ON b.CustomerID = c.AccountID "
                + "JOIN Tours t ON b.TourID = t.TourID "
                + "JOIN Accounts p ON t.ProviderID = p.AccountID "
                + "LEFT JOIN TourDepartures dep ON b.DepartureID = dep.DepartureID "
                + "ORDER BY b.BookingDate DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToBooking(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Số chỗ còn lại của ngày khởi hành tại lần createBookingWithPassengers gần nhất báo hết chỗ. */
    private int lastSeatsLeft = 0;

    public int getLastSeatsLeft() {
        return lastSeatsLeft;
    }

    /**
     * Tạo đơn + danh sách hành khách trong MỘT transaction, có khóa dòng ngày khởi hành để hai người
     * đặt cùng lúc không vượt số chỗ.
     *
     * @return mã đơn (> 0) nếu thành công; -1 nếu lỗi DB; -2 nếu ngày khởi hành không còn đủ chỗ / đã đóng
     *         (khi đó getLastSeatsLeft() cho biết còn bao nhiêu chỗ).
     */
    public int createBookingWithPassengers(Booking b, List<Passenger> passengers) {
        lastSeatsLeft = 0;
        try {
            connection.setAutoCommit(false);

            // 1) Khóa dòng ngày khởi hành: giao dịch khác đặt cùng ngày phải chờ giao dịch này xong
            String lockSql = "SELECT 1 FROM TourDepartures WITH (UPDLOCK, ROWLOCK) "
                    + "WHERE DepartureID = ? AND TourID = ? AND Status = 'OPEN' AND DepartureDate >= CAST(GETDATE() AS DATE)";
            boolean open;
            try (PreparedStatement ps = connection.prepareStatement(lockSql)) {
                ps.setInt(1, b.getDepartureId());
                ps.setInt(2, b.getTourId());
                try (ResultSet rs = ps.executeQuery()) {
                    open = rs.next();
                }
            }
            if (!open) {
                connection.rollback();
                return -2;
            }

            // 2) Còn đủ chỗ không?
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT SeatsLeft FROM vw_DepartureSeats WHERE DepartureID = ?")) {
                ps.setInt(1, b.getDepartureId());
                try (ResultSet rs = ps.executeQuery()) {
                    lastSeatsLeft = rs.next() ? Math.max(0, rs.getInt(1)) : 0;
                }
            }
            if (lastSeatsLeft < b.getGuestsCount()) {
                connection.rollback();
                return -2;
            }

            // 3) Ghi đơn rồi ghi hành khách (insertBooking dùng chung connection đang ở chế độ transaction)
            int bookingId = insertBooking(b);
            if (bookingId <= 0) {
                connection.rollback();
                return -1;
            }
            String pSql = "INSERT INTO BookingPassengers (BookingID, FullName, Gender, DateOfBirth, IdCard, Phone, PassengerType, IsRepresentative) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(pSql)) {
                for (Passenger p : passengers) {
                    ps.setInt(1, bookingId);
                    ps.setString(2, p.getFullName());
                    ps.setString(3, p.getGender());
                    ps.setDate(4, p.getDateOfBirth());
                    ps.setString(5, p.getIdCard());
                    ps.setString(6, p.getPhone());
                    ps.setString(7, p.getPassengerType());
                    ps.setBoolean(8, p.isRepresentative());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            connection.commit();
            return bookingId;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ignored) {
            }
            return -1;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * Nạp danh sách hành khách vào từng đơn (1 truy vấn cho cả danh sách).
     *
     * @param maskSensitive true (dùng cho Manager): che CCCD dạng 0012****45 và bỏ ngày sinh ngay trên server
     */
    public void attachPassengers(List<Booking> bookings, boolean maskSensitive) {
        if (bookings == null || bookings.isEmpty()) {
            return;
        }
        java.util.Map<Integer, Booking> byId = new java.util.HashMap<>();
        StringBuilder in = new StringBuilder();
        for (Booking b : bookings) {
            byId.put(b.getBookingId(), b);
            in.append(in.length() == 0 ? "?" : ",?");
        }
        String sql = "SELECT PassengerID, BookingID, FullName, Gender, DateOfBirth, IdCard, Phone, PassengerType, IsRepresentative "
                + "FROM BookingPassengers WHERE BookingID IN (" + in + ") ORDER BY IsRepresentative DESC, PassengerID ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int i = 1;
            for (Booking b : bookings) {
                ps.setInt(i++, b.getBookingId());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Passenger p = new Passenger();
                    p.setPassengerId(rs.getInt("PassengerID"));
                    p.setBookingId(rs.getInt("BookingID"));
                    p.setFullName(rs.getString("FullName"));
                    p.setGender(rs.getString("Gender"));
                    p.setDateOfBirth(rs.getDate("DateOfBirth"));
                    p.setIdCard(rs.getString("IdCard"));
                    p.setPhone(rs.getString("Phone"));
                    p.setPassengerType(rs.getString("PassengerType"));
                    p.setRepresentative(rs.getBoolean("IsRepresentative"));
                    if (maskSensitive) {
                        p.setIdCard(p.getMaskedIdCard());
                        p.setDateOfBirth(null);
                    }
                    byId.get(p.getBookingId()).getPassengers().add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Booking mapResultSetToBooking(ResultSet rs) throws Exception {
        Booking b = new Booking();
        b.setBookingId(rs.getInt("BookingID"));
        b.setCustomerId(rs.getInt("CustomerID"));
        b.setTourId(rs.getInt("TourID"));
        b.setGuestsCount(rs.getInt("GuestsCount"));
        b.setSpecialRequests(rs.getString("SpecialRequests"));
        b.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        b.setPaymentMethod(rs.getString("PaymentMethod"));
        b.setPaymentStatus(rs.getString("PaymentStatus"));
        b.setBookingDate(rs.getTimestamp("BookingDate"));
        b.setCustomerName(rs.getString("CustomerName"));
        b.setTourName(rs.getString("TourName"));
        b.setProviderName(rs.getString("ProviderName"));
        try {
            b.setDepartureId(rs.getInt("DepartureID"));
            b.setDepartureDate(rs.getDate("DepartureDate"));
        } catch (Exception ignored) {
        }
        try {
            b.setCustomerPhone(rs.getString("CustomerPhone"));
            b.setCustomerIdCard(rs.getString("CustomerIDCard"));
            b.setBookingChannel(rs.getString("BookingChannel"));
        } catch (Exception ignored) {
        }
        return b;
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

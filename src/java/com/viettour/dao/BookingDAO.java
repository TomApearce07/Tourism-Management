package com.viettour.dao;

import com.viettour.model.Booking;
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
        String sql = "INSERT INTO Bookings (CustomerID, TourID, GuestsCount, SpecialRequests, TotalAmount, PaymentMethod, PaymentStatus, CustomerPhone, CustomerIDCard, BookingChannel) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
        String sql = "SELECT b.*, c.FullName AS CustomerName, t.TourName, p.FullName AS ProviderName "
                + "FROM Bookings b "
                + "JOIN Accounts c ON b.CustomerID = c.AccountID "
                + "JOIN Tours t ON b.TourID = t.TourID "
                + "JOIN Accounts p ON t.ProviderID = p.AccountID "
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
        String sql = "SELECT b.*, c.FullName AS CustomerName, t.TourName, p.FullName AS ProviderName "
                + "FROM Bookings b "
                + "JOIN Accounts c ON b.CustomerID = c.AccountID "
                + "JOIN Tours t ON b.TourID = t.TourID "
                + "JOIN Accounts p ON t.ProviderID = p.AccountID "
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
        String sql = "SELECT b.*, c.FullName AS CustomerName, t.TourName, p.FullName AS ProviderName "
                + "FROM Bookings b "
                + "JOIN Accounts c ON b.CustomerID = c.AccountID "
                + "JOIN Tours t ON b.TourID = t.TourID "
                + "JOIN Accounts p ON t.ProviderID = p.AccountID "
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
        String sql = "SELECT b.*, c.FullName AS CustomerName, t.TourName, p.FullName AS ProviderName "
                + "FROM Bookings b "
                + "JOIN Accounts c ON b.CustomerID = c.AccountID "
                + "JOIN Tours t ON b.TourID = t.TourID "
                + "JOIN Accounts p ON t.ProviderID = p.AccountID "
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

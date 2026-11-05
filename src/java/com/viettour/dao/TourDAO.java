package com.viettour.dao;

import com.viettour.model.Tour;
import com.viettour.util.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class TourDAO extends DBContext {

    public List<Tour> getApprovedTours() {
        List<Tour> list = new ArrayList<>();
        String sql = "SELECT t.*, a.FullName AS ProviderName FROM Tours t JOIN Accounts a ON t.ProviderID = a.AccountID "
                + "WHERE t.ApprovalStatus = 'APPROVED' AND t.AvailableSeats > 0 ORDER BY t.TourID ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToTour(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Tour> searchApprovedTours(String keyword) {
        List<Tour> list = new ArrayList<>();
        String sql = "SELECT t.*, a.FullName AS ProviderName FROM Tours t JOIN Accounts a ON t.ProviderID = a.AccountID "
                + "WHERE t.ApprovalStatus = 'APPROVED' AND t.AvailableSeats > 0 AND (t.TourName LIKE ? OR t.Location LIKE ?) ORDER BY t.TourID DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String searchPattern = "%" + (keyword != null ? keyword.trim() : "") + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTour(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Tour> getToursByStatus(String status) {
        List<Tour> list = new ArrayList<>();
        String sql = "SELECT t.*, a.FullName AS ProviderName FROM Tours t JOIN Accounts a ON t.ProviderID = a.AccountID "
                + "WHERE t.ApprovalStatus = ? ORDER BY t.TourID ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTour(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Tour> getAllTours() {
        List<Tour> list = new ArrayList<>();
        String sql = "SELECT t.*, a.FullName AS ProviderName FROM Tours t JOIN Accounts a ON t.ProviderID = a.AccountID ORDER BY t.TourID ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToTour(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Tour> getAllToursForManager() {
        List<Tour> list = new ArrayList<>();
        String sql = "SELECT t.*, a.FullName AS ProviderName FROM Tours t JOIN Accounts a ON t.ProviderID = a.AccountID "
                + "ORDER BY CASE WHEN t.ApprovalStatus = 'PENDING' THEN 1 ELSE 2 END ASC, t.TourID DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToTour(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Tour> getToursByProvider(int providerId) {
        List<Tour> list = new ArrayList<>();
        String sql = "SELECT t.*, a.FullName AS ProviderName FROM Tours t JOIN Accounts a ON t.ProviderID = a.AccountID "
                + "WHERE t.ProviderID = ? ORDER BY t.TourID DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, providerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTour(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Tour getTourById(int tourId) {
        String sql = "SELECT t.*, a.FullName AS ProviderName FROM Tours t JOIN Accounts a ON t.ProviderID = a.AccountID WHERE t.TourID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tourId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTour(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertTour(Tour tour) {
        String sql = "INSERT INTO Tours (TourName, Location, Duration, Price, AvailableSeats, Itinerary, ImageURL, ProviderID, ApprovalStatus, TransportCost, HotelCost, MealGuideCost) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tour.getTourName());
            ps.setString(2, tour.getLocation());
            ps.setString(3, tour.getDuration());
            ps.setBigDecimal(4, tour.getPrice());
            ps.setInt(5, tour.getAvailableSeats());
            ps.setString(6, tour.getItinerary());
            ps.setString(7, tour.getImageUrl());
            ps.setInt(8, tour.getProviderId());
            ps.setString(9, tour.getApprovalStatus());
            ps.setBigDecimal(10, tour.getTransportCost());
            ps.setBigDecimal(11, tour.getHotelCost());
            ps.setBigDecimal(12, tour.getMealGuideCost());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTour(Tour tour) {
        String sql = "UPDATE Tours SET TourName = ?, Location = ?, Duration = ?, Price = ?, AvailableSeats = ?, "
                + "Itinerary = ?, ImageURL = ?, TransportCost = ?, HotelCost = ?, MealGuideCost = ?, "
                + "ApprovalStatus = 'PENDING' WHERE TourID = ? AND ProviderID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tour.getTourName());
            ps.setString(2, tour.getLocation());
            ps.setString(3, tour.getDuration());
            ps.setBigDecimal(4, tour.getPrice());
            ps.setInt(5, tour.getAvailableSeats());
            ps.setString(6, tour.getItinerary());
            ps.setString(7, tour.getImageUrl());
            ps.setBigDecimal(8, tour.getTransportCost());
            ps.setBigDecimal(9, tour.getHotelCost());
            ps.setBigDecimal(10, tour.getMealGuideCost());
            ps.setInt(11, tour.getTourId());
            ps.setInt(12, tour.getProviderId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateApprovalStatus(int tourId, String newStatus) {
        String sql = "UPDATE Tours SET ApprovalStatus = ? WHERE TourID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, tourId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countToursByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM Tours WHERE ApprovalStatus = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean deductAvailableSeats(int tourId, int seatsToDeduct) {
        String sql = "UPDATE Tours SET AvailableSeats = AvailableSeats - ? WHERE TourID = ? AND AvailableSeats >= ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, seatsToDeduct);
            ps.setInt(2, tourId);
            ps.setInt(3, seatsToDeduct);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Tour mapResultSetToTour(ResultSet rs) throws Exception {
        Tour t = new Tour();
        t.setTourId(rs.getInt("TourID"));
        t.setTourName(rs.getString("TourName"));
        t.setLocation(rs.getString("Location"));
        t.setDuration(rs.getString("Duration"));
        t.setPrice(rs.getBigDecimal("Price"));
        t.setAvailableSeats(rs.getInt("AvailableSeats"));
        t.setItinerary(rs.getString("Itinerary"));
        t.setImageUrl(rs.getString("ImageURL"));
        t.setProviderId(rs.getInt("ProviderID"));
        t.setApprovalStatus(rs.getString("ApprovalStatus"));
        t.setCreatedAt(rs.getTimestamp("CreatedAt"));
        t.setProviderName(rs.getString("ProviderName"));
        try {
            t.setTransportCost(rs.getBigDecimal("TransportCost"));
            t.setHotelCost(rs.getBigDecimal("HotelCost"));
            t.setMealGuideCost(rs.getBigDecimal("MealGuideCost"));
        } catch (Exception ignored) {
        }
        return t;
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

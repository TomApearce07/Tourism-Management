package com.viettour.dao;

import com.viettour.model.Notification;
import com.viettour.util.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Thông báo của khách (bảng Notifications). Việc tạo thông báo nằm trong transaction của BookingDAO. */
public class NotificationDAO extends DBContext {

    public List<Notification> getUnread(int accountId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT NotificationID, ISNULL(BookingID, 0) AS BookingID, Message, CreatedAt FROM Notifications "
                + "WHERE AccountID = ? AND IsRead = 0 ORDER BY CreatedAt DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notification n = new Notification();
                    n.setNotificationId(rs.getInt("NotificationID"));
                    n.setBookingId(rs.getInt("BookingID"));
                    n.setMessage(rs.getString("Message"));
                    n.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    list.add(n);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void markAllRead(int accountId) {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE Notifications SET IsRead = 1 WHERE AccountID = ? AND IsRead = 0")) {
            ps.setInt(1, accountId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

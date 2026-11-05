package com.viettour.dao;

import com.viettour.model.Account;
import com.viettour.util.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class AccountDAO extends DBContext {

    public Account login(String userOrEmail, String password) {
        String sql = "SELECT a.*, r.RoleName FROM Accounts a JOIN Roles r ON a.RoleID = r.RoleID "
                + "WHERE (a.Email = ? OR a.Username = ?) AND a.Password = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userOrEmail);
            ps.setString(2, userOrEmail);
            ps.setString(3, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAccount(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isEmailOrUsernameExist(String email, String username) {
        String sql = "SELECT AccountID FROM Accounts WHERE Email = ? OR Username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean register(Account acc) {
        String sql = "INSERT INTO Accounts (Username, Password, FullName, Email, Phone, RoleID, Status, TaxCode, BusinessLicense, CompanyAddress) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, acc.getUsername());
            ps.setString(2, acc.getPassword());
            ps.setString(3, acc.getFullName());
            ps.setString(4, acc.getEmail());
            ps.setString(5, acc.getPhone());
            ps.setInt(6, acc.getRoleId());
            ps.setString(7, acc.getStatus());
            ps.setString(8, acc.getTaxCode());
            ps.setString(9, acc.getBusinessLicense());
            ps.setString(10, acc.getCompanyAddress());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Account getAccountById(int accountId) {
        String sql = "SELECT a.*, r.RoleName FROM Accounts a JOIN Roles r ON a.RoleID = r.RoleID WHERE a.AccountID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAccount(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT a.*, r.RoleName FROM Accounts a JOIN Roles r ON a.RoleID = r.RoleID ORDER BY a.RoleID DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToAccount(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Account> getAccountsByRole(int roleId) {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT a.*, r.RoleName FROM Accounts a JOIN Roles r ON a.RoleID = r.RoleID WHERE a.RoleID = ? ORDER BY a.AccountID DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAccount(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateAccountStatus(int accountId, String newStatus) {
        String sql = "UPDATE Accounts SET Status = ? WHERE AccountID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, accountId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Account mapResultSetToAccount(ResultSet rs) throws Exception {
        Account acc = new Account();
        acc.setAccountId(rs.getInt("AccountID"));
        acc.setUsername(rs.getString("Username"));
        acc.setPassword(rs.getString("Password"));
        acc.setFullName(rs.getString("FullName"));
        acc.setEmail(rs.getString("Email"));
        acc.setPhone(rs.getString("Phone"));
        acc.setRoleId(rs.getInt("RoleID"));
        acc.setStatus(rs.getString("Status"));
        acc.setCreatedAt(rs.getTimestamp("CreatedAt"));
        acc.setRoleName(rs.getString("RoleName"));
        try {
            acc.setTaxCode(rs.getString("TaxCode"));
            acc.setBusinessLicense(rs.getString("BusinessLicense"));
            acc.setCompanyAddress(rs.getString("CompanyAddress"));
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return acc;
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

-- =====================================================================
-- Migrate_C3_Cancel.sql  (chạy 1 lần SAU Migrate_Departures.sql; chạy lại được)
-- - Tours.MinFillPercent : % số chỗ tối thiểu phải có người đăng ký thì ngày khởi hành mới chạy (Provider tự đặt)
-- - Bookings.CancelReason / RefundedAt : lý do hủy + thời điểm hoàn tiền
-- - Notifications : thông báo gửi tới Customer (hủy tour, xác nhận tour...)
-- =====================================================================
USE VietTourDB;
GO

IF COL_LENGTH('dbo.Tours', 'MinFillPercent') IS NULL
    ALTER TABLE Tours ADD MinFillPercent INT NOT NULL
        CONSTRAINT DF_Tours_MinFill DEFAULT 0
        CONSTRAINT CK_Tours_MinFill CHECK (MinFillPercent BETWEEN 0 AND 100);
GO

IF COL_LENGTH('dbo.Bookings', 'CancelReason') IS NULL
    ALTER TABLE Bookings ADD CancelReason NVARCHAR(500) NULL;
IF COL_LENGTH('dbo.Bookings', 'RefundedAt') IS NULL
    ALTER TABLE Bookings ADD RefundedAt DATETIME NULL;
GO

IF OBJECT_ID('dbo.Notifications') IS NULL
BEGIN
    CREATE TABLE Notifications (
        NotificationID INT IDENTITY(1,1) PRIMARY KEY,
        AccountID      INT NOT NULL,
        BookingID      INT NULL,
        Message        NVARCHAR(1000) NOT NULL,
        IsRead         BIT NOT NULL CONSTRAINT DF_Noti_Read DEFAULT 0,
        CreatedAt      DATETIME NOT NULL CONSTRAINT DF_Noti_Created DEFAULT GETDATE(),
        CONSTRAINT FK_Noti_Account FOREIGN KEY (AccountID) REFERENCES Accounts(AccountID)
    );
    CREATE INDEX IX_Noti_Account ON Notifications(AccountID, IsRead);
END
GO

-- Gợi ý: đặt yêu cầu tối thiểu cho tour cũ (ví dụ 50%) nếu muốn áp dụng ngay:
-- UPDATE Tours SET MinFillPercent = 50;

-- =====================================================================
-- Migrate_Departures.sql  (chạy 1 lần trên VietTourDB, SAU các script cũ)
-- C1.1: ngày khởi hành cho từng tour  |  C1.2: bảng hành khách
-- Có thể chạy lại nhiều lần mà không lỗi (có IF kiểm tra).
-- =====================================================================
USE VietTourDB;
GO

-- 1) Ngày khởi hành của từng tour (mỗi ngày có số chỗ riêng)
IF OBJECT_ID('dbo.TourDepartures') IS NULL
CREATE TABLE TourDepartures (
    DepartureID   INT IDENTITY(1,1) PRIMARY KEY,
    TourID        INT NOT NULL,
    DepartureDate DATE NOT NULL,
    TotalSeats    INT NOT NULL CONSTRAINT CK_Dep_Seats CHECK (TotalSeats >= 0),
    Status        VARCHAR(20) NOT NULL CONSTRAINT DF_Dep_Status DEFAULT 'OPEN',  -- OPEN, CLOSED, CANCELLED
    CreatedAt     DATETIME NOT NULL CONSTRAINT DF_Dep_Created DEFAULT GETDATE(),
    CONSTRAINT FK_Dep_Tour FOREIGN KEY (TourID) REFERENCES Tours(TourID),
    CONSTRAINT UQ_Dep_TourDate UNIQUE (TourID, DepartureDate)
);
GO

-- 2) Đơn đặt chỗ gắn với ngày khởi hành
IF COL_LENGTH('dbo.Bookings', 'DepartureID') IS NULL
    ALTER TABLE Bookings ADD DepartureID INT NULL
        CONSTRAINT FK_Booking_Dep REFERENCES TourDepartures(DepartureID);
GO

-- 3) Người đi tour (một đơn có nhiều hành khách)
IF OBJECT_ID('dbo.BookingPassengers') IS NULL
BEGIN
    CREATE TABLE BookingPassengers (
        PassengerID      INT IDENTITY(1,1) PRIMARY KEY,
        BookingID        INT NOT NULL,
        FullName         NVARCHAR(100) NOT NULL,
        Gender           VARCHAR(10)  NULL,          -- MALE, FEMALE, OTHER
        DateOfBirth      DATE         NULL,
        IdCard           VARCHAR(50)  NULL,          -- CCCD / Passport (trẻ em có thể để trống)
        Phone            VARCHAR(20)  NULL,
        PassengerType    VARCHAR(10)  NOT NULL CONSTRAINT DF_Pas_Type DEFAULT 'ADULT',  -- ADULT, CHILD
        IsRepresentative BIT          NOT NULL CONSTRAINT DF_Pas_Rep DEFAULT 0,
        CONSTRAINT FK_Pas_Booking FOREIGN KEY (BookingID) REFERENCES Bookings(BookingID) ON DELETE CASCADE
    );
    CREATE INDEX IX_Pas_Booking ON BookingPassengers(BookingID);
END
GO

-- 4) Dữ liệu cũ: tạo 3 ngày khởi hành (sau 7/14/21 ngày) cho tour đã duyệt chưa có ngày,
--    rồi gắn đơn cũ vào ngày gần nhất của tour đó.
INSERT INTO TourDepartures (TourID, DepartureDate, TotalSeats)
-- Sức chứa gốc = số chỗ còn lại hiện tại + số khách đã PAID (vì logic cũ đã trừ chỗ khi duyệt),
-- để khi gắn đơn cũ vào ngày khởi hành đầu tiên thì không bị trừ hai lần.
SELECT t.TourID, DATEADD(DAY, d.n, CAST(GETDATE() AS DATE)),
       CASE WHEN t.AvailableSeats + ISNULL((SELECT SUM(b.GuestsCount) FROM Bookings b WHERE b.TourID = t.TourID AND b.PaymentStatus = 'PAID'), 0) > 0
            THEN t.AvailableSeats + ISNULL((SELECT SUM(b.GuestsCount) FROM Bookings b WHERE b.TourID = t.TourID AND b.PaymentStatus = 'PAID'), 0)
            ELSE 10 END
FROM Tours t CROSS JOIN (VALUES (7), (14), (21)) d(n)
WHERE t.ApprovalStatus = 'APPROVED'
  AND NOT EXISTS (SELECT 1 FROM TourDepartures x WHERE x.TourID = t.TourID);

UPDATE b SET DepartureID = (SELECT TOP 1 d.DepartureID FROM TourDepartures d
                            WHERE d.TourID = b.TourID ORDER BY d.DepartureDate)
FROM Bookings b WHERE b.DepartureID IS NULL;
GO

-- 5) View "số chỗ còn lại" = nguồn sự thật duy nhất (không dùng bộ đếm để khỏi lệch).
--    Tính đơn WAITING_APPROVAL, PAID và UNPAID còn trong 15 phút giữ chỗ.
CREATE OR ALTER VIEW vw_DepartureSeats AS
SELECT d.DepartureID, d.TourID, d.DepartureDate, d.TotalSeats, d.Status,
       d.TotalSeats - ISNULL(SUM(CASE
            WHEN b.PaymentStatus IN ('WAITING_APPROVAL','PAID') THEN b.GuestsCount
            WHEN b.PaymentStatus = 'UNPAID' AND b.BookingDate > DATEADD(MINUTE, -15, GETDATE()) THEN b.GuestsCount
            END), 0) AS SeatsLeft
FROM TourDepartures d
LEFT JOIN Bookings b ON b.DepartureID = d.DepartureID
GROUP BY d.DepartureID, d.TourID, d.DepartureDate, d.TotalSeats, d.Status;
GO

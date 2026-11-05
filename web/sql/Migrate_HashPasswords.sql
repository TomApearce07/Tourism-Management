-- =====================================================================
--  Chuyển mật khẩu trong bảng Accounts từ dạng thô sang SHA-256
--  (chữ thường, 64 ký tự, khớp với PasswordUtil.hashPassword của project)
--
--  CHẠY ĐÚNG MỘT LẦN, đồng thời với việc triển khai WAR mới:
--    - WAR mới + DB chưa chuyển  => không ai đăng nhập được
--    - WAR cũ   + DB đã chuyển   => không ai đăng nhập được
--  Script an toàn khi chạy lại: chỉ đổi các dòng có độ dài khác 64 ký tự.
-- =====================================================================
USE VietTourDB;
GO

UPDATE Accounts
SET Password = LOWER(CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', Password), 2))
WHERE LEN(Password) <> 64;
GO

-- Kiểm tra: tài khoản seed (mật khẩu 123456) phải có mã băm bắt đầu bằng 8d969eef6ecad3c2...
SELECT Username, LEN(Password) AS HashLength, LEFT(Password, 16) AS HashPrefix
FROM Accounts
ORDER BY AccountID;
GO

-- Cách khác nếu chỉ cần cập nhật dữ liệu seed:
-- UPDATE Accounts SET Password = '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92'
-- WHERE Password = '123456';
package com.viettour.controller.auth;

import com.viettour.dao.AccountDAO;
import com.viettour.model.Account;
import com.viettour.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Đăng ký tài khoản (URL /register khai báo trong web.xml, không dùng annotation).
 *
 * Quy tắc nghiệp vụ:
 *  - Customer (RoleID = 1): trạng thái ACTIVE, đăng nhập được ngay.
 *  - Provider (RoleID = 2): trạng thái PENDING, phải được Manager duyệt mới đăng nhập được.
 *  - Chỉ cho phép đăng ký 2 vai trò trên. Mọi giá trị "role" khác đều bị coi là Customer,
 *    nên không thể tự đăng ký Manager / Admin bằng cách sửa form.
 */
public class RegisterServlet extends HttpServlet {

    private static final String REGISTER_JSP = "/views/auth/register.jsp";

    private static final int ROLE_CUSTOMER = 1;
    private static final int ROLE_PROVIDER = 2;

    // Giới hạn độ dài khớp với cột trong DesignDB_VietTour.sql
    private static final int MAX_FULLNAME = 100;
    private static final int MAX_EMAIL = 100;
    private static final int MAX_PASSWORD = 100;
    private static final int MAX_LICENSE = 100;
    private static final int MAX_ADDRESS = 255;

    private static final String USERNAME_REGEX = "^[A-Za-z0-9_.]{3,50}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_REGEX = "^0[0-9]{9,10}$";
    private static final String TAXCODE_REGEX = "^[0-9]{10}(-?[0-9]{3})?$";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(REGISTER_JSP).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullName = clean(request.getParameter("fullName"));
        String username = clean(request.getParameter("username"));
        String email = clean(request.getParameter("email")).toLowerCase();
        String phone = clean(request.getParameter("phone"));
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String agree = request.getParameter("agree");
        boolean isProvider = "Provider".equalsIgnoreCase(clean(request.getParameter("role")));

        if (password == null) {
            password = "";
        }

        // ===== 1. Kiểm tra thông tin chung =====
        if (fullName.isEmpty() || fullName.length() > MAX_FULLNAME) {
            showError(request, response, "Họ tên / Tên tổ chức không được để trống và tối đa " + MAX_FULLNAME + " ký tự!");
            return;
        }
        if (!username.matches(USERNAME_REGEX)) {
            showError(request, response, "Tên đăng nhập gồm 3-50 ký tự: chữ không dấu, số, dấu gạch dưới hoặc dấu chấm!");
            return;
        }
        if (email.length() > MAX_EMAIL || !email.matches(EMAIL_REGEX)) {
            showError(request, response, "Địa chỉ email không hợp lệ!");
            return;
        }
        if (!phone.matches(PHONE_REGEX)) {
            showError(request, response, "Số điện thoại phải bắt đầu bằng 0 và gồm 10 hoặc 11 chữ số!");
            return;
        }
        if (password.length() < 6 || password.length() > MAX_PASSWORD) {
            showError(request, response, "Mật khẩu phải từ 6 đến " + MAX_PASSWORD + " ký tự!");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showError(request, response, "Mật khẩu nhập lại không khớp!");
            return;
        }
        if (!"1".equals(agree)) {
            showError(request, response, "Bạn cần đồng ý với Điều khoản dịch vụ và Chính sách bảo mật!");
            return;
        }

        // ===== 2. Kiểm tra thêm cho Provider =====
        String taxCode = "";
        String businessLicense = "";
        String companyAddress = "";
        if (isProvider) {
            taxCode = clean(request.getParameter("taxCode"));
            businessLicense = clean(request.getParameter("businessLicense"));
            companyAddress = clean(request.getParameter("companyAddress"));

            if (taxCode.isEmpty() || businessLicense.isEmpty() || companyAddress.isEmpty()) {
                showError(request, response, "Nhà cung cấp vui lòng nhập đầy đủ Mã số thuế, Số Giấy phép kinh doanh và Địa chỉ trụ sở!");
                return;
            }
            if (!taxCode.matches(TAXCODE_REGEX)) {
                showError(request, response, "Mã số thuế phải gồm 10 hoặc 13 chữ số!");
                return;
            }
            if (businessLicense.length() > MAX_LICENSE) {
                showError(request, response, "Số Giấy phép kinh doanh tối đa " + MAX_LICENSE + " ký tự!");
                return;
            }
            if (companyAddress.length() > MAX_ADDRESS) {
                showError(request, response, "Địa chỉ trụ sở tối đa " + MAX_ADDRESS + " ký tự!");
                return;
            }
        }

        AccountDAO accountDAO = new AccountDAO();
        try {
            // ===== 3. Kiểm tra trùng email / username =====
            if (accountDAO.isEmailOrUsernameExist(email, username)) {
                showError(request, response, "Email hoặc Tên đăng nhập đã được sử dụng!");
                return;
            }

            // ===== 4. Tạo tài khoản =====
            Account account = new Account();
            account.setUsername(username);
            account.setPassword(PasswordUtil.hashPassword(password));   // không lưu mật khẩu thô
            account.setFullName(fullName);
            account.setEmail(email);
            account.setPhone(phone);

            if (isProvider) {
                account.setRoleId(ROLE_PROVIDER);
                account.setStatus("PENDING");           // chờ Manager phê duyệt
                account.setTaxCode(taxCode);
                account.setBusinessLicense(businessLicense);
                account.setCompanyAddress(companyAddress);
            } else {
                account.setRoleId(ROLE_CUSTOMER);
                account.setStatus("ACTIVE");
            }

            if (!accountDAO.register(account)) {
                showError(request, response, "Đăng ký thất bại! Vui lòng kiểm tra lại thông tin hoặc thử lại sau.");
                return;
            }

        } finally {
            accountDAO.close();     // đóng kết nối DB sau khi dùng xong
        }

        // Provider và Customer hiển thị 2 thông báo khác nhau ở trang đăng nhập
        String flag = isProvider ? "provider" : "true";
        response.sendRedirect(request.getContextPath() + "/login?registered=" + flag);
    }

    /** Bỏ khoảng trắng đầu/cuối; null trở thành chuỗi rỗng. */
    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    /** Trả lại trang đăng ký kèm thông báo lỗi (các giá trị đã nhập vẫn còn trong ${param}). */
    private void showError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher(REGISTER_JSP).forward(request, response);
    }
}

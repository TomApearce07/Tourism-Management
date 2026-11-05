<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng ký tài khoản | VietTour</title>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
    </head>
    <body>
        <div class="auth">
            <aside class="auth-visual" style="background-image:url('${pageContext.request.contextPath}/static/images/auth-hoian.jpg'), linear-gradient(160deg,#2b4c7a,#0b2b3a)">
                <div>
                    <a href="${pageContext.request.contextPath}/home" class="brand">VietTour</a>
                    <div style="font-size:12px;margin-top:8px">Một Việt Nam, muôn vàn trải nghiệm.</div>
                </div>
                <div>
                    <div class="hero-eyebrow" style="font-size:11px;margin-bottom:0">ĐI ĐỂ YÊU THÊM VIỆT NAM</div>
                    <blockquote>“Mỗi chuyến đi, một miền thương nhớ.”</blockquote>
                    <p class="sub">Từ những con phố thân quen đến miền đất mới, hành trình đáng nhớ bắt đầu cùng VietTour.</p>
                </div>
            </aside>
            <div class="auth-form-side">
                <div class="auth-top">
                    <a href="${pageContext.request.contextPath}/home"><i class="bi bi-arrow-left"></i>Quay lại Trang chủ</a>
                    <span>Tiếng Việt</span>
                </div>
                <div class="auth-main">
                    <div class="auth-box">
                        <div class="auth-tabs">
                            <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                            <a href="${pageContext.request.contextPath}/register" class="active">Đăng ký</a>
                        </div>
                        <h1>Bắt đầu hành trình</h1>
                        <p class="sub">Tạo tài khoản để khám phá hoặc cung cấp dịch vụ du lịch trên VietTour.</p>

                        <c:if test="${not empty error}">
                            <div class="alert-vt"><c:out value="${error}"/></div>
                        </c:if>

                        <%-- Khi servlet forward lại trang này do lỗi, ${param.xxx} vẫn còn nên form giữ nguyên dữ liệu đã nhập (trừ mật khẩu) --%>
                        <form action="${pageContext.request.contextPath}/register" method="post">
                            <div class="mb-3">
                                <label class="form-label">Vai trò đăng ký</label>
                                <select class="form-select" name="role" id="roleSelect" onchange="toggleProviderFields()">
                                    <option value="Customer" ${param.role != 'Provider' ? 'selected' : ''}>Khách hàng (Customer)</option>
                                    <option value="Provider" ${param.role == 'Provider' ? 'selected' : ''}>Nhà cung cấp dịch vụ Tour (Provider)</option>
                                </select>
                                <div class="hint"><b>Nhà cung cấp:</b> Cần xác thực thông tin pháp nhân và chờ Manager phê duyệt.</div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label" id="fullNameLabel">Họ và tên</label>
                                <input type="text" class="form-control" name="fullName" maxlength="100"
                                       placeholder="Nhập họ tên hoặc Tên pháp nhân công ty"
                                       value="<c:out value="${param.fullName}"/>" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Tên đăng nhập (Username)</label>
                                <input type="text" class="form-control" name="username" maxlength="50"
                                       pattern="[A-Za-z0-9_.]{3,50}"
                                       title="Từ 3 đến 50 ký tự: chữ không dấu, số, dấu gạch dưới hoặc dấu chấm"
                                       placeholder="Nhập username viết liền không dấu"
                                       value="<c:out value="${param.username}"/>" autocomplete="username" required>
                            </div>
                            <div class="row g-2 mb-3">
                                <div class="col-md-7">
                                    <label class="form-label">Địa chỉ Email</label>
                                    <input type="email" class="form-control" name="email" maxlength="100"
                                           placeholder="you@example.com"
                                           value="<c:out value="${param.email}"/>" required>
                                </div>
                                <div class="col-md-5">
                                    <label class="form-label">Số điện thoại</label>
                                    <input type="tel" class="form-control" name="phone" maxlength="11"
                                           pattern="0[0-9]{9,10}" title="Số điện thoại bắt đầu bằng 0, gồm 10 hoặc 11 số"
                                           placeholder="0901234567"
                                           value="<c:out value="${param.phone}"/>" required>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Mật khẩu</label>
                                <div class="pw-wrap">
                                    <input type="password" class="form-control" id="password" name="password"
                                           minlength="6" maxlength="100" placeholder="Tối thiểu 6 ký tự"
                                           autocomplete="new-password" required>
                                    <button type="button" class="toggle-pw" data-target="password"><i class="bi bi-eye-slash"></i></button>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Nhập lại mật khẩu</label>
                                <div class="pw-wrap">
                                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                                           minlength="6" maxlength="100" placeholder="Nhập lại mật khẩu"
                                           autocomplete="new-password" required>
                                    <button type="button" class="toggle-pw" data-target="confirmPassword"><i class="bi bi-eye-slash"></i></button>
                                </div>
                            </div>

                            <div id="providerOrgFields" style="display:none; background:#f3f5f2; padding:16px; border-radius:10px; border:1px solid var(--vt-border);" class="mb-3">
                                <div class="fw-bold mb-2" style="font-size:13px; color:var(--vt-green);">
                                    <i class="bi bi-building-check me-1"></i> Hồ sơ xác thực Tổ chức cung cấp Tour
                                </div>
                                <div class="mb-2">
                                    <label class="form-label">Mã số thuế doanh nghiệp</label>
                                    <input type="text" class="form-control" name="taxCode" id="taxCodeInput" maxlength="50"
                                           pattern="[0-9]{10}(-?[0-9]{3})?" title="Mã số thuế gồm 10 số hoặc 13 số"
                                           placeholder="VD: 0102345678"
                                           value="<c:out value="${param.taxCode}"/>">
                                </div>
                                <div class="mb-2">
                                    <label class="form-label">Số Giấy phép kinh doanh Lữ hành</label>
                                    <input type="text" class="form-control" name="businessLicense" id="licenseInput" maxlength="100"
                                           placeholder="VD: 79-123/2026/TCDL-GP LHQT"
                                           value="<c:out value="${param.businessLicense}"/>">
                                </div>
                                <div>
                                    <label class="form-label">Địa chỉ trụ sở chính</label>
                                    <input type="text" class="form-control" name="companyAddress" id="addressInput" maxlength="255"
                                           placeholder="Số nhà, đường, quận/huyện, tỉnh/thành phố"
                                           value="<c:out value="${param.companyAddress}"/>">
                                </div>
                            </div>

                            <div class="form-check mb-3" style="font-size:12px">
                                <input class="form-check-input" type="checkbox" id="agree" name="agree" value="1" required>
                                <label class="form-check-label text-muted-vt" for="agree">Tôi đồng ý với Điều khoản dịch vụ và Chính sách bảo mật của VietTour.</label>
                            </div>
                            <button type="submit" class="btn-vt w-100" style="height:48px">Đăng ký tài khoản <i class="bi bi-arrow-right"></i></button>
                        </form>
                        <div class="text-center mt-3" style="font-size:12px;color:var(--vt-muted)">
                            Đã có tài khoản? <a href="${pageContext.request.contextPath}/login" class="fw-semibold">Đăng nhập ngay</a>
                        </div>
                    </div>
                </div>
                <div class="auth-foot"><span>© 2026 VietTour</span><a href="#"><i class="bi bi-question-circle me-2"></i>Cần trợ giúp?</a></div>
            </div>
        </div>
        <script>
            function toggleProviderFields() {
                var role = document.getElementById('roleSelect').value;
                var orgBlock = document.getElementById('providerOrgFields');
                var nameLabel = document.getElementById('fullNameLabel');
                var isProvider = (role === 'Provider');

                orgBlock.style.display = isProvider ? 'block' : 'none';
                nameLabel.textContent = isProvider ? 'Tên pháp nhân Tổ chức / Công ty du lịch' : 'Họ và tên';
                document.getElementById('taxCodeInput').required = isProvider;
                document.getElementById('licenseInput').required = isProvider;
                document.getElementById('addressInput').required = isProvider;
            }
            // Chạy ngay khi tải trang để khớp vai trò đang chọn (ví dụ sau khi server trả lại lỗi)
            toggleProviderFields();
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/static/js/main.js"></script>
    </body>
</html>

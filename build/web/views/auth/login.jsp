<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng nhập | VietTour</title>
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
                            <a href="${pageContext.request.contextPath}/login" class="active">Đăng nhập</a>
                            <a href="${pageContext.request.contextPath}/register">Đăng ký</a>
                        </div>
                        <h1>Chào mừng trở lại</h1>
                        <p class="sub">Đăng nhập để tiếp tục hành trình khám phá Việt Nam cùng VietTour.</p>

                        <%-- Đăng ký xong: Customer dùng được ngay, Provider phải chờ Manager duyệt --%>
                        <c:if test="${param.registered == 'true'}">
                            <div class="alert alert-success py-2" style="font-size:13px">
                                Đăng ký tài khoản thành công! Vui lòng đăng nhập.
                            </div>
                        </c:if>
                        <c:if test="${param.registered == 'provider'}">
                            <div class="alert alert-success py-2" style="font-size:13px">
                                Đăng ký thành công! Tài khoản Nhà cung cấp cần được Manager phê duyệt trước khi đăng nhập.
                            </div>
                        </c:if>

                        <c:if test="${not empty error}">
                            <div class="alert-vt"><c:out value="${error}"/></div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/login" method="post">
                            <div class="mb-3">
                                <label class="form-label">Email hoặc Tên đăng nhập</label>
                                <input type="text" class="form-control" name="email" maxlength="100"
                                       placeholder="you@example.com hoặc username"
                                       value="<c:out value="${param.email}"/>"
                                       autocomplete="username" autofocus required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Mật khẩu</label>
                                <div class="pw-wrap">
                                    <input type="password" class="form-control" id="password" name="password"
                                           placeholder="Nhập mật khẩu của bạn"
                                           autocomplete="current-password" required>
                                    <button type="button" class="toggle-pw" data-target="password"><i class="bi bi-eye-slash"></i></button>
                                </div>
                            </div>
                            <button type="submit" class="btn-vt w-100 mt-3" style="height:48px">Đăng nhập <i class="bi bi-arrow-right"></i></button>
                        </form>
                        <div class="text-center mt-3" style="font-size:12px;color:var(--vt-muted)">
                            Chưa có tài khoản VietTour? <a href="${pageContext.request.contextPath}/register" class="fw-semibold">Tạo tài khoản mới</a>
                        </div>
                    </div>
                </div>
                <div class="auth-foot"><span>© 2026 VietTour</span><a href="#"><i class="bi bi-question-circle me-2"></i>Cần trợ giúp?</a></div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/static/js/main.js"></script>
    </body>
</html>

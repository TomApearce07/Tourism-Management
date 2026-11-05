<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://viettour.com/tags" prefix="vt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Trang chủ | VietTour</title>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
    </head>
    <body>
        <header class="vt-nav">
            <div class="vt-container">
                <div class="vt-nav-left">
                    <a href="${pageContext.request.contextPath}/home" class="vt-brand">VietTour</a>
                    <ul class="vt-menu d-none d-lg-flex">
                        <li><a href="${pageContext.request.contextPath}/home" class="active">Trang chủ</a></li>
                        <li><a href="${pageContext.request.contextPath}/tours">Danh sách Tour</a></li>
                    </ul>
                </div>
                <div>
                    <c:choose>
                        <c:when test="${not empty sessionScope.account}">
                            <div class="user-profile-menu">
                                <div class="user-profile-btn" onclick="toggleUserDropdown(event)">
                                    <div class="user-avatar-circle">
                                        <c:out value="${empty sessionScope.account.fullName ? '?' : sessionScope.account.fullName.substring(0, 1).toUpperCase()}"/>
                                    </div>
                                    <span style="font-weight: 500; color: #1e293b;"><c:out value="${sessionScope.account.fullName}"/></span>
                                </div>
                                <div id="userDropdownMenu" class="user-dropdown-content">
                                    <c:if test="${sessionScope.account.roleId == 1}">
                                        <a href="${pageContext.request.contextPath}/customer/history">Lịch sử đặt tour</a>
                                    </c:if>
                                    <c:if test="${sessionScope.account.roleId == 2}">
                                        <a href="${pageContext.request.contextPath}/provider/dashboard">Bảng điều khiển NCC</a>
                                    </c:if>
                                    <c:if test="${sessionScope.account.roleId == 3}">
                                        <a href="${pageContext.request.contextPath}/manager/overview">Không gian Quản lý</a>
                                    </c:if>
                                    <c:if test="${sessionScope.account.roleId == 4}">
                                        <a href="${pageContext.request.contextPath}/admin/users">Quản trị Hệ thống</a>
                                    </c:if>
                                    <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-primary">Đăng nhập</a>
                            <a href="${pageContext.request.contextPath}/register" class="btn btn-primary">Đăng ký</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </header>

        <section class="vt-hero" style="background-image:url('${pageContext.request.contextPath}/static/images/hero-halong.jpg'), linear-gradient(135deg,#1d5a5e,#0b3b46)">
            <div class="vt-container">
                <div class="hero-eyebrow">ĐI ĐỂ YÊU THÊM VIỆT NAM</div>
                <h1>Khám phá vẻ đẹp Việt Nam</h1>
                <p class="lead-vt">Từ những miền di sản đến chân trời mới. Tìm hành trình của bạn cùng VietTour.</p>
                <form class="vt-search" action="${pageContext.request.contextPath}/tours" method="get">
                    <div class="field">
                        <i class="bi bi-geo-alt"></i>
                        <div class="flex-grow-1">
                            <label for="destination">Điểm đến</label>
                            <select id="destination" name="keyword">
                                <option value="">Bạn muốn đi đâu?</option>
                                <option value="Quảng Ninh">Quảng Ninh</option>
                                <option value="Lào Cai">Lào Cai</option>
                                <option value="Quảng Nam">Quảng Nam</option>
                                <option value="Kiên Giang">Kiên Giang</option>
                                <option value="Ninh Bình">Ninh Bình</option>
                            </select>
                        </div>
                    </div>
                    <div class="divider"></div>
                    <div class="field">
                        <i class="bi bi-calendar3"></i>
                        <div class="flex-grow-1">
                            <label>Ngày khởi hành</label>
                            <input type="date" name="date">
                        </div>
                    </div>
                    <button type="submit" class="btn-vt-accent"><i class="bi bi-search"></i>Tìm kiếm</button>
                </form>
            </div>
        </section>

        <section style="padding:68px 0">
            <div class="vt-container">
                <div class="d-flex justify-content-between align-items-end mb-4 flex-wrap gap-2">
                    <div>
                        <div class="eyebrow">HÀNH TRÌNH ĐƯỢC YÊU THÍCH</div>
                        <h2 class="section-title">Các Tour Nổi Bật</h2>
                    </div>
                    <a href="${pageContext.request.contextPath}/tours" class="link-more">Khám phá tất cả <i class="bi bi-arrow-up-right"></i></a>
                </div>
                <div class="row g-4">
                    <c:forEach var="t" items="${featuredTours}">
                        <div class="col-12 col-md-6 col-lg-3">
                            <div class="tour-card">
                                <div class="photo" style="background-image:url('${pageContext.request.contextPath}/static/images/<c:out value="${t.imageUrl}"/>'), var(--ph)">
                                    <span class="badge-region"><c:out value="${t.location}"/></span>
                                </div>
                                <div class="body">
                                    <div>
                                        <div class="meta"><i class="bi bi-clock"></i><c:out value="${t.duration}"/></div>
                                        <h3><c:out value="${t.tourName}"/></h3>
                                        <div class="province"><i class="bi bi-geo-alt"></i><c:out value="${t.location}"/></div>
                                    </div>
                                    <hr>
                                    <div>
                                        <div class="price"><b><vt:formatMoney value="${t.price}" /></b><span>/ khách</span></div>
                                        <div class="seats"><i class="bi bi-people"></i>Còn ${t.availableSeats} chỗ • Sẵn sàng khởi hành</div>
                                    </div>
                                    <a href="${pageContext.request.contextPath}/tour-detail?id=${t.tourId}" class="btn-vt-outline">Xem chi tiết</a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </section>
        <footer class="vt-footer"><div class="vt-container"><div class="legal"><span>© 2026 VietTour.</span></div></div></footer>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/static/js/main.js"></script>
    </body>
</html>
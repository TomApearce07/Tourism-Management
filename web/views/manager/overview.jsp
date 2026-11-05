<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://viettour.com/tags" prefix="vt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tổng quan vận hành | VietTour</title>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
        <script src="${pageContext.request.contextPath}/static/js/main.js"></script>
    </head>
    <body>
        <div class="adm">
            <aside class="adm-side">
                <div class="brand">VietTour</div>
                <div class="ops">KHÔNG GIAN QUẢN LÝ</div>
                <nav class="mt-4">
                    <a href="${pageContext.request.contextPath}/manager/overview" class="active"><i class="bi bi-grid"></i>Tổng quan</a>
                    <a href="${pageContext.request.contextPath}/manager/manage-tours"><i class="bi bi-map"></i>Quản lý &amp; Duyệt Tour</a>
                    <a href="${pageContext.request.contextPath}/manager/providers"><i class="bi bi-building-check"></i>Quản lý Nhà cung cấp</a>
                    <a href="${pageContext.request.contextPath}/manager/bookings"><i class="bi bi-ticket-perforated"></i>Đơn đặt chỗ</a>
                </nav>
            </aside>
            <div class="adm-main">
                <div class="adm-top">
                    <b style="font-size:14px">Không gian Quản lý (Manager)</b>
                    <div class="vt-user">
                        <c:if test="${not empty sessionScope.account}">
                            <div class="user-profile-menu">
                                <div class="user-profile-btn" onclick="toggleUserDropdown(event)">
                                    <div class="user-avatar-circle">
                                        <c:out value="${empty sessionScope.account.fullName ? '?' : sessionScope.account.fullName.substring(0, 1).toUpperCase()}"/>
                                    </div>
                                    <span style="font-weight: 500; color: #1e293b;"><c:out value="${sessionScope.account.fullName}"/></span>
                                </div>
                                <div id="userDropdownMenu" class="user-dropdown-content">
                                    <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>
                <div class="adm-content">
                    <h1>Tổng quan vận hành hệ thống</h1>
                    <p class="section-sub">Theo dõi danh mục tour, kiểm duyệt nhà cung cấp và doanh thu toàn nền tảng.</p>
                    <div class="row g-3 my-3">
                        <div class="col-md-4">
                            <div class="stat-card">
                                <div class="top">Tour đang hoạt động<i class="bi bi-map"></i></div>
                                <div class="num">${activeCount}</div>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="stat-card">
                                <div class="top">Tour chờ phê duyệt<i class="bi bi-clock"></i></div>
                                <div class="num">${pendingCount}</div>
                                <div class="note warn">Đang chờ bạn xét duyệt</div>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="stat-card">
                                <div class="top">Tổng doanh thu nền tảng<i class="bi bi-wallet2"></i></div>
                                <div class="num"><vt:formatMoney value="${totalRevenue}" /></div>
                                <div class="note">Từ các đơn đã tất toán</div>
                            </div>
                        </div>
                    </div>
                    <div class="panel mt-4">
                        <div class="panel-head d-flex justify-content-between align-items-center">
                            <h2>Các Tour mới gửi (Đang chờ duyệt)</h2>
                            <a href="${pageContext.request.contextPath}/manager/manage-tours" class="btn-mini">Xem tất cả Tour</a>
                        </div>
                        <div class="table-scroll">
                            <table class="adm-table">
                                <thead>
                                    <tr>
                                        <th>Tên Tour</th>
                                        <th>Nhà cung cấp</th>
                                        <th class="r">Giá bán</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="t" items="${recentPendingTours}">
                                        <tr>
                                            <td>
                                                <div class="tour-cell">
                                                    <div class="thumb" style="background-image:url('${pageContext.request.contextPath}/static/images/<c:out value="${t.imageUrl}"/>'), var(--ph)"></div>
                                                    <div><b><c:out value="${t.tourName}"/></b><small><c:out value="${t.location}"/></small></div>
                                                </div>
                                            </td>
                                            <td><c:out value="${t.providerName}"/></td>
                                            <td class="r fw-semibold"><vt:formatMoney value="${t.price}" /></td>
                                            <td><span class="status Pending"><i class="bi bi-clock"></i>Chờ duyệt</span></td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/manager/tour-detail?id=${t.tourId}" class="btn-mini">
                                                    <i class="bi bi-eye me-1"></i>Xem chi tiết &amp; Duyệt
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
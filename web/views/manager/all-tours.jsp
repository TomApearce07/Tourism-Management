<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://viettour.com/tags" prefix="vt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý &amp; Phê duyệt Tour | VietTour</title>
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
                    <a href="${pageContext.request.contextPath}/manager/overview"><i class="bi bi-grid"></i>Tổng quan</a>
                    <a href="${pageContext.request.contextPath}/manager/manage-tours" class="active"><i class="bi bi-map"></i>Quản lý &amp; Duyệt Tour</a>
                    <a href="${pageContext.request.contextPath}/manager/providers"><i class="bi bi-building-check"></i>Quản lý Nhà cung cấp</a>
                    <a href="${pageContext.request.contextPath}/manager/tour-stats"><i class="bi bi-bar-chart-line"></i>Thống kê Tour</a>
                    <a href="${pageContext.request.contextPath}/manager/bookings"><i class="bi bi-ticket-perforated"></i>Đơn đặt chỗ</a>
                </nav>
            </aside>
            <div class="adm-main">
                <div class="adm-top">
                    <b style="font-size:14px">Không gian Quản lý (Manager)</b>
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
                <div class="adm-content">
                    <h1>Quản lý &amp; Phê duyệt Danh mục Tour</h1>
                    <p class="section-sub">Các Tour mới tạo hoặc vừa chỉnh sửa (trạng thái Chờ duyệt) được xếp lên đầu. Nhấn <b>Xem chi tiết &amp; Duyệt</b> để kiểm tra thông tin Nhà cung cấp và nội dung Tour trước khi phê duyệt.</p>

                    <c:if test="${param.statusUpdated == 'true'}">
                        <div class="alert alert-success mt-3 mb-0">Đã cập nhật trạng thái phê duyệt Tour thành công!</div>
                    </c:if>

                    <div class="panel mt-4">
                        <div class="panel-head">
                            <h2>Danh mục Tour trên nền tảng <span class="count-pill">Chờ duyệt: ${pendingCount} | Đã duyệt: ${approvedCount}</span></h2>
                        </div>
                        <div class="table-scroll">
                            <table class="adm-table">
                                <thead>
                                    <tr>
                                        <th>Mã Tour</th>
                                        <th>Tên Tour / Địa điểm</th>
                                        <th>Công ty cung cấp</th>
                                        <th class="c">Chỗ trống</th>
                                        <th class="r">Giá bán</th>
                                        <th class="r">Giá vốn</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="t" items="${allTours}">
                                        <tr style="${t.approvalStatus == 'PENDING' ? 'background:#fdf9e8;' : ''}">
                                            <td class="mono text-muted-vt">VT-T00${t.tourId}</td>
                                            <td>
                                                <div class="tour-cell">
                                                    <div class="thumb sm" style="background-image:url('${pageContext.request.contextPath}/static/images/<c:out value="${t.imageUrl}"/>'), var(--ph)"></div>
                                                    <div>
                                                        <b><c:out value="${t.tourName}"/></b>
                                                        <small><c:out value="${t.location}"/> • <c:out value="${t.duration}"/></small>
                                                    </div>
                                                </div>
                                            </td>
                                            <td><b><c:out value="${t.providerName}"/></b></td>
                                            <td class="c">${t.availableSeats}</td>
                                            <td class="r fw-semibold"><vt:formatMoney value="${t.price}" /></td>
                                            <td class="r"><vt:formatMoney value="${t.unitCost}" /></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${t.approvalStatus == 'APPROVED'}"><span class="status Approved"><span class="sdot"></span>Đã duyệt</span></c:when>
                                                    <c:when test="${t.approvalStatus == 'PENDING'}"><span class="status Pending"><span class="sdot"></span>Chờ duyệt</span></c:when>
                                                    <c:otherwise><span class="status Rejected"><span class="sdot"></span>Đã đình chỉ / Từ chối</span></c:otherwise>
                                                </c:choose>
                                            </td>
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
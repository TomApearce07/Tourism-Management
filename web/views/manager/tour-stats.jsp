<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://viettour.com/tags" prefix="vt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Thống kê Tour | VietTour</title>
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
                    <a href="${pageContext.request.contextPath}/manager/manage-tours"><i class="bi bi-map"></i>Quản lý &amp; Duyệt Tour</a>
                    <a href="${pageContext.request.contextPath}/manager/providers"><i class="bi bi-building-check"></i>Quản lý Nhà cung cấp</a>
                    <a href="${pageContext.request.contextPath}/manager/tour-stats" class="active"><i class="bi bi-bar-chart-line"></i>Thống kê Tour</a>
                    <a href="${pageContext.request.contextPath}/manager/bookings"><i class="bi bi-ticket-perforated"></i>Đơn đặt chỗ</a>
                </nav>
            </aside>
            <div class="adm-main">
                <div class="adm-top">
                    <b style="font-size:14px">Không gian Quản lý (Manager)</b>
                    <c:if test="${not empty sessionScope.account}">
                        <div class="user-profile-menu">
                            <div class="user-profile-btn" onclick="toggleUserDropdown(event)">
                                <div class="user-avatar-circle"><c:out value="${empty sessionScope.account.fullName ? '?' : sessionScope.account.fullName.substring(0, 1).toUpperCase()}"/></div>
                                <span style="font-weight: 500; color: #1e293b;"><c:out value="${sessionScope.account.fullName}"/></span>
                            </div>
                            <div id="userDropdownMenu" class="user-dropdown-content">
                                <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
                            </div>
                        </div>
                    </c:if>
                </div>
                <div class="adm-content">
                    <h1>Thống kê Tour</h1>
                    <p class="section-sub">"Đã bán" tính theo đơn <b>PAID</b>; đơn chờ nhà cung cấp duyệt hiển thị riêng. Chi phí = số khách đã bán × giá vốn hiện tại của tour.</p>

                    <form method="get" action="${pageContext.request.contextPath}/manager/tour-stats" class="d-flex gap-2 flex-wrap my-3">
                        <select class="form-select" style="max-width:260px" name="providerId" data-autosubmit>
                            <option value="">Tất cả nhà cung cấp</option>
                            <c:forEach var="p" items="${providers}">
                                <option value="${p.accountId}" ${selProvider == p.accountId ? 'selected' : ''}><c:out value="${p.fullName}"/></option>
                            </c:forEach>
                        </select>
                        <select class="form-select" style="max-width:220px" name="status" data-autosubmit>
                            <option value="">Mọi trạng thái duyệt</option>
                            <option value="APPROVED" ${selStatus == 'APPROVED' ? 'selected' : ''}>Đã duyệt</option>
                            <option value="PENDING" ${selStatus == 'PENDING' ? 'selected' : ''}>Chờ duyệt</option>
                            <option value="REJECTED" ${selStatus == 'REJECTED' ? 'selected' : ''}>Từ chối</option>
                        </select>
                    </form>

                    <div class="row g-3 mb-4">
                        <div class="col-md-3"><div class="stat-card"><div class="top">Khách đã bán<i class="bi bi-people"></i></div><div class="num">${totalSold}</div></div></div>
                        <div class="col-md-3"><div class="stat-card"><div class="top">Doanh thu<i class="bi bi-wallet2"></i></div><div class="num" style="font-size:22px"><vt:formatMoney value="${totalRevenue}" /></div></div></div>
                        <div class="col-md-3"><div class="stat-card"><div class="top">Chi phí<i class="bi bi-cash-stack"></i></div><div class="num" style="font-size:22px"><vt:formatMoney value="${totalCost}" /></div></div></div>
                        <div class="col-md-3"><div class="stat-card"><div class="top">Lợi nhuận<i class="bi bi-graph-up"></i></div><div class="num" style="font-size:22px"><vt:formatMoney value="${totalProfit}" /></div></div></div>
                    </div>

                    <c:if test="${not empty detailTour}">
                        <div class="panel mb-4">
                            <div class="panel-head d-flex justify-content-between"><h2>Chi tiết theo ngày khởi hành: <c:out value="${detailTour.tourName}"/></h2>
                                <a class="btn-mini" href="${pageContext.request.contextPath}/manager/tour-stats?providerId=${selProvider}&status=${selStatus}">Đóng</a></div>
                            <div class="table-scroll">
                                <table class="adm-table">
                                    <thead><tr><th>Ngày khởi hành</th><th class="c">Tổng chỗ</th><th class="c">Đã bán</th><th class="c">Còn lại</th><th style="min-width:180px">Tỷ lệ lấp đầy</th><th>Trạng thái</th></tr></thead>
                                    <tbody>
                                        <c:forEach var="d" items="${departureStats}">
                                            <tr>
                                                <td><b>${d.dateText}</b></td>
                                                <td class="c">${d.totalSeats}</td>
                                                <td class="c">${d.sold}</td>
                                                <td class="c">${d.seatsLeft}</td>
                                                <td>
                                                    <div class="progress" style="height:10px"><div class="progress-bar" style="width:${d.fillRate}%"></div></div>
                                                    <small>${d.fillRate}%</small>
                                                </td>
                                                <td>${d.status == 'OPEN' ? 'Đang mở' : 'Đã đóng'}</td>
                                            </tr>
                                        </c:forEach>
                                        <c:if test="${empty departureStats}"><tr><td colspan="6" class="text-muted-vt">Tour chưa có ngày khởi hành.</td></tr></c:if>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </c:if>

                    <div class="panel">
                        <div class="panel-head"><h2>Thống kê từng Tour</h2></div>
                        <div class="table-scroll">
                            <table class="adm-table">
                                <thead>
                                    <tr>
                                        <th>Tour</th><th>Nhà cung cấp</th><th>Duyệt</th>
                                        <th class="c">Khách đã bán</th><th class="c">Khách chờ duyệt</th><th class="c">Đơn hủy/hoàn</th>
                                        <th class="r">Doanh thu</th><th class="r">Chi phí</th><th class="r">Lợi nhuận</th><th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="t" items="${tourStats}">
                                        <tr>
                                            <td><b><c:out value="${t.tourName}"/></b><br><small class="text-muted-vt">VT-T00${t.tourId}</small></td>
                                            <td><c:out value="${t.providerName}"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${t.approvalStatus == 'APPROVED'}"><span class="status Approved"><span class="sdot"></span>Đã duyệt</span></c:when>
                                                    <c:when test="${t.approvalStatus == 'PENDING'}"><span class="status Pending"><span class="sdot"></span>Chờ duyệt</span></c:when>
                                                    <c:otherwise><span class="status Rejected"><span class="sdot"></span>Từ chối</span></c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="c"><b>${t.guestsSold}</b></td>
                                            <td class="c">${t.guestsWaiting}</td>
                                            <td class="c">${t.cancelledOrders}</td>
                                            <td class="r"><vt:formatMoney value="${t.revenue}" /></td>
                                            <td class="r"><vt:formatMoney value="${t.cost}" /></td>
                                            <td class="r fw-semibold"><vt:formatMoney value="${t.profit}" /></td>
                                            <td><a class="btn-mini" href="${pageContext.request.contextPath}/manager/tour-stats?providerId=${selProvider}&status=${selStatus}&tourId=${t.tourId}">Theo ngày</a></td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty tourStats}"><tr><td colspan="10" class="text-muted-vt">Không có tour nào phù hợp bộ lọc.</td></tr></c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>

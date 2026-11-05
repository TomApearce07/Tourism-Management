<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://viettour.com/tags" prefix="vt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Đơn đặt chỗ toàn hệ thống | VietTour</title>
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
                    <a href="${pageContext.request.contextPath}/manager/tour-stats"><i class="bi bi-bar-chart-line"></i>Thống kê Tour</a>
                    <a href="${pageContext.request.contextPath}/manager/bookings" class="active"><i class="bi bi-ticket-perforated"></i>Đơn đặt chỗ</a>
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
                    <h1>Quản lý Đơn đặt chỗ toàn nền tảng</h1>
                    <div class="panel mt-4">
                        <div class="panel-head"><h2>Danh sách giao dịch đặt chỗ</h2></div>
                        <div class="table-scroll">
                            <table class="adm-table">
                                <thead><tr><th>Mã đơn</th><th>Khách hàng</th><th>Tên Tour &amp; Kênh</th><th>Nhà cung cấp</th><th class="r">Tổng tiền</th><th>Khởi hành, Hành khách &amp; Lợi nhuận</th><th>Trạng thái</th></tr></thead>
                                <tbody>
                                    <c:forEach var="b" items="${allBookings}">
                                        <tr>
                                            <td><b>BK-00${b.bookingId}</b></td>
                                            <td><c:out value="${b.customerName}"/></td>
                                            <td><b><c:out value="${b.tourName}"/></b><br><small>Kênh: ${empty b.bookingChannel ? 'WEBSITE' : b.bookingChannel}</small></td>
                                            <td><c:out value="${b.providerName}"/></td>
                                            <td class="r fw-semibold"><vt:formatMoney value="${b.totalAmount}" /></td>
                                            <td style="max-width:320px">
                                                <small><b>Khởi hành:</b> ${empty b.departureDateText ? 'Chưa gán ngày' : b.departureDateText}</small>
                                                <c:if test="${not empty b.passengers}">
                                                    <details>
                                                        <summary style="cursor:pointer;font-size:12px">${b.passengers.size()} hành khách</summary>
                                                        <c:forEach var="p" items="${b.passengers}">
                                                            <div style="font-size:12px"><b><c:out value="${p.fullName}"/></b> · ${p.passengerType == 'CHILD' ? 'Trẻ em' : 'Người lớn'}<c:if test="${not empty p.idCard}"> · <c:out value="${p.idCard}"/></c:if></div>
                                                        </c:forEach>
                                                    </details>
                                                </c:if>
                                                <div><small><c:out value="${b.specialRequests}"/></small></div>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${b.paymentStatus == 'PAID'}"><span class="pay Paid"><span class="sdot"></span>ĐÃ THANH TOÁN</span></c:when>
                                                    <c:when test="${b.paymentStatus == 'WAITING_APPROVAL'}"><span class="status Pending"><span class="sdot"></span>CHỜ NCC DUYỆT</span></c:when>
                                                    <c:when test="${b.paymentStatus == 'UNPAID'}"><span class="pay Unpaid"><span class="sdot"></span>CHƯA THANH TOÁN</span></c:when>
                                                    <c:otherwise><span class="pay Refunded"><span class="sdot"></span>ĐÃ HOÀN / HỦY</span></c:otherwise>
                                                </c:choose>
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
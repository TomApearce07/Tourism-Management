<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://viettour.com/tags" prefix="vt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Lịch sử đặt tour | VietTour</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
    </head>
    <body>
        <header class="vt-nav">
            <div class="vt-container">
                <div class="vt-nav-left">
                    <a href="${pageContext.request.contextPath}/home" class="vt-brand">VietTour</a>
                    <ul class="vt-menu d-none d-lg-flex">
                        <li><a href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                        <li><a href="${pageContext.request.contextPath}/tours">Danh sách Tour</a></li>
                    </ul>
                </div>
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
        </header>
        <div class="vt-container my-5">
            <c:if test="${not empty sessionScope.paymentSuccessMsg}">
                <div class="alert alert-success"><c:out value="${sessionScope.paymentSuccessMsg}"/></div>
                <% session.removeAttribute("paymentSuccessMsg"); %>
            </c:if>
            <h1 class="mb-4">Lịch sử đặt tour của bạn</h1>
            <div class="panel">
                <div class="table-scroll">
                    <table class="adm-table">
                        <thead>
                            <tr>
                                <th>Mã đơn</th>
                                <th>Tên Tour</th>
                                <th>Nhà cung cấp</th>
                                <th>Ngày khởi hành</th>
                                <th class="c">Số khách</th>
                                <th class="r">Tổng tiền</th>
                                <th>Ngày đặt</th>
                                <th>Trạng thái thanh toán</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="b" items="${bookingList}">
                                <tr>
                                    <td><b>#BK-${b.bookingId}</b></td>
                                    <td><c:out value="${b.tourName}"/></td>
                                    <td><c:out value="${b.providerName}"/></td>
                                    <td>${empty b.departureDateText ? "—" : b.departureDateText}</td>
                                    <td class="c">${b.guestsCount}</td>
                                    <td class="r fw-semibold"><vt:formatMoney value="${b.totalAmount}" /></td>
                                    <td>${b.bookingDate}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${b.paymentStatus == 'PAID'}">
                                                <span class="pay Paid"><span class="sdot"></span>Đã thanh toán (Đã duyệt)</span>
                                            </c:when>
                                            <c:when test="${b.paymentStatus == 'WAITING_APPROVAL'}">
                                                <span class="status Pending"><span class="sdot"></span>Đã chuyển khoản QR (Chờ NCC duyệt)</span>
                                            </c:when>
                                            <c:when test="${b.paymentStatus == 'REFUNDED'}">
                                                <span class="pay Refunded"><span class="sdot"></span>Đã hủy / Hoàn tiền</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="pay Unpaid"><span class="sdot"></span>Chưa thanh toán</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <script src="${pageContext.request.contextPath}/static/js/main.js"></script>
    </body>
</html>
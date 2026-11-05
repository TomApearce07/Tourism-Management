<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://viettour.com/tags" prefix="vt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý Đơn đặt chỗ | VietTour</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
        <script src="${pageContext.request.contextPath}/static/js/main.js"></script>
    </head>
    <body>
        <div class="adm">
            <aside class="adm-side">
                <div class="brand">VietTour</div>
                <div class="ops">KHÔNG GIAN NHÀ CUNG CẤP</div>
                <div class="adm-ws">
                    <div><small>DOANH NGHIỆP CỦA BẠN</small><b><c:out value="${sessionScope.account.fullName}"/></b></div>
                </div>
                <nav class="mt-3">
                    <a href="${pageContext.request.contextPath}/provider/dashboard"><i class="bi bi-map"></i>Tour của tôi</a>
                    <a href="${pageContext.request.contextPath}/provider/booking-orders" class="active"><i class="bi bi-ticket-perforated"></i>Đơn đặt chỗ</a>
                </nav>
            </aside>
            <div class="adm-main">
                <div class="adm-top">
                    <b style="font-size:14px">Bảng điều khiển Nhà cung cấp</b>
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
                    <h1>Quản lý Đơn đặt chỗ &amp; Duyệt thanh toán</h1>
                    <p class="section-sub">Kiểm tra thông tin hành khách, biên lợi nhuận và phê duyệt các đơn đã chuyển khoản qua QR Sandbox.</p>
                    <c:if test="${not empty sessionScope.providerError}">
                        <div class="alert alert-danger mt-3"><c:out value="${sessionScope.providerError}"/></div>
                        <c:remove var="providerError" scope="session"/>
                    </c:if>
                    <div class="alert alert-info mt-3" style="font-size:13px">
                        Chỉ duyệt thanh toán được từ <b>2 ngày trước ngày khởi hành</b> và khi ngày đó đạt số khách tối thiểu. Nếu không đủ, vào <a href="${pageContext.request.contextPath}/provider/dashboard">Tour của tôi</a> → Chi tiết để <b>hủy ngày khởi hành &amp; hoàn tiền</b>. "Từ chối" một đơn sẽ hoàn tiền ngay cho khách đó.
                    </div>
                    <div class="panel mt-4">
                        <div class="panel-head"><h2>Danh sách đơn đặt tour</h2></div>
                        <div class="table-scroll">
                            <table class="adm-table">
                                <thead>
                                    <tr>
                                        <th>Mã đơn</th>
                                        <th>Khách hàng</th>
                                        <th>Tour &amp; Kênh đặt</th>
                                        <th class="c">Số khách</th>
                                        <th class="r">Tổng tiền</th>
                                        <th>Khởi hành, Hành khách &amp; Biên lợi nhuận</th>
                                        <th>Trạng thái &amp; Duyệt</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="b" items="${orders}">
                                        <tr>
                                            <td><span class="bk-id">BK-00${b.bookingId}</span></td>
                                            <td><b><c:out value="${b.customerName}"/></b></td>
                                            <td>
                                                <b style="font-weight:700"><c:out value="${b.tourName}"/></b><br>
                                                <small>Mã Tour: VT-T00${b.tourId} • Kênh: ${empty b.bookingChannel ? 'WEBSITE' : b.bookingChannel}</small>
                                            </td>
                                            <td class="c">${b.guestsCount}</td>
                                            <td class="r fw-semibold"><vt:formatMoney value="${b.totalAmount}" /></td>
                                            <td style="max-width:360px">
                                                <div class="mb-1"><b>Khởi hành:</b> ${empty b.departureDateText ? 'Chưa gán ngày' : b.departureDateText}</div>
                                                <c:if test="${not empty b.passengers}">
                                                    <details class="mb-1">
                                                        <summary style="cursor:pointer">${b.passengers.size()} hành khách (bấm để xem)</summary>
                                                        <c:forEach var="p" items="${b.passengers}">
                                                            <div style="font-size:12px;border-top:1px dashed #ddd;padding:4px 0">
                                                                <b><c:out value="${p.fullName}"/></b><c:if test="${p.representative}"> <span class="badge text-bg-primary">Đại diện</span></c:if>
                                                                · ${p.passengerType == 'CHILD' ? 'Trẻ em' : 'Người lớn'}<c:if test="${not empty p.genderLabel}"> · ${p.genderLabel}</c:if><br>
                                                                Sinh: ${p.dobText}<c:if test="${not empty p.idCard}"> · CCCD/PP: <c:out value="${p.idCard}"/></c:if><c:if test="${not empty p.phone}"> · SĐT: <c:out value="${p.phone}"/></c:if>
                                                            </div>
                                                        </c:forEach>
                                                    </details>
                                                </c:if>
                                                <small><c:choose><c:when test="${empty b.specialRequests}"><span class='text-muted-vt'>Không có ghi chú</span></c:when><c:otherwise><c:out value="${b.specialRequests}"/></c:otherwise></c:choose></small>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${b.paymentStatus == 'PAID'}">
                                                        <span class="pay Paid"><span class="sdot"></span>Đã thanh toán</span>
                                                    </c:when>
                                                    <c:when test="${b.paymentStatus == 'WAITING_APPROVAL'}">
                                                        <div class="mb-1"><span class="status Pending"><span class="sdot"></span>Khách đã CK QR (Chờ duyệt)</span></div>
                                                        <form action="${pageContext.request.contextPath}/provider/booking-orders" method="post" class="act">
                                                            <input type="hidden" name="bookingId" value="${b.bookingId}">
                                                            <button type="submit" name="action" value="approve" class="btn-approve" style="width:auto; padding:0 10px; height:30px; font-size:12px; background: green">Duyệt TT</button>
                                                            <button type="submit" name="action" value="reject" class="btn-reject" style="width:auto; padding:0 10px; height:30px; font-size:12px; background: red">Từ chối</button>
                                                        </form>
                                                    </c:when>
                                                    <c:when test="${b.paymentStatus == 'UNPAID'}">
                                                        <span class="pay Unpaid"><span class="sdot"></span>Chưa thanh toán</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="pay Refunded"><span class="sdot"></span>Đã hủy / Hoàn tiền</span>
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
            </div>
        </div>
    </body>
</html>
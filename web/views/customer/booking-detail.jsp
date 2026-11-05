<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://viettour.com/tags" prefix="vt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết đơn #BK-${booking.bookingId} | VietTour</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
    </head>
    <body>
        <header class="co-header">
            <div class="vt-container">
                <a href="${pageContext.request.contextPath}/home" class="vt-brand">VietTour</a>
                <div class="d-flex align-items-center">
                    <a href="${pageContext.request.contextPath}/customer/history" class="back"><i class="bi bi-arrow-left"></i>Quay lại lịch sử đặt tour</a>
                </div>
            </div>
        </header>
        <main class="vt-container" style="padding-top:36px;padding-bottom:64px">
            <h1 class="co-title">Chi tiết đơn #BK-${booking.bookingId}</h1>
            <c:if test="${not empty tour and tour.approvalStatus != 'APPROVED'}">
                <div class="alert alert-warning mt-3"><i class="bi bi-info-circle me-1"></i>Tour này đang được nhà cung cấp cập nhật. Đơn của bạn vẫn được giữ nguyên giá và thông tin đã đặt.</div>
            </c:if>
            <div class="row g-5 mt-1">
                <div class="col-lg-7">
                    <section class="co-section mb-4">
                        <h2>Thông tin tour</h2>
                        <c:if test="${not empty tour}">
                            <div class="thumb mb-3" style="height:180px;background-size:cover;border-radius:12px;background-image:url('${pageContext.request.contextPath}/static/images/<c:out value="${tour.imageUrl}"/>'), var(--ph)"></div>
                            <div class="fw-bold" style="font-size:18px"><c:out value="${tour.tourName}"/></div>
                            <div class="text-muted-vt mb-2"><i class="bi bi-geo-alt me-1"></i><c:out value="${tour.location}"/> · <i class="bi bi-clock me-1"></i><c:out value="${tour.duration}"/></div>
                            <p style="white-space:pre-line;font-size:14px"><c:out value="${tour.itinerary}"/></p>
                        </c:if>
                    </section>
                    <section class="co-section mb-4">
                        <h2>Danh sách hành khách (${booking.passengers.size()})</h2>
                        <div class="table-scroll">
                            <table class="adm-table">
                                <thead><tr><th>Họ tên</th><th>Loại</th><th>Giới tính</th><th>Ngày sinh</th><th>CCCD / Passport</th><th>SĐT</th></tr></thead>
                                <tbody>
                                    <c:forEach var="p" items="${booking.passengers}">
                                        <tr>
                                            <td><b><c:out value="${p.fullName}"/></b><c:if test="${p.representative}"> <span class="badge text-bg-primary">Đại diện</span></c:if></td>
                                            <td>${p.passengerType == 'CHILD' ? 'Trẻ em' : 'Người lớn'}</td>
                                            <td>${p.genderLabel}</td>
                                            <td>${p.dobText}</td>
                                            <td><c:out value="${p.idCard}"/></td>
                                            <td><c:out value="${p.phone}"/></td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty booking.passengers}"><tr><td colspan="6" class="text-muted-vt">Đơn cũ, chưa có danh sách hành khách.</td></tr></c:if>
                                </tbody>
                            </table>
                        </div>
                    </section>
                </div>
                <div class="col-lg-5">
                    <aside class="summary-card mb-4">
                        <h2>Thông tin đơn</h2>
                        <div class="summary-row"><span>Mã đơn</span><b>#BK-${booking.bookingId}</b></div>
                        <div class="summary-row"><span>Ngày đặt</span><b>${booking.bookingDate}</b></div>
                        <div class="summary-row"><span>Ngày khởi hành</span><b>${empty booking.departureDateText ? 'Chưa gán ngày' : booking.departureDateText}</b></div>
                        <div class="summary-row"><span>Số khách</span><b>${booking.guestsCount}</b></div>
                        <div class="summary-row"><span>Thanh toán</span>
                            <b><c:choose>
                                <c:when test="${booking.paymentStatus == 'PAID'}">Đã thanh toán (đã duyệt)</c:when>
                                <c:when test="${booking.paymentStatus == 'WAITING_APPROVAL'}">Chờ nhà cung cấp duyệt</c:when>
                                <c:when test="${booking.paymentStatus == 'REFUNDED'}">Đã hủy / hoàn tiền</c:when>
                                <c:otherwise>Chưa thanh toán</c:otherwise>
                            </c:choose></b></div>
                        <c:if test="${booking.paymentStatus == 'REFUNDED' and not empty booking.cancelReason}">
                            <div class="alert alert-warning py-2 mt-2" style="font-size:13px"><b>Đơn đã hủy / hoàn tiền.</b> Lý do: <c:out value="${booking.cancelReason}"/></div>
                        </c:if>
                        <div class="pt-3 fw-bold" style="font-size:13px">Tổng tiền</div>
                        <div class="summary-total"><vt:formatMoney value="${booking.totalAmount}" /></div>
                    </aside>
                    <aside class="summary-card">
                        <h2>Liên hệ nhà cung cấp</h2>
                        <c:choose>
                            <c:when test="${not empty provider}">
                                <div class="fw-bold mb-2"><c:out value="${provider.fullName}"/></div>
                                <div class="mb-1"><i class="bi bi-telephone me-2"></i><c:choose><c:when test="${not empty provider.phone}"><a href="tel:<c:out value="${provider.phone}"/>"><c:out value="${provider.phone}"/></a></c:when><c:otherwise>Chưa cập nhật</c:otherwise></c:choose></div>
                                <div class="mb-1"><i class="bi bi-envelope me-2"></i><c:choose><c:when test="${not empty provider.email}"><a href="mailto:<c:out value="${provider.email}"/>"><c:out value="${provider.email}"/></a></c:when><c:otherwise>Chưa cập nhật</c:otherwise></c:choose></div>
                                <div><i class="bi bi-geo-alt me-2"></i><c:out value="${empty provider.companyAddress ? 'Chưa cập nhật địa chỉ' : provider.companyAddress}"/></div>
                            </c:when>
                            <c:otherwise><div class="text-muted-vt">Không có thông tin nhà cung cấp.</div></c:otherwise>
                        </c:choose>
                    </aside>
                </div>
            </div>
        </main>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/static/js/main.js"></script>
    </body>
</html>

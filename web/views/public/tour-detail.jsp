<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://viettour.com/tags" prefix="vt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${tour.tourName}"/> | VietTour</title>
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
            </div>
        </div>
    </header>
    <section class="detail-banner" style="background-image:url('${pageContext.request.contextPath}/static/images/<c:out value="${tour.imageUrl}"/>'), var(--ph)">
        <div class="vt-container">
            <span class="badge-region">Cung cấp bởi: <c:out value="${tour.providerName}"/></span>
            <div class="loc"><i class="bi bi-geo-alt-fill"></i><c:out value="${tour.location}"/></div>
        </div>
    </section>
    <section class="detail" style="padding:28px 0 72px">
        <div class="vt-container">
            <div class="row g-5">
                <div class="col-lg-8">
                    <h1 class="detail-title"><c:out value="${tour.tourName}"/></h1>
                    <div class="detail-meta">
                        <span><i class="bi bi-clock"></i><c:out value="${tour.duration}"/></span>
                        <span><i class="bi bi-geo-alt"></i><c:out value="${tour.location}"/></span>
                        <span><i class="bi bi-calendar-event"></i>${departures.size()} ngày khởi hành còn chỗ</span>
                    </div>
                    <h2>Tổng quan &amp; Lịch trình chi tiết</h2>
                    <p style="white-space:pre-line"><c:out value="${tour.itinerary}"/></p>
                </div>
                <div class="col-lg-4">
                    <form class="booking-card" action="${pageContext.request.contextPath}/customer/checkout" method="get" id="bookForm"
                          data-price="${tour.price}" data-max="${tour.availableSeats}" data-default="1">
                        <input type="hidden" name="tourId" value="${tour.tourId}">
                        <input type="hidden" name="guests" id="guestsInput" value="1">
                        <div class="fw-bold mb-2" style="font-size:15px">Đặt hành trình của bạn</div>
                        <div class="price"><b><vt:formatMoney value="${tour.price}" /></b></div>
                        <div class="text-muted-vt mt-1" style="font-size:12px">/ khách · <c:out value="${tour.duration}"/></div>
                        
                        <div class="form-label mt-3">Chọn ngày khởi hành</div>
                        <c:choose>
                            <c:when test="${not empty departures}">
                                <select class="form-select" name="departureId" id="departureSelect" required>
                                    <c:forEach var="d" items="${departures}">
                                        <option value="${d.departureId}" data-seats="${d.seatsLeft}" ${selectedDepartureId == d.departureId ? 'selected' : ''}>${d.dateText} · còn ${d.seatsLeft} chỗ</option>
                                    </c:forEach>
                                </select>
                            </c:when>
                            <c:otherwise>
                                <div class="alert alert-warning py-2 mb-0" style="font-size:13px">Tour hiện chưa có ngày khởi hành nào còn chỗ.</div>
                            </c:otherwise>
                        </c:choose>

                        <div class="form-label mt-3">Số lượng hành khách</div>
                        <div class="stepper mb-3">
                            <button type="button" id="btnMinus"><i class="bi bi-dash-lg"></i></button>
                            <span id="guestsText">1</span>
                            <button type="button" id="btnPlus"><i class="bi bi-plus-lg"></i></button>
                        </div>
                        <div class="total-line mb-3">
                            <span>Tạm tính · <span id="totalGuests">1</span> khách</span>
                            <b id="totalPrice"><vt:formatMoney value="${tour.price}" /></b>
                        </div>

                        <c:choose>
                            <c:when test="${not empty sessionScope.account and empty departures}">
                                <button type="button" class="btn-vt-accent btn-vt-lg" disabled>Tạm hết chỗ</button>
                            </c:when>
                            <c:when test="${not empty sessionScope.account}">
                                <button type="submit" class="btn-vt-accent btn-vt-lg">Đặt Tour ngay <i class="bi bi-arrow-right"></i></button>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/login" class="btn-vt-accent btn-vt-lg text-center" style="display:block;line-height:54px;">Đăng nhập để đặt Tour</a>
                            </c:otherwise>
                        </c:choose>
                    </form>
                </div>
            </div>
        </div>
    </section>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/main.js"></script>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://viettour.com/tags" prefix="vt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Danh sách tour | VietTour</title>
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
                        <li><a href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                        <li><a href="${pageContext.request.contextPath}/tours" class="active">Danh sách Tour</a></li>
                    </ul>
                </div>
                <div>
                    <c:choose>
                        <c:when test="${not empty sessionScope.account}">
                            <a href="${pageContext.request.contextPath}/customer/history" class="btn-vt-outline">Lịch sử đặt tour</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/login" class="btn-vt">Đăng nhập / Đăng ký</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </header>

        <section class="tour-list" style="padding:40px 0 64px">
            <div class="vt-container">
                <div class="breadcrumb-vt mb-4">
                    <a href="${pageContext.request.contextPath}/home" class="text-muted-vt">Trang chủ</a><span>/</span><span style="color:var(--vt-green)">Danh sách Tour</span>
                </div>
                <h1 class="section-title">Danh sách tour đang mở bán</h1>
                <div class="row g-4 mt-2">
                    <c:forEach var="t" items="${tourList}">
                        <c:if test="${t.availableSeats > 0}">
                            <div class="col-12 col-md-6 col-lg-4">
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
                                            <div class="seats"><i class="bi bi-people"></i>Còn ${t.availableSeats} chỗ</div>
                                        </div>
                                        <a href="${pageContext.request.contextPath}/tour-detail?id=${t.tourId}" class="btn-vt-outline">Xem chi tiết</a>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
        </section>
        <footer class="vt-footer"><div class="vt-container"><div class="legal"><span>© 2026 VietTour.</span></div></div></footer>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/static/js/main.js"></script>
    </body>
</html>
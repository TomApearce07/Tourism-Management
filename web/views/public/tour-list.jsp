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

                <%-- Form tìm kiếm nằm NGOÀI vùng data-live để không bị làm mới khi đang gõ --%>
                <form class="list-search" action="${pageContext.request.contextPath}/tours" method="get">
                    <input type="text" class="form-control search-main" name="keyword" maxlength="100" value="<c:out value="${keyword}"/>" placeholder="Bạn muốn đi đâu? (vd: Hạ Long, Sapa...)">
                    <input type="date" class="form-control" style="max-width:190px" name="date" value="<c:out value="${dateValue}"/>" title="Ngày khởi hành">
                    <button type="submit" class="btn-vt-accent" style="padding:0 20px"><i class="bi bi-search me-1"></i>Tìm kiếm</button>
                    <c:if test="${not empty keyword or not empty dateValue}"><a href="${pageContext.request.contextPath}/tours" class="btn-vt-outline" style="display:inline-flex;align-items:center;padding:0 16px">Xóa lọc</a></c:if>
                </form>
                <c:if test="${not empty dateError}"><div class="alert alert-warning py-2"><c:out value="${dateError}"/></div></c:if>

                <div class="row g-4 mt-2" data-live="tours">
                    <c:if test="${empty tourList}"><div class="col-12 text-muted-vt">Không tìm thấy tour phù hợp. Hãy thử từ khóa hoặc ngày khác.</div></c:if>
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
                                            <%@ include file="card-extra.jspf" %>
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
        <script src="${pageContext.request.contextPath}/static/js/live-refresh.js"></script>
    </body>
</html>
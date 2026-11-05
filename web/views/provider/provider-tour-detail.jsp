<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://viettour.com/tags" prefix="vt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Chi tiết &amp; Chỉnh sửa Tour | VietTour</title>
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
                    <a href="${pageContext.request.contextPath}/provider/dashboard" class="active"><i class="bi bi-map"></i>Tour của tôi</a>
                    <a href="${pageContext.request.contextPath}/provider/booking-orders"><i class="bi bi-ticket-perforated"></i>Đơn đặt chỗ</a>
                </nav>
            </aside>
            <div class="adm-main">
                <div class="adm-top">
                    <b style="font-size:14px">Bảng điều khiển Nhà cung cấp / Chi tiết &amp; Chỉnh sửa Tour</b>
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
                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">
                        <div>
                            <h1>Chi tiết &amp; Chỉnh sửa Tour #VT-T00${tour.tourId}</h1>
                            <p class="section-sub text-danger fw-semibold">
                                <i class="bi bi-exclamation-triangle-fill me-1"></i> Lưu ý: Mỗi lần bạn chỉnh sửa và bấm Lưu, Tour sẽ tự động chuyển về trạng thái <b>Chờ duyệt (PENDING)</b> để Manager kiểm duyệt lại trước khi hiển thị.
                            </p>
                        </div>
                        <a href="${pageContext.request.contextPath}/provider/dashboard" class="btn-cancel d-inline-flex align-items-center gap-2">
                            <i class="bi bi-arrow-left"></i> Quay lại
                        </a>
                    </div>

                    <div class="panel p-4 mt-3">
                        <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">
                            <div>
                                <b>Trạng thái hiện tại:</b>
                                <c:choose>
                                    <c:when test="${tour.approvalStatus == 'APPROVED'}"><span class="status Approved ms-2"><span class="sdot"></span>Đã duyệt</span></c:when>
                                    <c:when test="${tour.approvalStatus == 'PENDING'}"><span class="status Pending ms-2"><span class="sdot"></span>Chờ duyệt</span></c:when>
                                    <c:otherwise><span class="status Rejected ms-2"><span class="sdot"></span>Từ chối</span></c:otherwise>
                                </c:choose>
                            </div>
                            <div>
                                Giá bán: <b><vt:formatMoney value="${tour.price}" /></b> &nbsp;|&nbsp;
                                Giá vốn: <b><vt:formatMoney value="${tour.unitCost}" /></b> &nbsp;|&nbsp;
                                Lợi nhuận dự kiến: <b style="color:var(--vt-green)"><vt:formatMoney value="${tour.unitProfit}" /></b>
                            </div>
                        </div>
                        <hr>

                        <c:if test="${not empty sessionScope.providerError}">
                        <div class="alert alert-danger"><c:out value="${sessionScope.providerError}"/></div>
                        <c:remove var="providerError" scope="session"/>
                    </c:if>
                    <form action="${pageContext.request.contextPath}/provider/tour-detail" method="post" enctype="multipart/form-data">
                            <input type="hidden" name="tourId" value="${tour.tourId}">

                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="f-label">Tên Tour *</label>
                                    <input class="f-input" type="text" name="tourName" value="<c:out value="${tour.tourName}"/>" required>
                                </div>
                                <div class="col-md-3">
                                    <label class="f-label">Địa điểm *</label>
                                    <input class="f-input" type="text" name="location" value="<c:out value="${tour.location}"/>" required>
                                </div>
                                <div class="col-md-3">
                                    <label class="f-label">Thời lượng *</label>
                                    <input class="f-input" type="text" name="duration" value="<c:out value="${tour.duration}"/>" required>
                                </div>

                                <div class="col-md-3">
                                    <label class="f-label">Giá bán công bố (VND) *</label>
                                    <input class="f-input" type="number" name="price" value="${tour.price}" required>
                                </div>
                                <div class="col-md-3">
                                    <label class="f-label">Chi phí Vé &amp; Xe (VND)</label>
                                    <input class="f-input" type="number" name="transportCost" value="${tour.transportCost}" required>
                                </div>
                                <div class="col-md-3">
                                    <label class="f-label">Chi phí Khách sạn (VND)</label>
                                    <input class="f-input" type="number" name="hotelCost" value="${tour.hotelCost}" required>
                                </div>
                                <div class="col-md-3">
                                    <label class="f-label">Chi phí Ăn uống &amp; HDV (VND)</label>
                                    <input class="f-input" type="number" name="mealGuideCost" value="${tour.mealGuideCost}" required>
                                </div>

                                <div class="col-md-4">
                                    <label class="f-label">Tổng số chỗ khả dụng *</label>
                                    <input class="f-input" type="number" name="availableSeats" value="${tour.availableSeats}" required>
                                </div>
                                <div class="col-md-8">
                                    <label class="f-label">Cập nhật Hình ảnh mới (Bỏ trống nếu giữ ảnh cũ: <c:out value="${tour.imageUrl}"/>)</label>
                                    <input type="file" name="imageFile" class="form-control" accept="image/jpeg,image/png,image/webp,image/gif">
                                </div>

                                <div class="col-12">
                                    <label class="f-label">Lịch trình &amp; Mô tả chi tiết *</label>
                                    <textarea class="f-input" name="itinerary" rows="6" required><c:out value="${tour.itinerary}"/></textarea>
                                </div>

                                <div class="col-12 d-flex justify-content-end gap-2 mt-4">
                                    <a href="${pageContext.request.contextPath}/provider/dashboard" class="btn-cancel d-inline-flex align-items-center">Hủy</a>
                                    <button type="submit" class="btn-add"><i class="bi bi-check2-circle"></i> Lưu chỉnh sửa &amp; Gửi Manager duyệt</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
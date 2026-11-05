<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://viettour.com/tags" prefix="vt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Tour của tôi | VietTour</title>
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
                    <a href="#" data-bs-toggle="modal" data-bs-target="#createTourModal"><i class="bi bi-plus-lg"></i>Tạo Tour mới</a>
                    <a href="${pageContext.request.contextPath}/provider/booking-orders"><i class="bi bi-ticket-perforated"></i>Đơn đặt chỗ</a>
                </nav>
            </aside>
            <div class="adm-main">
                <div class="adm-top">
                    <b style="font-size:14px">Bảng điều khiển Nhà cung cấp</b>
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
                                    <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-primary">Đăng nhập</a>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="adm-content">
                    <div class="d-flex justify-content-between align-items-start flex-wrap gap-2">
                        <div>
                            <h1>Danh mục Tour của tôi</h1>
                            <p class="section-sub">Quản lý danh sách tour, xem chi tiết, chỉnh sửa thông tin và theo dõi biên lợi nhuận từng hành trình.</p>
                        </div>
                        <button type="button" class="btn-add" data-bs-toggle="modal" data-bs-target="#createTourModal"><i class="bi bi-plus-lg"></i>Thêm Tour mới</button>
                    </div>

                    <c:if test="${not empty sessionScope.providerError}">
                        <c:set var="providerErrorMsg" value="${sessionScope.providerError}" scope="request"/>
                        <c:remove var="providerError" scope="session"/>
                    </c:if>
                    <c:if test="${not empty providerErrorMsg}">
                        <div class="alert alert-danger"><c:out value="${providerErrorMsg}"/></div>
                    </c:if>
                    <c:if test="${param.updated == 'true'}">
                        <div class="alert alert-warning mt-3 mb-0">
                            <i class="bi bi-info-circle me-1"></i> Đã lưu chỉnh sửa Tour! Trạng thái của Tour đã tự động chuyển về <b>Chờ duyệt (PENDING)</b> để Manager xét duyệt lại.
                        </div>
                    </c:if>

                    <div class="row g-3 my-4">
                        <div class="col-md-4"><div class="stat-card">
                                <div class="top">Tour đang hoạt động<i class="bi bi-map"></i></div>
                                <div class="num">${activeToursCount}</div>
                            </div></div>
                        <div class="col-md-4"><div class="stat-card">
                                <div class="top">Đơn đã thanh toán<i class="bi bi-ticket-perforated"></i></div>
                                <div class="num">${paidBookingsCount}</div>
                            </div></div>
                        <div class="col-md-4"><div class="stat-card">
                                <div class="top">Doanh thu thực nhận<i class="bi bi-wallet2"></i></div>
                                <div class="num"><vt:formatMoney value="${providerRevenue}" /></div>
                            </div></div>
                    </div>
                    <div class="panel">
                        <div class="panel-head"><h2>Danh sách Tour cung cấp</h2></div>
                        <div class="table-scroll">
                            <table class="adm-table">
                                <thead>
                                    <tr>
                                        <th>Tên Tour / Địa điểm</th>
                                        <th>Thời lượng</th>
                                        <th class="c">Chỗ trống</th>
                                        <th class="r">Giá bán</th>
                                        <th class="r">Giá vốn / Lợi nhuận</th>
                                        <th>Trạng thái duyệt</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="t" items="${myTours}">
                                        <tr>
                                            <td>
                                                <div class="tour-cell">
                                                    <div class="thumb" style="background-image:url('${pageContext.request.contextPath}/static/images/<c:out value="${t.imageUrl}"/>'), var(--ph)"></div>
                                                    <div><b><c:out value="${t.tourName}"/></b><small><c:out value="${t.location}"/></small></div>
                                                </div>
                                            </td>
                                            <td><c:out value="${t.duration}"/></td>
                                            <td class="c"><b>${t.availableSeats}</b> chỗ</td>
                                            <td class="r fw-semibold"><vt:formatMoney value="${t.price}" /></td>
                                            <td class="r">
                                                <small>Vốn: <vt:formatMoney value="${t.unitCost}" /></small><br>
                                                <b style="color:var(--vt-green)">Lãi: <vt:formatMoney value="${t.unitProfit}" /></b>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${t.approvalStatus == 'APPROVED'}"><span class="status Approved"><span class="sdot"></span>Đã duyệt</span></c:when>
                                                    <c:when test="${t.approvalStatus == 'PENDING'}"><span class="status Pending"><span class="sdot"></span>Chờ duyệt</span></c:when>
                                                    <c:otherwise><span class="status Rejected"><span class="sdot"></span>Từ chối</span></c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/provider/tour-detail?id=${t.tourId}" class="btn-mini">
                                                    <i class="bi bi-pencil-square me-1"></i>Chi tiết / Sửa
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

        <div class="modal fade" id="createTourModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered" style="max-width:720px">
                <form class="modal-content vt-modal" method="post" action="${pageContext.request.contextPath}/provider/create-tour" enctype="multipart/form-data">
                <c:if test="${not empty providerErrorMsg}"><div class="alert alert-danger m-3 mb-0"><c:out value="${providerErrorMsg}"/></div></c:if>
                    <div class="modal-header">
                        <h5 class="modal-title">Tạo Tour Du Lịch Mới</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row g-3">
                            <div class="col-md-6">
                                <label class="f-label">Tên Tour</label>
                                <input class="f-input" type="text" name="tourName" required>
                            </div>
                            <div class="col-md-6">
                                <label class="f-label">Địa điểm</label>
                                <input class="f-input" type="text" name="location" required>
                            </div>
                            <div class="col-md-6">
                                <label class="f-label">Giá bán công bố (VND/khách)</label>
                                <input class="f-input" type="number" name="price" required>
                            </div>
                            <div class="col-md-6">
                                <label class="f-label">Thời lượng</label>
                                <input class="f-input" type="text" name="duration" placeholder="2 ngày 1 đêm" required>
                            </div>
                            <div class="col-md-4">
                                <label class="f-label">Chi phí Vé &amp; Xe (VND/khách)</label>
                                <input class="f-input" type="number" name="transportCost" value="0" required>
                            </div>
                            <div class="col-md-4">
                                <label class="f-label">Chi phí Khách sạn (VND/khách)</label>
                                <input class="f-input" type="number" name="hotelCost" value="0" required>
                            </div>
                            <div class="col-md-4">
                                <label class="f-label">Chi phí Ăn uống &amp; HDV</label>
                                <input class="f-input" type="number" name="mealGuideCost" value="0" required>
                            </div>
                            <div class="col-md-6">
                                <label class="f-label">Tỷ lệ đăng ký tối thiểu để tour khởi hành (%) *</label>
                                <input class="f-input" type="number" name="minFillPercent" value="50" min="0" max="100" required>
                            </div>
                            <div class="col-md-6 d-flex align-items-end"><small class="text-muted-vt">Ví dụ 50% với 20 chỗ: cần ≥ 10 khách đã đăng ký. Khách sẽ thấy yêu cầu này ngay ở danh sách tour. Nếu không đủ, bạn hủy ngày khởi hành và hệ thống hoàn tiền cho khách.</small></div>
                            <div class="col-12" data-dep-box>
                                <label class="f-label">Ngày khởi hành &amp; số chỗ (mỗi ngày có số chỗ riêng) *</label>
                                <div data-dep-rows>
                                    <div class="d-flex gap-2 mb-2" data-dep-row>
                                        <input class="f-input" type="date" name="departureDate" required>
                                        <input class="f-input" type="number" name="departureSeats" min="1" placeholder="Số chỗ" required>
                                        <button type="button" class="btn-mini" data-dep-remove title="Xóa dòng"><i class="bi bi-x-lg"></i></button>
                                    </div>
                                </div>
                                <button type="button" class="btn-mini" data-dep-add><i class="bi bi-plus-lg me-1"></i>Thêm ngày khởi hành</button>
                            </div>
                            <div class="col-12">
                                <label class="f-label">Lịch trình &amp; Mô tả chi tiết</label>
                                <textarea class="f-input" name="itinerary" rows="4" required></textarea>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Hình ảnh Tour</label>
                                <input type="file" name="imageFile" class="form-control" accept="image/jpeg,image/png,image/webp,image/gif" required>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn-cancel" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn-add">Gửi yêu cầu phê duyệt</button>
                    </div>
                </form>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
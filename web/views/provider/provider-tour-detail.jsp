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
                                    <label class="f-label">Tỷ lệ đăng ký tối thiểu (%) *</label>
                                    <input class="f-input" type="number" name="minFillPercent" value="${tour.minFillPercent}" min="0" max="100" required>
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

                    <div class="panel p-4 mt-3">
                        <h2 style="font-size:16px">Ngày khởi hành &amp; số chỗ</h2>
                        <p class="section-sub">Thêm / đóng / đổi số chỗ từng ngày <b>không cần Manager duyệt lại</b>. Không thể giảm số chỗ xuống thấp hơn số chỗ đã giữ hoặc đã bán.</p>
                        <c:if test="${param.dep == 'ok'}"><div class="alert alert-success py-2">Đã cập nhật ngày khởi hành.</div></c:if>
                        <c:if test="${param.dep == 'cancelled'}"><div class="alert alert-success py-2">Đã hủy và hoàn tiền ngay cho ${param.n} đơn; lý do đã được gửi tới các khách.</div></c:if>
                        <c:if test="${param.dep == 'approved'}"><div class="alert alert-success py-2">Đã xác nhận ${param.n} đơn: tour chắc chắn khởi hành.</div></c:if>
                        <div class="alert alert-info py-2" style="font-size:13px">
                            Quy tắc: yêu cầu tối thiểu <b>${tour.minFillPercent}%</b> số chỗ mỗi ngày. Từ <b>${2} ngày trước khởi hành</b> bạn xem số khách đăng ký thực tế:
                            đủ thì bấm <b>Xác nhận khởi hành</b> (đơn thành <i>Đã thanh toán</i>); không đủ thì bấm <b>Hủy &amp; hoàn tiền</b> (nhập lý do, tiền hoàn ngay cho khách).
                        </div>
                        <div class="table-scroll">
                            <table class="adm-table">
                                <thead><tr><th>Ngày khởi hành</th><th class="c">Tổng chỗ</th><th class="c">Đã giữ / bán</th><th class="c">Còn lại</th><th class="c">Đăng ký thực tế / Cần</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>
                                <tbody>
                                    <c:forEach var="d" items="${departures}">
                                        <tr>
                                            <td><b>${d.dateText}</b></td>
                                            <td class="c">
                                                <form action="${pageContext.request.contextPath}/provider/departures" method="post" class="d-inline-flex gap-1 justify-content-center">
                                                    <input type="hidden" name="action" value="update">
                                                    <input type="hidden" name="tourId" value="${tour.tourId}">
                                                    <input type="hidden" name="departureId" value="${d.departureId}">
                                                    <input class="f-input" style="width:90px" type="number" name="totalSeats" min="1" value="${d.totalSeats}" required>
                                                    <button type="submit" class="btn-mini">Lưu</button>
                                                </form>
                                            </td>
                                            <td class="c">${d.seatsTaken}</td>
                                            <td class="c"><b>${d.seatsLeft}</b></td>
                                            <td class="c">
                                                <b>${d.registered}</b> / ${d.requiredGuests}
                                                <c:choose>
                                                    <c:when test="${d.thresholdMet}"><span class="badge text-bg-success">Đủ</span></c:when>
                                                    <c:otherwise><span class="badge text-bg-warning">Chưa đủ</span></c:otherwise>
                                                </c:choose><br>
                                                <small class="text-muted-vt">Quyết định từ ${d.decisionDateText}</small>
                                            </td>
                                            <td><c:choose>
                                                    <c:when test="${d.status == 'OPEN'}"><span class="status Approved"><span class="sdot"></span>Đang mở</span></c:when>
                                                    <c:when test="${d.status == 'CANCELLED'}"><span class="status Rejected"><span class="sdot"></span>Đã hủy</span></c:when>
                                                    <c:otherwise><span class="status Rejected"><span class="sdot"></span>Đã đóng</span></c:otherwise>
                                                </c:choose></td>
                                            <td>
                                                <c:if test="${d.status != 'CANCELLED'}">
                                                    <form action="${pageContext.request.contextPath}/provider/departures" method="post" class="d-flex flex-wrap gap-1">
                                                        <input type="hidden" name="tourId" value="${tour.tourId}">
                                                        <input type="hidden" name="departureId" value="${d.departureId}">
                                                        <c:if test="${d.registered > 0}">
                                                            <button type="submit" name="action" value="approveAll" class="btn-mini" style="background:#198754;color:#fff"
                                                                    ${(d.decisionOpen and d.thresholdMet) ? '' : 'disabled title="Chỉ bấm được khi tới hạn quyết định và đủ số khách tối thiểu"'}>Xác nhận khởi hành</button>
                                                        </c:if>
                                                        <c:choose>
                                                            <c:when test="${d.status == 'OPEN'}"><button type="submit" name="action" value="close" class="btn-mini">Đóng bán</button></c:when>
                                                            <c:otherwise><button type="submit" name="action" value="open" class="btn-mini">Mở lại</button></c:otherwise>
                                                        </c:choose>
                                                        <button type="button" class="btn-mini" style="background:#c0392b;color:#fff" data-bs-toggle="modal" data-bs-target="#cancelModal"
                                                                data-cancel-action="cancel" data-cancel-dep="${d.departureId}" data-cancel-title="Hủy ngày khởi hành ${d.rangeText}">Hủy &amp; hoàn tiền</button>
                                                    </form>
                                                </c:if>
                                                <c:if test="${d.status == 'CANCELLED'}"><span class="text-muted-vt">—</span></c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty departures}"><tr><td colspan="7" class="text-muted-vt">Chưa có ngày khởi hành nào. Hãy thêm bên dưới để khách có thể đặt tour.</td></tr></c:if>
                                </tbody>
                            </table>
                        </div>
                        <form action="${pageContext.request.contextPath}/provider/departures" method="post" class="mt-3" data-dep-box>
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="tourId" value="${tour.tourId}">
                            <label class="f-label">Thêm ngày khởi hành mới</label>
                            <div data-dep-rows>
                                <div class="d-flex gap-2 mb-2" data-dep-row>
                                    <input class="f-input" type="date" name="departureDate" required>
                                    <input class="f-input" type="number" name="departureSeats" min="1" placeholder="Số chỗ" required>
                                    <button type="button" class="btn-mini" data-dep-remove title="Xóa dòng"><i class="bi bi-x-lg"></i></button>
                                </div>
                            </div>
                            <button type="button" class="btn-mini" data-dep-add><i class="bi bi-plus-lg me-1"></i>Thêm dòng</button>
                            <button type="submit" class="btn-add ms-2">Lưu ngày khởi hành</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <div class="modal fade" id="cancelModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <form class="modal-content vt-modal" method="post" action="${pageContext.request.contextPath}/provider/departures">
                    <input type="hidden" name="tourId" value="${tour.tourId}">
                    <input type="hidden" name="action" id="cancelAction" value="cancel">
                    <input type="hidden" name="departureId" id="cancelDepId" value="">
                    <div class="modal-header">
                        <h5 class="modal-title" id="cancelTitle">Ngừng cung cấp</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="alert alert-danger py-2" style="font-size:13px">Tiền sẽ được <b>hoàn NGAY</b> cho tất cả khách đã đặt và <b>lý do bên dưới được gửi tới từng khách</b>. Không thể hoàn tác.</div>
                        <label class="f-label">Lý do ngừng cung cấp *</label>
                        <textarea class="f-input" name="reason" rows="4" minlength="5" maxlength="400" required placeholder="Ví dụ: Số lượng người đăng ký chưa đạt mức tối thiểu 50% để khởi hành..."></textarea>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn-cancel" data-bs-dismiss="modal">Quay lại</button>
                        <button type="submit" class="btn-add" style="background:#c0392b">Xác nhận hủy &amp; hoàn tiền</button>
                    </div>
                </form>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://viettour.com/tags" prefix="vt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thanh toán đặt chỗ | VietTour</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
    </head>
    <body>
        <header class="co-header">
            <div class="vt-container">
                <a href="${pageContext.request.contextPath}/home" class="vt-brand">VietTour</a>
                <div class="d-flex align-items-center">
                    <a href="${pageContext.request.contextPath}/tour-detail?id=${tour.tourId}" class="back"><i class="bi bi-arrow-left"></i>Quay lại chi tiết Tour</a>
                </div>
            </div>
        </header>
        <main class="vt-container" style="padding-top:36px;padding-bottom:64px">
            <h1 class="co-title">Xác nhận Đặt chỗ &amp; Thanh toán</h1>
            <form action="${pageContext.request.contextPath}/customer/checkout" method="post" class="mt-4" id="checkoutForm">
                <c:if test="${not empty error}"><div class="alert-vt"><c:out value="${error}"/></div></c:if>
                <input type="hidden" name="tourId" value="${tour.tourId}">
                <input type="hidden" name="guestsCount" value="${guests}">
                <div class="row g-5">
                    <div class="col-lg-7">
                        <section class="co-section mb-5">
                            <h2>Yêu cầu đặc biệt</h2>
                            <textarea class="form-control mt-3" name="specialRequests" rows="4" placeholder="Ghi chú ăn chay, dị ứng thực phẩm, yêu cầu phòng nghỉ..."></textarea>
                        </section>
                        <section class="co-section">
                            <h2 class="mb-3">Phương thức thanh toán (Sandbox)</h2>
                            <label class="pay-option">
                                <input type="radio" name="paymentMethod" value="VNPAY_SANDBOX" checked>
                                <span class="flex-grow-1"><b>VNPAY Sandbox (QR Code)</b><small>Quét mã VietQR trên môi trường thử nghiệm VNPAY</small></span>
                            </label>
                            <label class="pay-option">
                                <input type="radio" name="paymentMethod" value="MOMO_SANDBOX">
                                <span class="flex-grow-1"><b>MoMo Sandbox (QR Code)</b><small>Quét mã QR trên môi trường thử nghiệm MoMo</small></span>
                            </label>
                        </section>
                    </div>
                    <div class="col-lg-5">
                        <aside class="summary-card">
                            <h2>Tóm tắt đơn đặt tour</h2>
                            <div class="thumb" style="background-image:url('${pageContext.request.contextPath}/static/images/<c:out value="${tour.imageUrl}"/>'), var(--ph)"></div>
                            <div class="fw-bold mb-1" style="font-size:18px"><c:out value="${tour.tourName}"/></div>
                            <div class="pt-3 pb-2" style="border-bottom:1px solid var(--vt-border)">
                                <div class="summary-row"><span>Số lượng khách</span><b>${guests} khách</b></div>
                                <div class="summary-row"><span>Đơn giá gốc / khách</span><b><vt:formatMoney value="${tour.price}" /></b></div>
                                <div class="summary-row"><span>Chỗ trống hiện tại</span><b>Còn ${tour.availableSeats} chỗ</b></div>
                            </div>
                            <div class="pt-3 fw-bold" style="font-size:13px">Tổng thanh toán (Đã tính hệ số mùa &amp; chiết khấu đoàn)</div>
                            <div class="summary-total"><vt:formatMoney value="${totalAmount}" /></div>

                            <button type="button" class="btn-vt w-100" data-bs-toggle="modal" data-bs-target="#customerInfoModal">
                                Tiến hành thanh toán
                            </button>
                        </aside>
                    </div>
                </div>

                <div class="modal fade" id="customerInfoModal" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content vt-modal">
                            <div class="modal-header">
                                <h5 class="modal-title">Thông tin hành khách đặt Tour</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <div class="mb-3">
                                    <label class="f-label">Họ và tên người đại diện</label>
                                    <input type="text" class="f-input" name="guestFullName" value="<c:out value="${sessionScope.account.fullName}"/>" required>
                                </div>
                                <div class="mb-3">
                                    <label class="f-label">Số điện thoại liên hệ</label>
                                    <input type="tel" class="f-input" name="customerPhone" value="<c:out value="${sessionScope.account.phone}"/>" placeholder="Nhập số điện thoại (10 số)" required>
                                </div>
                                <div class="mb-3">
                                    <label class="f-label">Số CCCD / Hộ chiếu (Passport)</label>
                                    <input type="text" class="f-input" name="customerIdCard" placeholder="Nhập số CCCD hoặc Passport" required>
                                </div>
                                <div class="mb-2">
                                    <label class="f-label">Kênh tiếp nhận đặt chỗ</label>
                                    <select class="form-select" name="bookingChannel">
                                        <option value="WEBSITE" selected>Website trực tuyến VietTour</option>
                                        <option value="OTA_KLOOK_AGODA">Kênh đối tác OTA (Klook / Agoda / Viator)</option>
                                        <option value="ZALO_HOTLINE">Tư vấn qua Zalo / Điện thoại</option>
                                        <option value="AGENCY">Đại lý lữ hành</option>
                                    </select>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn-cancel" data-bs-dismiss="modal">Quay lại</button>
                                <button type="submit" class="btn-add">Xác nhận &amp; Mở cổng QR Sandbox</button>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </main>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/static/js/main.js"></script>
    </body>
</html>
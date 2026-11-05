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
                <input type="hidden" name="departureId" value="${departure.departureId}">
                <input type="hidden" name="guestsCount" value="${guests}">
                <div class="row g-5">
                    <div class="col-lg-7">
                        <section class="co-section mb-5" id="paxSection" data-depdate="${departure.departureDate}">
                            <h2>Danh sách hành khách (${guests} người)</h2>
                            <p class="text-muted-vt" style="font-size:13px">Nhập thông tin đúng như giấy tờ tùy thân. Hành khách 1 là người đại diện đặt tour. Người dưới 12 tuổi tại ngày khởi hành được tính là trẻ em (CCCD không bắt buộc).</p>
                            <c:forEach begin="0" end="${guests - 1}" var="i">
                                <c:set var="pn" value="${paramValues.passengerName[i]}"/>
                                <c:set var="pp" value="${paramValues.passengerPhone[i]}"/>
                                <c:if test="${i == 0 and empty paramValues.passengerName}">
                                    <c:set var="pn" value="${sessionScope.account.fullName}"/>
                                    <c:set var="pp" value="${sessionScope.account.phone}"/>
                                </c:if>
                                <div class="pax-row border rounded p-3 mb-3">
                                    <div class="fw-semibold mb-2">Hành khách ${i + 1}<c:if test="${i == 0}"> (người đại diện)</c:if>
                                        <span class="badge text-bg-light ms-2 pax-type"></span></div>
                                    <div class="row g-2">
                                        <div class="col-md-6">
                                            <label class="f-label">Họ và tên *</label>
                                            <input class="f-input" type="text" name="passengerName" maxlength="100" value="<c:out value="${pn}"/>" required ${i == 0 ? 'data-rep="name"' : ''}>
                                        </div>
                                        <div class="col-md-3">
                                            <label class="f-label">Giới tính</label>
                                            <select class="form-select" name="passengerGender">
                                                <option value="">--</option>
                                                <option value="MALE" ${paramValues.passengerGender[i] == 'MALE' ? 'selected' : ''}>Nam</option>
                                                <option value="FEMALE" ${paramValues.passengerGender[i] == 'FEMALE' ? 'selected' : ''}>Nữ</option>
                                                <option value="OTHER" ${paramValues.passengerGender[i] == 'OTHER' ? 'selected' : ''}>Khác</option>
                                            </select>
                                        </div>
                                        <div class="col-md-3">
                                            <label class="f-label">Ngày sinh *</label>
                                            <input class="f-input pax-dob" type="date" name="passengerDob" value="<c:out value="${paramValues.passengerDob[i]}"/>" required>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="f-label">CCCD / Passport <span class="pax-id-req">*</span></label>
                                            <input class="f-input pax-id" type="text" name="passengerIdCard" maxlength="20" pattern="[A-Za-z0-9]{8,20}" value="<c:out value="${paramValues.passengerIdCard[i]}"/>" ${i == 0 ? 'data-rep="id"' : ''}>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="f-label">Số điện thoại (tùy chọn)</label>
                                            <input class="f-input" type="tel" name="passengerPhone" maxlength="11" value="<c:out value="${pp}"/>" ${i == 0 ? 'data-rep="phone"' : ''}>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </section>
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
                                <div class="summary-row"><span>Ngày khởi hành</span><b>${departure.dateText}</b></div>
                                <div class="summary-row"><span>Số lượng khách</span><b>${guests} khách</b></div>
                                <div class="summary-row"><span>Đơn giá gốc / khách</span><b><vt:formatMoney value="${tour.price}" /></b></div>
                                <div class="summary-row"><span>Chỗ trống ngày này</span><b>Còn ${departure.seatsLeft} chỗ</b></div>
                            </div>
                            <c:if test="${tour.minFillPercent > 0}">
                                <div class="min-note my-2">
                                    <i class="bi bi-info-circle"></i><span>Tour chỉ khởi hành khi ngày này có tối thiểu <b>${tour.minFillPercent}%</b> số chỗ được đăng ký
                                    (cần ≥ <b>${departure.requiredGuests}</b> khách, hiện có ${departure.registered}). Khoảng ${departure.decisionDateText} nhà cung cấp sẽ xác nhận;
                                    nếu không đủ, bạn được hoàn 100% tiền và nhận thông báo lý do.</span>
                                </div>
                            </c:if>
                            <c:if test="${not empty provider}">
                                <div class="py-2" style="border-bottom:1px solid var(--vt-border);font-size:13px">
                                    <div class="fw-bold mb-1">Liên hệ nhà cung cấp</div>
                                    <div><c:out value="${provider.fullName}"/></div>
                                    <c:if test="${not empty provider.phone}"><div><i class="bi bi-telephone me-1"></i><a href="tel:<c:out value="${provider.phone}"/>"><c:out value="${provider.phone}"/></a></div></c:if>
                                    <c:if test="${not empty provider.email}"><div><i class="bi bi-envelope me-1"></i><a href="mailto:<c:out value="${provider.email}"/>"><c:out value="${provider.email}"/></a></div></c:if>
                                    <c:if test="${not empty provider.companyAddress}"><div><i class="bi bi-geo-alt me-1"></i><c:out value="${provider.companyAddress}"/></div></c:if>
                                </div>
                            </c:if>
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
                                <p class="text-muted-vt" style="font-size:12px">Thông tin người đại diện được đồng bộ với "Hành khách 1" ở trên.</p>
                                <div class="mb-3">
                                    <label class="f-label">Họ và tên người đại diện</label>
                                    <input type="text" class="f-input" name="guestFullName" data-rep-src="name" value="<c:out value="${sessionScope.account.fullName}"/>" required>
                                </div>
                                <div class="mb-3">
                                    <label class="f-label">Số điện thoại liên hệ</label>
                                    <input type="tel" class="f-input" name="customerPhone" data-rep-src="phone" value="<c:out value="${sessionScope.account.phone}"/>" placeholder="Nhập số điện thoại (10 số)" required>
                                </div>
                                <div class="mb-3">
                                    <label class="f-label">Số CCCD / Hộ chiếu (Passport)</label>
                                    <input type="text" class="f-input" name="customerIdCard" data-rep-src="id" value="<c:out value="${param.customerIdCard}"/>" placeholder="Nhập số CCCD hoặc Passport" required>
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
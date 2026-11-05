<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://viettour.com/tags" prefix="vt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Chi tiết &amp; Xét duyệt Tour #VT-T00${tour.tourId} | VietTour</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
        <script src="${pageContext.request.contextPath}/static/js/main.js"></script>
    </head>
    <body>
        <div class="adm">
            <aside class="adm-side">
                <div class="brand">VietTour</div>
                <div class="ops">KHÔNG GIAN QUẢN LÝ</div>
                <nav class="mt-4">
                    <a href="${pageContext.request.contextPath}/manager/overview"><i class="bi bi-grid"></i>Tổng quan</a>
                    <a href="${pageContext.request.contextPath}/manager/manage-tours" class="active"><i class="bi bi-map"></i>Quản lý &amp; Duyệt Tour</a>
                    <a href="${pageContext.request.contextPath}/manager/providers"><i class="bi bi-building-check"></i>Quản lý Nhà cung cấp</a>
                    <a href="${pageContext.request.contextPath}/manager/bookings"><i class="bi bi-ticket-perforated"></i>Đơn đặt chỗ</a>
                </nav>
            </aside>
            <div class="adm-main">
                <div class="adm-top">
                    <b style="font-size:14px">Không gian Quản lý (Manager) / Chi tiết xét duyệt Tour</b>
                    <c:if test="${not empty sessionScope.account}">
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
                    </c:if>
                </div>
                <div class="adm-content">
                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">
                        <div>
                            <h1>Chi tiết Hồ sơ Tour #VT-T00${tour.tourId}</h1>
                            <p class="section-sub">Kiểm tra thông tin xác thực của Nhà cung cấp và chi tiết dịch vụ Tour trước khi quyết định phê duyệt.</p>
                        </div>
                        <a href="${pageContext.request.contextPath}/manager/manage-tours" class="btn-cancel d-inline-flex align-items-center gap-2">
                            <i class="bi bi-arrow-left"></i> Quay lại danh sách
                        </a>
                    </div>

                    <div class="row g-4 mt-1">
                        <!-- Cột 1: Thông tin xác thực của Provider -->
                        <div class="col-lg-5">
                            <div class="panel p-4 h-100">
                                <h2 class="mb-3" style="font-size:18px;font-weight:700"><i class="bi bi-building-check me-2 text-success"></i>Thông tin Tổ chức cung cấp (Provider)</h2>
                                <hr>
                                <p class="mb-2"><b>Mã Nhà cung cấp:</b> <span class="mono">VT-P00${provider.accountId}</span></p>
                                <p class="mb-2"><b>Tên Tổ chức / Công ty:</b> <c:out value="${provider.fullName}"/></p>
                                <p class="mb-2"><b>Tên đăng nhập (Username):</b> <c:out value="${provider.username}"/></p>
                                <p class="mb-2"><b>Email liên hệ:</b> <c:out value="${provider.email}"/></p>
                                <p class="mb-2"><b>Số điện thoại:</b> <c:out value="${provider.phone}"/></p>
                                <p class="mb-2"><b>Mã số thuế doanh nghiệp:</b> <b><c:out value="${empty provider.taxCode ? 'Chưa cập nhật' : provider.taxCode}"/></b></p>
                                <p class="mb-2"><b>Số Giấy phép KD Lữ hành:</b> <b><c:out value="${empty provider.businessLicense ? 'Chưa cập nhật' : provider.businessLicense}"/></b></p>
                                <p class="mb-2"><b>Địa chỉ trụ sở chính:</b> <c:out value="${empty provider.companyAddress ? 'Chưa cập nhật' : provider.companyAddress}"/></p>
                                <p class="mb-0"><b>Trạng thái tài khoản NCC:</b>
                                    <c:choose>
                                        <c:when test="${provider.status == 'ACTIVE'}"><span class="status Approved"><span class="sdot"></span>Đã xác thực (Active)</span></c:when>
                                        <c:when test="${provider.status == 'PENDING'}"><span class="status Pending"><span class="sdot"></span>Chờ duyệt hồ sơ</span></c:when>
                                        <c:otherwise><span class="status Rejected"><span class="sdot"></span>Đã khóa</span></c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                        </div>

                        <!-- Cột 2: Thông tin chi tiết dịch vụ Tour & Nút Phê duyệt -->
                        <div class="col-lg-7">
                            <div class="panel p-4 h-100">
                                <div class="d-flex justify-content-between align-items-center">
                                    <h2 class="mb-0" style="font-size:18px;font-weight:700"><i class="bi bi-compass me-2 text-success"></i>Thông tin Dịch vụ Tour</h2>
                                    <c:choose>
                                        <c:when test="${tour.approvalStatus == 'APPROVED'}"><span class="status Approved"><span class="sdot"></span>Đã duyệt</span></c:when>
                                        <c:when test="${tour.approvalStatus == 'PENDING'}"><span class="status Pending"><span class="sdot"></span>Đang chờ phê duyệt</span></c:when>
                                        <c:otherwise><span class="status Rejected"><span class="sdot"></span>Đã từ chối / Đình chỉ</span></c:otherwise>
                                    </c:choose>
                                </div>
                                <hr>
                                <div class="row g-3 mb-3 align-items-center">
                                    <div class="col-md-4">
                                        <div style="height:120px;border-radius:10px;background:url('${pageContext.request.contextPath}/static/images/<c:out value="${tour.imageUrl}"/>') center/cover no-repeat, var(--ph);"></div>
                                    </div>
                                    <div class="col-md-8">
                                        <h4 class="fw-bold mb-2" style="font-size:20px"><c:out value="${tour.tourName}"/></h4>
                                        <p class="mb-1"><b>Địa điểm:</b> <c:out value="${tour.location}"/></p>
                                        <p class="mb-1"><b>Thời lượng:</b> <c:out value="${tour.duration}"/></p>
                                        <p class="mb-1"><b>Số chỗ mở bán:</b> ${tour.availableSeats} chỗ</p>
                                        <p class="mb-0"><b>Ngày tạo:</b> <fmt:formatDate value="${tour.createdAt}" pattern="dd/MM/yyyy HH:mm"/></p>
                                    </div>
                                </div>

                                <div class="p-3 rounded mb-3" style="background:#f6f8f5;border:1px solid var(--vt-border)">
                                    <b class="d-block mb-2">Cấu trúc Giá bán &amp; Chi phí thành phần (trên 1 khách):</b>
                                    <div class="d-flex justify-content-between py-1"><span>Chi phí Vé &amp; Xe di chuyển:</span><b><vt:formatMoney value="${tour.transportCost}" /></b></div>
                                    <div class="d-flex justify-content-between py-1"><span>Chi phí Khách sạn lưu trú:</span><b><vt:formatMoney value="${tour.hotelCost}" /></b></div>
                                    <div class="d-flex justify-content-between py-1"><span>Chi phí Ăn uống &amp; HDV:</span><b><vt:formatMoney value="${tour.mealGuideCost}" /></b></div>
                                    <hr class="my-2">
                                    <div class="d-flex justify-content-between py-1"><span>Tổng giá vốn / khách:</span><b><vt:formatMoney value="${tour.unitCost}" /></b></div>
                                    <div class="d-flex justify-content-between py-1" style="color:var(--vt-green)"><span><b>Giá bán công bố / khách:</b></span><b><vt:formatMoney value="${tour.price}" /></b></div>
                                    <div class="d-flex justify-content-between py-1"><span>Biên lợi nhuận dự kiến / khách:</span><b><vt:formatMoney value="${tour.unitProfit}" /></b></div>
                                </div>

                                <div class="mb-4">
                                    <b class="d-block mb-2">Lịch trình &amp; Mô tả chi tiết:</b>
                                    <div class="p-3 rounded" style="background:#fff;border:1px solid var(--vt-border);white-space:pre-line"><c:out value="${tour.itinerary}"/></div>
                                </div>

                                <form method="post" action="${pageContext.request.contextPath}/manager/approve-tour" class="d-flex justify-content-end gap-2 pt-3" style="border-top:1px solid var(--vt-border)">
                                    <input type="hidden" name="tourId" value="${tour.tourId}">
                                    <input type="hidden" name="from" value="manage-tours">
                                    <button type="submit" name="action" value="reject" class="btn-reject">
                                        <i class="bi bi-x-circle me-1"></i> Từ chối / Đình chỉ Tour
                                    </button>
                                    <button type="submit" name="action" value="approve" class="btn-approve">
                                        <i class="bi bi-check-lg me-1"></i> Phê duyệt Mở bán Tour
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
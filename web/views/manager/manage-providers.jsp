<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://viettour.com/tags" prefix="vt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý Nhà cung cấp | VietTour</title>
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
                    <a href="${pageContext.request.contextPath}/manager/manage-tours"><i class="bi bi-map"></i>Quản lý &amp; Duyệt Tour</a>
                    <a href="${pageContext.request.contextPath}/manager/providers" class="active"><i class="bi bi-building-check"></i>Quản lý Nhà cung cấp</a>
                    <a href="${pageContext.request.contextPath}/manager/tour-stats"><i class="bi bi-bar-chart-line"></i>Thống kê Tour</a>
                    <a href="${pageContext.request.contextPath}/manager/bookings"><i class="bi bi-ticket-perforated"></i>Đơn đặt chỗ</a>
                </nav>
            </aside>
            <div class="adm-main">
                <div class="adm-top">
                    <b style="font-size:14px">Không gian Quản lý (Manager)</b>
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
                    <h1>Xác thực &amp; Quản lý Nhà cung cấp (Provider)</h1>
                    <p class="section-sub">Kiểm tra hồ sơ pháp nhân (Mã số thuế, Giấy phép lữ hành) và phê duyệt quyền hoạt động cho các tổ chức cung cấp dịch vụ tour.</p>
                    <div class="panel mt-4">
                        <div class="panel-head"><h2>Danh sách Tổ chức cung cấp dịch vụ Tour</h2></div>
                        <div class="table-scroll">
                            <table class="adm-table">
                                <thead>
                                    <tr>
                                        <th>Mã NCC</th>
                                        <th>Tên Tổ chức / Công ty</th>
                                        <th>Email &amp; SĐT</th>
                                        <th>Hồ sơ xác thực (MST &amp; GPKD)</th>
                                        <th class="c">Tour (duyệt/tổng)</th>
                                        <th class="c">Vé đã bán</th>
                                        <th class="c">Đơn chờ duyệt</th>
                                        <th class="r">Doanh thu</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="p" items="${providerList}">
                                        <c:set var="st" value="${providerStats[p.accountId]}"/>
                                        <tr>
                                            <td class="mono text-muted-vt">VT-P00${p.accountId}</td>
                                            <td><b><c:out value="${p.fullName}"/></b><br><small><c:out value="${empty p.companyAddress ? 'Chưa cập nhật địa chỉ' : p.companyAddress}"/></small></td>
                                            <td><c:out value="${p.email}"/><br><small><c:out value="${p.phone}"/></small></td>
                                            <td>
                                                <b>MST:</b> <c:out value="${empty p.taxCode ? 'N/A' : p.taxCode}"/><br>
                                                <small><b>GPKD:</b> <c:out value="${empty p.businessLicense ? 'N/A' : p.businessLicense}"/></small>
                                            </td>
                                            <td class="c">${empty st ? 0 : st.approvedTours} / ${empty st ? 0 : st.totalTours}</td>
                                            <td class="c"><b>${empty st ? 0 : st.guestsSold}</b></td>
                                            <td class="c">${empty st ? 0 : st.waitingOrders}</td>
                                            <td class="r fw-semibold"><vt:formatMoney value="${empty st ? 0 : st.revenue}" /></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${p.status == 'ACTIVE' || p.status == 'Active'}"><span class="status Approved"><span class="sdot"></span>Đã duyệt (Active)</span></c:when>
                                                    <c:when test="${p.status == 'PENDING' || p.status == 'Pending'}"><span class="status Pending"><span class="sdot"></span>Chờ duyệt hồ sơ</span></c:when>
                                                    <c:otherwise><span class="status Rejected"><span class="sdot"></span>Đã khóa (Locked)</span></c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="d-flex align-items-center gap-2">
                                                    <button type="button" class="btn-mini" data-bs-toggle="modal" data-bs-target="#providerDetailModal-${p.accountId}">
                                                        <i class="bi bi-eye me-1"></i>Chi tiết
                                                    </button>
                                                    <form method="post" action="${pageContext.request.contextPath}/manager/providers" class="act">
                                                        <input type="hidden" name="accountId" value="${p.accountId}">
                                                        <c:if test="${p.status != 'ACTIVE' && p.status != 'Active'}">
                                                            <button type="submit" name="action" value="approve" class="btn-approve" style="width:auto; padding:6px 14px; background: green">Duyệt</button>
                                                        </c:if>
                                                        <c:if test="${p.status != 'LOCKED' && p.status != 'Locked'}">
                                                            <button type="submit" name="action" value="lock" class="btn-reject" style="width:auto; padding:6px 14px; background: red">Khóa</button>
                                                        </c:if>
                                                    </form>
                                                </div>

                                                <!-- Modal xem Chi tiết & Duyệt Nhà cung cấp -->
                                                <div class="modal fade" id="providerDetailModal-${p.accountId}" tabindex="-1" aria-hidden="true">
                                                    <div class="modal-dialog modal-dialog-centered">
                                                        <div class="modal-content vt-modal">
                                                            <div class="modal-header">
                                                                <h5 class="modal-title">Hồ sơ Pháp nhân VT-P00${p.accountId}</h5>
                                                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                            </div>
                                                            <div class="modal-body">
                                                                <p class="mb-2"><b>Tên Tổ chức / Công ty:</b> <c:out value="${p.fullName}"/></p>
                                                                <p class="mb-2"><b>Username:</b> <c:out value="${p.username}"/></p>
                                                                <p class="mb-2"><b>Email:</b> <c:out value="${p.email}"/></p>
                                                                <p class="mb-2"><b>Số điện thoại:</b> <c:out value="${p.phone}"/></p>
                                                                <hr>
                                                                <p class="mb-2"><b>Mã số thuế doanh nghiệp:</b> <c:out value="${empty p.taxCode ? 'Chưa cung cấp' : p.taxCode}"/></p>
                                                                <p class="mb-2"><b>Số Giấy phép KD Lữ hành:</b> <c:out value="${empty p.businessLicense ? 'Chưa cung cấp' : p.businessLicense}"/></p>
                                                                <p class="mb-2"><b>Địa chỉ trụ sở chính:</b> <c:out value="${empty p.companyAddress ? 'Chưa cung cấp' : p.companyAddress}"/></p>
                                                                <p class="mb-0"><b>Ngày đăng ký:</b> <fmt:formatDate value="${p.createdAt}" pattern="dd/MM/yyyy HH:mm"/></p>
                                                            </div>
                                                            <div class="modal-footer">
                                                                <form method="post" action="${pageContext.request.contextPath}/manager/providers" class="d-flex gap-2 m-0">
                                                                    <input type="hidden" name="accountId" value="${p.accountId}">
                                                                    <c:if test="${p.status != 'ACTIVE' && p.status != 'Active'}">
                                                                        <button type="submit" name="action" value="approve" class="btn-approve" style="width:auto;padding:0 16px;">Phê duyệt Hoạt động</button>
                                                                    </c:if>
                                                                    <c:if test="${p.status != 'LOCKED' && p.status != 'Locked'}">
                                                                        <button type="submit" name="action" value="lock" class="btn-reject" style="width:auto;padding:0 16px;">Khóa Tài khoản</button>
                                                                    </c:if>
                                                                    <button type="button" class="btn-cancel" data-bs-dismiss="modal">Đóng</button>
                                                                </form>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
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
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
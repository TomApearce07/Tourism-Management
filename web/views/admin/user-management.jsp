<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý Tài khoản | VietTour</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
        <script src="${pageContext.request.contextPath}/static/js/main.js"></script>
    </head>
    <body>
        <div class="adm">
            <aside class="adm-side">
                <div class="brand">VietTour</div>
                <div class="ops">QUẢN TRỊ HỆ THỐNG</div>
                <div class="adm-ws">
                    <div><small>QUYỀN TRUY CẬP</small><b><i class="bi bi-shield-check me-2"></i>Quản trị viên (Admin)</b></div>
                </div>
                <nav class="mt-4">
                    <a href="${pageContext.request.contextPath}/admin/users" class="active"><i class="bi bi-people"></i>Quản lý người dùng</a>
                    <a href="${pageContext.request.contextPath}/home"><i class="bi bi-globe"></i>Xem Trang chủ</a>
                </nav>
            </aside>
            <div class="adm-main">
                <div class="adm-top">
                    <b style="font-size:14px">Không gian Quản trị viên hệ thống</b>
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
                    <div class="d-flex justify-content-between align-items-start flex-wrap gap-2">
                        <div>
                            <h1>Quản lý tất cả Tài khoản</h1>
                            <p class="section-sub">Tạo tài khoản cho Manager, xem chi tiết hồ sơ Manager và khóa/mở khóa người dùng.</p>
                        </div>
                        <button type="button" class="btn-add" data-bs-toggle="modal" data-bs-target="#createManagerModal">
                            <i class="bi bi-person-plus-fill"></i> Tạo tài khoản Manager
                        </button>
                    </div>

                    <c:if test="${param.created == 'true'}">
                        <div class="alert alert-success mt-3 mb-0">Đã tạo tài khoản Manager mới thành công!</div>
                    </c:if>
                    <c:if test="${not empty errorMsg}">
                        <div class="alert alert-danger mt-3 mb-0"><c:out value="${errorMsg}"/></div>
                    </c:if>

                    <div class="panel mt-4">
                        <div class="panel-head"><h2>Danh sách tài khoản người dùng</h2></div>
                        <div class="table-scroll">
                            <table class="adm-table">
                                <thead>
                                    <tr>
                                        <th>Mã TK</th>
                                        <th>Username</th>
                                        <th>Họ tên / Tổ chức</th>
                                        <th>Email</th>
                                        <th>Vai trò</th>
                                        <th>Trạng thái</th>
                                        <th>Chi tiết</th>
                                        <th>Khóa / Mở</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="u" items="${userList}">
                                        <tr style="${u.roleId == 3 ? 'background:#f0f7f4;' : ''}">
                                            <td class="mono text-muted-vt">VT-U00${u.accountId}</td>
                                            <td class="mono"><b><c:out value="${u.username}"/></b></td>
                                            <td><b style="font-weight:700"><c:out value="${u.fullName}"/></b></td>
                                            <td><c:out value="${u.email}"/></td>
                                            <td><b>${u.roleName}</b></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${u.status == 'ACTIVE'}"><span class="status Approved"><i class="bi bi-check-lg"></i>Đang hoạt động</span></c:when>
                                                    <c:when test="${u.status == 'PENDING'}"><span class="status Pending"><i class="bi bi-clock"></i>Chờ duyệt</span></c:when>
                                                    <c:otherwise><span class="status Rejected"><i class="bi bi-lock"></i>Đã khóa</span></c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <button type="button" class="btn-mini" data-bs-toggle="modal" data-bs-target="#userDetailModal-${u.accountId}">
                                                    <i class="bi bi-eye me-1"></i>Xem
                                                </button>

                                                <!-- Modal xem thông tin chi tiết Manager / User -->
                                                <div class="modal fade" id="userDetailModal-${u.accountId}" tabindex="-1" aria-hidden="true">
                                                    <div class="modal-dialog modal-dialog-centered">
                                                        <div class="modal-content vt-modal">
                                                            <div class="modal-header">
                                                                <h5 class="modal-title">Chi tiết Tài khoản VT-U00${u.accountId}</h5>
                                                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                            </div>
                                                            <div class="modal-body">
                                                                <p class="mb-2"><b>Vai trò:</b> <span class="status Approved">${u.roleName}</span></p>
                                                                <p class="mb-2"><b>Tên đăng nhập (Username):</b> <c:out value="${u.username}"/></p>
                                                                <p class="mb-2"><b>Họ và tên / Tên đơn vị:</b> <c:out value="${u.fullName}"/></p>
                                                                <p class="mb-2"><b>Email:</b> <c:out value="${u.email}"/></p>
                                                                <p class="mb-2"><b>Số điện thoại:</b> <c:out value="${empty u.phone ? 'Chưa cập nhật' : u.phone}"/></p>
                                                                <p class="mb-2"><b>Trạng thái:</b> ${u.status}</p>
                                                                <p class="mb-2"><b>Ngày tạo:</b> <fmt:formatDate value="${u.createdAt}" pattern="dd/MM/yyyy HH:mm"/></p>
                                                                <c:if test="${u.roleId == 2}">
                                                                    <hr>
                                                                    <p class="mb-1"><b>Mã số thuế:</b> <c:out value="${empty u.taxCode ? 'N/A' : u.taxCode}"/></p>
                                                                    <p class="mb-1"><b>Giấy phép KD Lữ hành:</b> <c:out value="${empty u.businessLicense ? 'N/A' : u.businessLicense}"/></p>
                                                                    <p class="mb-0"><b>Địa chỉ trụ sở:</b> <c:out value="${empty u.companyAddress ? 'N/A' : u.companyAddress}"/></p>
                                                                </c:if>
                                                            </div>
                                                            <div class="modal-footer">
                                                                <button type="button" class="btn-cancel" data-bs-dismiss="modal">Đóng</button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${u.accountId == sessionScope.account.accountId}">
                                                        <button type="button" class="toggle ${u.status == 'ACTIVE' ? 'on' : 'off'}" disabled style="opacity: 0.6; cursor: not-allowed; border: none;">
                                                            <span class="lbl">${u.status == 'ACTIVE' ? 'ON' : 'OFF'}</span>
                                                            <span class="knob"></span>
                                                        </button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <form method="post" action="${pageContext.request.contextPath}/admin/toggle-account" class="m-0 preserve-scroll">
                                                            <input type="hidden" name="accountId" value="${u.accountId}">
                                                            <input type="hidden" name="currentStatus" value="${u.status}">
                                                            <button type="submit" class="toggle ${u.status == 'ACTIVE' ? 'on' : 'off'}">
                                                                <span class="lbl">${u.status == 'ACTIVE' ? 'ON' : 'OFF'}</span>
                                                                <span class="knob"></span>
                                                            </button>
                                                        </form>
                                                    </c:otherwise>
                                                </c:choose>
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

        <!-- Modal Tạo tài khoản cho Manager -->
        <div class="modal fade" id="createManagerModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <form class="modal-content vt-modal" action="${pageContext.request.contextPath}/admin/users" method="post">
                    <input type="hidden" name="action" value="createManager">
                    <div class="modal-header">
                        <h5 class="modal-title">Tạo tài khoản Quản lý (Manager)</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="f-label">Họ và tên Manager *</label>
                            <input type="text" class="f-input" name="fullName" placeholder="VD: Nguyễn Quản Lý" required>
                        </div>
                        <div class="mb-3">
                            <label class="f-label">Tên đăng nhập (Username) *</label>
                            <input type="text" class="f-input" name="username" placeholder="VD: manager_nguyen" required>
                        </div>
                        <div class="mb-3">
                            <label class="f-label">Địa chỉ Email *</label>
                            <input type="email" class="f-input" name="email" placeholder="manager@viettour.com" required>
                        </div>
                        <div class="mb-3">
                            <label class="f-label">Số điện thoại *</label>
                            <input type="tel" class="f-input" name="phone" placeholder="0901234567" required>
                        </div>
                        <div class="mb-2">
                            <label class="f-label">Mật khẩu khởi tạo *</label>
                            <input type="password" class="f-input" name="password" minlength="6" placeholder="Tối thiểu 6 ký tự" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn-cancel" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn-add">Tạo tài khoản Manager</button>
                    </div>
                </form>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
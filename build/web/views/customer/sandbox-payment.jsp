<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Cổng Thanh Toán Giả Lập (Sandbox)</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .sandbox-box { max-width: 460px; margin: 50px auto; border: 2px dashed #dc3545; border-radius: 12px; padding: 30px; text-align: center; background: #fffafb; }
    </style>
</head>
<body style="background: #f8f9fa;">
    <div class="sandbox-box shadow-sm">
        <span class="badge bg-danger mb-3" style="font-size: 14px;">MÔI TRƯỜNG TEST (SANDBOX)</span>
        <h3 class="mb-4">Quét mã VietQR để thanh toán</h3>
        
        <div class="text-start mb-3" style="background: #fff; padding: 15px; border-radius: 8px; border: 1px solid #dee2e6;">
            <div>Mã đơn hàng: <strong>VT-BK<c:out value="${param.orderId}"/></strong></div>
            <div>Số tiền thanh toán: <strong class="text-danger"><c:out value="${param.amount}"/> VND</strong></div>
            <div>Merchant ID: <code>VIETTOUR_SANDBOX_MERCHANT</code></div>
        </div>

        <img src="https://api.qrserver.com/v1/create-qr-code/?size=220x220&data=VIETTOUR_PAY_ORDER_<c:out value="${param.orderId}"/>_AMOUNT_<c:out value="${param.amount}"/>" 
             alt="QR Code" class="mb-4 p-2 border bg-white rounded">

        <hr>
        <p class="text-muted small">Mô phỏng phản hồi tự động (Webhook / IPN) từ Cổng thanh toán về hệ thống VietTour:</p>
        
        <div class="d-flex gap-2 mt-3">
            <form action="${pageContext.request.contextPath}/api/ipn-handler" method="POST" class="w-50 m-0">
                <input type="hidden" name="secretKey" value="VT_SANDBOX_KEY_2026">
                <input type="hidden" name="orderId" value="<c:out value="${param.orderId}"/>">
                <input type="hidden" name="tourId" value="<c:out value="${param.tourId}"/>">
                <input type="hidden" name="guests" value="<c:out value="${param.guests}"/>">
                <input type="hidden" name="status" value="FAILED">
                <button type="submit" class="btn btn-outline-secondary w-100 fw-bold py-2">
                    Hủy giao dịch
                </button>
            </form>

            <form action="${pageContext.request.contextPath}/api/ipn-handler" method="POST" class="w-50 m-0">
                <input type="hidden" name="secretKey" value="VT_SANDBOX_KEY_2026">
                <input type="hidden" name="orderId" value="<c:out value="${param.orderId}"/>">
                <input type="hidden" name="tourId" value="<c:out value="${param.tourId}"/>">
                <input type="hidden" name="guests" value="<c:out value="${param.guests}"/>">
                <input type="hidden" name="status" value="SUCCESS">
                <button type="submit" class="btn btn-success w-100 fw-bold py-2">
                    Tôi đã thanh toán
                </button>
            </form>
        </div>
    </div>
</body>
</html>
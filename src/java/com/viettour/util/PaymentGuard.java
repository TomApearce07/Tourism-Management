package com.viettour.util;

import com.viettour.dao.BookingDAO;
import com.viettour.model.Booking;

/**
 * Quy tắc bảo vệ tiền của khách: khi khách đã thanh toán nhưng Nhà cung cấp CHƯA xác nhận
 * (trạng thái WAITING_APPROVAL) thì Manager / Admin không được đình chỉ tour hoặc khóa nhà cung cấp đó,
 * vì Provider sẽ không còn cách nào để duyệt hay hoàn tiền cho khách.
 */
public final class PaymentGuard {

    private static final String WAITING = "WAITING_APPROVAL";

    private PaymentGuard() {
    }

    /** Số đơn của một tour đã thanh toán nhưng Provider chưa duyệt. */
    public static int countPendingForTour(BookingDAO bookingDAO, int tourId) {
        int count = 0;
        for (Object o : bookingDAO.getAllBookings()) {
            Booking b = (Booking) o;
            if (b.getTourId() == tourId && WAITING.equalsIgnoreCase(b.getPaymentStatus())) {
                count++;
            }
        }
        return count;
    }

    /** Số đơn của tất cả tour thuộc một nhà cung cấp đã thanh toán nhưng chưa được duyệt. */
    public static int countPendingForProvider(BookingDAO bookingDAO, int providerId) {
        int count = 0;
        for (Object o : bookingDAO.getBookingsByProvider(providerId)) {
            Booking b = (Booking) o;
            if (WAITING.equalsIgnoreCase(b.getPaymentStatus())) {
                count++;
            }
        }
        return count;
    }
}

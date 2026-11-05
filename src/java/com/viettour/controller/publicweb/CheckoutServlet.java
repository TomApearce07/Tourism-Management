package com.viettour.controller.publicweb;

import com.viettour.dao.AccountDAO;
import com.viettour.dao.BookingDAO;
import com.viettour.dao.TourDAO;
import com.viettour.dao.TourDepartureDAO;
import com.viettour.model.Account;
import com.viettour.model.Booking;
import com.viettour.model.Passenger;
import com.viettour.model.Tour;
import com.viettour.model.TourDeparture;
import com.viettour.util.PassengerFormUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Đặt tour (URL /customer/checkout khai báo trong web.xml).
 *
 * Quy tắc nghiệp vụ:
 *  - Chỉ Customer (RoleID = 1) được đặt tour.
 *  - Chỉ đặt được tour đã APPROVED, bắt buộc chọn một ngày khởi hành còn mở; số khách từ 1 đến số chỗ còn
 *    trống CỦA NGÀY ĐÓ.
 *  - Phải nhập đủ thông tin từng hành khách (số dòng = số khách), kiểm tra ở server.
 *  - Tổng tiền luôn tính ở server (không tin số tiền từ trình duyệt).
 *  - Đơn mới có trạng thái UNPAID và GIỮ CHỖ ngay (transaction trong BookingDAO). Đơn UNPAID quá 15 phút
 *    tự không còn tính chỗ (view vw_DepartureSeats).
 */
public class CheckoutServlet extends HttpServlet {

    private static final String CHECKOUT_JSP = "/views/customer/checkout.jsp";
    private static final int ROLE_CUSTOMER = 1;

    // Cột SpecialRequests trong DB là NVARCHAR(500)
    private static final int MAX_SPECIAL_REQUESTS = 500;

    private static final String PHONE_REGEX = "^0[0-9]{9,10}$";
    private static final String ID_CARD_REGEX = "^[A-Za-z0-9]{8,20}$";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isCustomer(request)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        int tourId = parseInt(request.getParameter("tourId"), -1);
        int guests = parseInt(request.getParameter("guests"), 1);
        int departureId = parseInt(request.getParameter("departureId"), -1);

        TourDAO tourDAO = new TourDAO();
        TourDepartureDAO depDAO = new TourDepartureDAO();
        try {
            Tour tour = tourDAO.getTourById(tourId);
            if (!isBookable(tour)) {
                response.sendRedirect(request.getContextPath() + "/tours");
                return;
            }
            TourDeparture dep = depDAO.getDeparture(departureId);
            if (!isDepartureBookable(dep, tourId)) {    // chưa chọn ngày / ngày không hợp lệ: về trang chi tiết để chọn lại
                response.sendRedirect(request.getContextPath() + "/tour-detail?id=" + tourId);
                return;
            }
            // Ép số khách về khoảng hợp lệ thay vì báo lỗi (giống nút +/- ở trang chi tiết tour)
            if (guests < 1) {
                guests = 1;
            }
            if (guests > dep.getSeatsLeft()) {
                guests = dep.getSeatsLeft();
            }
            forwardCheckout(request, response, tour, dep, guests, null);
        } finally {
            tourDAO.close();
            depDAO.close();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isCustomer(request)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        Account account = (Account) request.getSession().getAttribute("account");

        int tourId = parseInt(request.getParameter("tourId"), -1);
        int guests = parseInt(request.getParameter("guestsCount"), 0);
        int departureId = parseInt(request.getParameter("departureId"), -1);

        String customerPhone = clean(request.getParameter("customerPhone"));
        String customerIdCard = clean(request.getParameter("customerIdCard"));
        String bookingChannel = clean(request.getParameter("bookingChannel"));
        String specialRequests = clean(request.getParameter("specialRequests"));
        String paymentMethod = clean(request.getParameter("paymentMethod"));

        TourDAO tourDAO = new TourDAO();
        TourDepartureDAO depDAO = new TourDepartureDAO();
        BookingDAO bookingDAO = new BookingDAO();
        try {
            Tour tour = tourDAO.getTourById(tourId);
            if (!isBookable(tour)) {
                response.sendRedirect(request.getContextPath() + "/tours");
                return;
            }
            TourDeparture dep = depDAO.getDeparture(departureId);
            if (!isDepartureBookable(dep, tourId)) {
                response.sendRedirect(request.getContextPath() + "/tour-detail?id=" + tourId);
                return;
            }

            // ===== Kiểm tra dữ liệu =====
            String error = null;
            if (guests < 1) {
                error = "Số lượng khách phải từ 1 trở lên!";
            } else if (guests > dep.getSeatsLeft()) {
                error = "Ngày khởi hành này chỉ còn " + dep.getSeatsLeft() + " chỗ trống, bạn không thể đặt " + guests + " khách!";
            }
            // Danh sách hành khách: đúng số dòng = số khách, kiểm tra từng người (xem PassengerFormUtil)
            List<Passenger> passengers = new ArrayList<>();
            if (error == null) {
                error = PassengerFormUtil.parse(request, guests, dep.getDepartureDate().toLocalDate(), passengers);
            }
            if (error == null && !customerPhone.matches(PHONE_REGEX)) {
                error = "Số điện thoại phải bắt đầu bằng 0 và gồm 10 hoặc 11 chữ số!";
            }
            if (error == null && !customerIdCard.matches(ID_CARD_REGEX)) {
                error = "Số CCCD / Passport gồm 8-20 ký tự chữ hoặc số!";
            }
            if (error == null && !"VNPAY_SANDBOX".equals(paymentMethod) && !"MOMO_SANDBOX".equals(paymentMethod)) {
                error = "Vui lòng chọn phương thức thanh toán hợp lệ!";
            }
            if (error != null) {
                int shown = Math.max(1, Math.min(guests, Math.max(1, dep.getSeatsLeft())));
                forwardCheckout(request, response, tour, dep, shown, error);
                return;
            }

            // Chỉ nhận các kênh bán hàng đã định nghĩa
            if (!"OTA_KLOOK_AGODA".equals(bookingChannel) && !"ZALO_HOTLINE".equals(bookingChannel)
                    && !"AGENCY".equals(bookingChannel)) {
                bookingChannel = "WEBSITE";
            }

            // ===== Tính tiền ở server =====
            BigDecimal totalAmount = calculateDynamicTotal(tour.getPrice(), guests, dep.getDepartureDate().toLocalDate());
            BigDecimal totalCost = tour.getUnitCost().multiply(new BigDecimal(guests));
            BigDecimal profit = totalAmount.subtract(totalCost);

            // ===== Ghi chú đơn hàng =====
            // Thông tin khách / CCCD / kênh đã có cột riêng và bảng BookingPassengers nên KHÔNG ghép vào đây nữa.
            // Giữ lại thẻ giá vốn / lợi nhuận vì trang Provider hiện đọc nó (sẽ thay bằng cột riêng ở C2.3).
            StringBuilder note = new StringBuilder();
            note.append("[Giá vốn: ").append(totalCost.toPlainString())
                    .append("đ, Lợi nhuận: ").append(profit.toPlainString()).append("đ]");

            // Phần ghi chú của khách bị cắt bớt để tổng độ dài không vượt cột trong DB
            String label = " | Ghi chú: ";
            int room = MAX_SPECIAL_REQUESTS - note.length() - label.length();
            if (!specialRequests.isEmpty() && room > 0) {
                String extra = noPipe(specialRequests);
                if (extra.length() > room) {
                    extra = extra.substring(0, room);
                }
                note.append(label).append(extra);
            }
            String noteText = note.toString();
            if (noteText.length() > MAX_SPECIAL_REQUESTS) {
                noteText = noteText.substring(0, MAX_SPECIAL_REQUESTS);
            }

            // ===== Tạo đơn =====
            Booking booking = new Booking();
            booking.setCustomerId(account.getAccountId());
            booking.setTourId(tourId);
            booking.setDepartureId(departureId);
            booking.setGuestsCount(guests);
            booking.setSpecialRequests(noteText);
            booking.setTotalAmount(totalAmount);
            booking.setPaymentMethod(paymentMethod);
            booking.setPaymentStatus("UNPAID");
            booking.setCustomerPhone(customerPhone);
            booking.setCustomerIdCard(customerIdCard);
            booking.setBookingChannel(bookingChannel);

            // Một transaction: khóa ngày khởi hành -> kiểm tra chỗ -> ghi đơn -> ghi hành khách
            int bookingId = bookingDAO.createBookingWithPassengers(booking, passengers);
            if (bookingId == -2) {
                // người khác vừa đặt trước: số chỗ trên màn hình đã cũ
                int left = bookingDAO.getLastSeatsLeft();
                TourDeparture fresh = depDAO.getDeparture(departureId);
                if (fresh != null && left > 0) {
                    forwardCheckout(request, response, tour, fresh, Math.min(guests, left),
                            "Ngày này vừa có người đặt, hiện chỉ còn " + left + " chỗ. Vui lòng điều chỉnh số khách.");
                } else {
                    request.getSession().setAttribute("paymentSuccessMsg", "Ngày khởi hành bạn chọn đã hết chỗ. Vui lòng chọn ngày khác.");
                    response.sendRedirect(request.getContextPath() + "/tour-detail?id=" + tourId);
                }
                return;
            }
            if (bookingId <= 0) {
                // ghi DB thất bại: không chuyển sang bước thanh toán
                forwardCheckout(request, response, tour, dep, guests,
                        "Không thể tạo đơn hàng lúc này. Vui lòng thử lại sau!");
                return;
            }

            // Chuyển thẳng tới trang giao diện thanh toán sandbox (chỉ là màn hình mô phỏng).
            // Trạng thái đơn chỉ đổi khi người dùng bấm nút ở trang đó (PaymentReturnServlet kiểm tra chủ đơn và trạng thái).
            response.sendRedirect(request.getContextPath()
                    + "/views/customer/sandbox-payment.jsp?orderId=" + bookingId
                    + "&amount=" + totalAmount.toPlainString()
                    + "&tourId=" + tourId
                    + "&guests=" + guests);
        } finally {
            tourDAO.close();
            depDAO.close();
            bookingDAO.close();
        }
    }

    /**
     * Giá = đơn giá x số khách x hệ số mùa x hệ số đoàn, làm tròn về số nguyên.
     *  - Hệ số mùa theo THÁNG KHỞI HÀNH: tháng 6-8 và 12 là 1.10; tháng 9-10 là 0.95; còn lại là 1.
     *  - Hệ số đoàn: từ 10 khách là 0.90; từ 5 khách là 0.95; còn lại là 1.
     * (Giữ nguyên công thức của bản cũ; chỉ đổi mốc tính mùa từ tháng đặt sang tháng khởi hành.)
     */
    private BigDecimal calculateDynamicTotal(BigDecimal price, int guests, LocalDate departureDate) {
        BigDecimal base = price.multiply(new BigDecimal(guests));

        int month = departureDate.getMonthValue();
        BigDecimal seasonFactor = BigDecimal.ONE;
        if ((month >= 6 && month <= 8) || month == 12) {
            seasonFactor = new BigDecimal("1.10");
        } else if (month == 9 || month == 10) {
            seasonFactor = new BigDecimal("0.95");
        }

        BigDecimal groupFactor = BigDecimal.ONE;
        if (guests >= 10) {
            groupFactor = new BigDecimal("0.90");
        } else if (guests >= 5) {
            groupFactor = new BigDecimal("0.95");
        }

        return base.multiply(seasonFactor).multiply(groupFactor).setScale(0, RoundingMode.HALF_UP);
    }

    /** Đặt các thuộc tính mà checkout.jsp cần rồi forward (kèm thông báo lỗi nếu có). */
    private void forwardCheckout(HttpServletRequest request, HttpServletResponse response,
            Tour tour, TourDeparture dep, int guests, String error) throws ServletException, IOException {
        BigDecimal totalAmount = calculateDynamicTotal(tour.getPrice(), guests, dep.getDepartureDate().toLocalDate());
        BigDecimal totalCost = tour.getUnitCost().multiply(new BigDecimal(guests));

        request.setAttribute("tour", tour);
        request.setAttribute("departure", dep);
        AccountDAO accountDAO = new AccountDAO();
        try {
            request.setAttribute("provider", accountDAO.getAccountById(tour.getProviderId()));   // liên hệ Provider ở bước đặt
        } finally {
            accountDAO.close();
        }
        request.setAttribute("guests", Integer.valueOf(guests));
        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("totalCost", totalCost);
        request.setAttribute("estimatedProfit", totalAmount.subtract(totalCost));
        if (error != null) {
            request.setAttribute("error", error);
        }
        request.getRequestDispatcher(CHECKOUT_JSP).forward(request, response);
    }

    /** Tour phải tồn tại và đã được duyệt (số chỗ được kiểm tra theo từng ngày khởi hành, xem isDepartureBookable). */
    private boolean isBookable(Tour tour) {
        return tour != null && "APPROVED".equalsIgnoreCase(tour.getApprovalStatus());
    }

    /** Ngày khởi hành phải thuộc đúng tour, đang OPEN, từ hôm nay trở đi và còn ít nhất 1 chỗ. */
    private boolean isDepartureBookable(TourDeparture dep, int tourId) {
        return dep != null
                && dep.getTourId() == tourId
                && "OPEN".equals(dep.getStatus())
                && !dep.getDepartureDate().toLocalDate().isBefore(LocalDate.now())
                && dep.getSeatsLeft() >= 1;
    }

    private boolean isCustomer(HttpServletRequest request) {
        Account account = (Account) request.getSession().getAttribute("account");
        return account != null && account.getRoleId() == ROLE_CUSTOMER;
    }

    private int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value == null ? "" : value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    /** Dấu "|" là ký tự ngăn cách trong ghi chú nên thay bằng "/" để khách không giả được thẻ khác. */
    private String noPipe(String value) {
        return value.replace('|', '/');
    }
}

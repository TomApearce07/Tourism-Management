package com.viettour.util;

import com.viettour.model.Passenger;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Đọc và kiểm tra danh sách hành khách ở bước đặt tour (mảng passengerName[], passengerGender[],
 * passengerDob[], passengerIdCard[], passengerPhone[]). Mọi kiểm tra chạy ở server, không tin JavaScript.
 */
public final class PassengerFormUtil {

    /** Dưới số tuổi này (tính tại ngày khởi hành) là trẻ em. */
    public static final int CHILD_MAX_AGE_EXCLUSIVE = 12;

    private static final String PHONE_REGEX = "^0[0-9]{9,10}$";
    private static final String ID_CARD_REGEX = "^[A-Za-z0-9]{8,20}$";

    private PassengerFormUtil() {
    }

    /**
     * @param guests        số khách của đơn; số dòng hành khách phải bằng đúng số này
     * @param departureDate ngày khởi hành, dùng để tính tuổi (trẻ em / người lớn)
     * @param out           nhận danh sách hợp lệ; hành khách đầu tiên là người đại diện
     * @return thông báo lỗi hoặc null nếu hợp lệ
     */
    public static String parse(HttpServletRequest request, int guests, LocalDate departureDate, List<Passenger> out) {
        String[] names = request.getParameterValues("passengerName");
        String[] genders = request.getParameterValues("passengerGender");
        String[] dobs = request.getParameterValues("passengerDob");
        String[] ids = request.getParameterValues("passengerIdCard");
        String[] phones = request.getParameterValues("passengerPhone");

        if (names == null || names.length != guests || genders == null || genders.length != guests
                || dobs == null || dobs.length != guests || ids == null || ids.length != guests
                || phones == null || phones.length != guests) {
            return "Vui lòng nhập đủ thông tin của " + guests + " hành khách!";
        }

        Set<String> seenIds = new HashSet<>();
        int adults = 0;
        LocalDate today = LocalDate.now();

        for (int i = 0; i < guests; i++) {
            String label = "Hành khách " + (i + 1) + ": ";
            String name = InputUtil.clean(names[i]);
            String dobText = InputUtil.clean(dobs[i]);
            String id = InputUtil.clean(ids[i]);
            String phone = InputUtil.clean(phones[i]);
            String gender = InputUtil.clean(genders[i]);

            if (name.isEmpty() || name.length() > 100) {
                return label + "họ tên không được để trống và tối đa 100 ký tự!";
            }
            LocalDate dob;
            try {
                dob = LocalDate.parse(dobText);
            } catch (DateTimeParseException e) {
                return label + "vui lòng nhập ngày sinh hợp lệ!";
            }
            if (!dob.isBefore(today) || dob.isBefore(today.minusYears(120))) {
                return label + "ngày sinh không hợp lệ!";
            }
            if (!"MALE".equals(gender) && !"FEMALE".equals(gender) && !"OTHER".equals(gender)) {
                gender = null;      // giới tính không bắt buộc
            }

            boolean child = Period.between(dob, departureDate).getYears() < CHILD_MAX_AGE_EXCLUSIVE;
            if (!child) {
                adults++;
                if (!id.matches(ID_CARD_REGEX)) {
                    return label + "CCCD / Passport gồm 8-20 ký tự chữ hoặc số!";
                }
            } else if (!id.isEmpty() && !id.matches(ID_CARD_REGEX)) {
                return label + "CCCD / Passport (nếu nhập) gồm 8-20 ký tự chữ hoặc số!";
            }
            if (!id.isEmpty() && !seenIds.add(id.toUpperCase())) {
                return label + "CCCD / Passport bị trùng với hành khách khác trong đơn!";
            }
            if (!phone.isEmpty() && !phone.matches(PHONE_REGEX)) {
                return label + "số điện thoại phải bắt đầu bằng 0 và gồm 10 hoặc 11 chữ số!";
            }

            Passenger p = new Passenger();
            p.setFullName(name);
            p.setGender(gender);
            p.setDateOfBirth(Date.valueOf(dob));
            p.setIdCard(id.isEmpty() ? null : id);
            p.setPhone(phone.isEmpty() ? null : phone);
            p.setPassengerType(child ? "CHILD" : "ADULT");
            p.setRepresentative(i == 0);
            out.add(p);
        }
        if (adults < 1) {
            return "Đơn đặt tour phải có ít nhất 1 người lớn (từ " + CHILD_MAX_AGE_EXCLUSIVE + " tuổi trở lên)!";
        }
        return null;
    }
}

package com.viettour.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Các hàm đọc và kiểm tra dữ liệu người dùng nhập, dùng chung cho các servlet. */
public final class InputUtil {

    /** Giá trị tiền tối đa chấp nhận (cột DECIMAL(18,2) còn dư rất nhiều). */
    private static final BigDecimal MAX_MONEY = new BigDecimal("9999999999999");

    private InputUtil() {
    }

    /** Bỏ khoảng trắng đầu/cuối; null trở thành chuỗi rỗng. */
    public static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    /** Đọc số nguyên; thiếu hoặc sai định dạng thì trả về giá trị dự phòng thay vì ném lỗi 500. */
    public static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(clean(value));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /**
     * Đọc số tiền. Trả về null khi sai định dạng, là số âm hoặc quá lớn.
     * Chuỗi rỗng được coi là 0 (dùng cho các khoản chi phí không bắt buộc).
     */
    public static BigDecimal parseMoney(String value) {
        String v = clean(value);
        if (v.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            BigDecimal d = new BigDecimal(v);
            if (d.signum() < 0 || d.compareTo(MAX_MONEY) > 0) {
                return null;
            }
            return d.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException | ArithmeticException e) {
            return null;
        }
    }
}

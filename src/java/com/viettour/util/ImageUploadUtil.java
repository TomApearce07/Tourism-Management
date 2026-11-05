package com.viettour.util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.Locale;

/**
 * Lưu ảnh tour tải lên một cách an toàn vào /static/images.
 *
 *  - Chỉ nhận đuôi jpg, jpeg, png, webp, gif và tối đa 5 MB.
 *  - Kiểm tra cả "chữ ký" đầu file (magic bytes) khớp với đuôi, nên file .jsp đổi tên thành
 *    .jpg vẫn bị từ chối.
 *  - Tên file do server tự sinh (không dùng tên khách gửi), không ghi đè ảnh cũ và không thể
 *    thoát ra ngoài thư mục ảnh.
 */
public final class ImageUploadUtil {

    public static final String DEFAULT_IMAGE = "default-tour.jpg";

    private static final long MAX_BYTES = 5L * 1024 * 1024;
    private static final SecureRandom RANDOM = new SecureRandom();

    private ImageUploadUtil() {
    }

    /** Kết quả lưu ảnh: có tên file mới, hoặc có thông báo lỗi, hoặc cả hai đều null (không tải ảnh). */
    public static final class Result {
        public final String fileName;
        public final String error;

        private Result(String fileName, String error) {
            this.fileName = fileName;
            this.error = error;
        }

        public boolean hasFile() {
            return fileName != null;
        }
    }

    public static Result save(Part part, ServletContext context) throws IOException {
        if (part == null || part.getSize() <= 0) {
            return new Result(null, null);               // người dùng không chọn ảnh
        }
        if (part.getSize() > MAX_BYTES) {
            return new Result(null, "Ảnh quá lớn, vui lòng chọn ảnh tối đa 5 MB!");
        }

        String ext = extensionOf(part.getSubmittedFileName());
        if (ext == null) {
            return new Result(null, "Chỉ chấp nhận ảnh định dạng JPG, PNG, WEBP hoặc GIF!");
        }
        String contentType = part.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            return new Result(null, "Tệp tải lên không phải là ảnh!");
        }

        byte[] head = new byte[12];
        int read;
        try (InputStream in = part.getInputStream()) {
            read = in.readNBytes(head, 0, head.length);
        }
        if (!matchesSignature(ext, head, read)) {
            return new Result(null, "Nội dung tệp không khớp với định dạng ảnh " + ext.toUpperCase(Locale.ROOT) + "!");
        }

        String realPath = context.getRealPath("/static/images");
        if (realPath == null) {
            return new Result(null, "Máy chủ chưa hỗ trợ lưu ảnh tải lên (ứng dụng chưa được giải nén).");
        }
        Path dir = Paths.get(realPath).toAbsolutePath().normalize();
        Files.createDirectories(dir);

        String name = "tour_" + System.currentTimeMillis() + "_" + Integer.toHexString(RANDOM.nextInt(0x1000000)) + "." + ext;
        Path target = dir.resolve(name).normalize();
        if (!target.startsWith(dir)) {
            return new Result(null, "Tên tệp không hợp lệ!");
        }
        try (InputStream in = part.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return new Result(name, null);
    }

    /** Trả về đuôi file hợp lệ (viết thường) hoặc null. */
    static String extensionOf(String submittedName) {
        if (submittedName == null) {
            return null;
        }
        String name = submittedName.toLowerCase(Locale.ROOT);
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) {
            return null;
        }
        String ext = name.substring(dot + 1);
        switch (ext) {
            case "jpg":
            case "jpeg":
            case "png":
            case "webp":
            case "gif":
                return ext;
            default:
                return null;
        }
    }

    /** So các byte đầu file với chữ ký của từng định dạng ảnh. */
    static boolean matchesSignature(String ext, byte[] b, int len) {
        switch (ext) {
            case "jpg":
            case "jpeg":
                return len >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF;
            case "png":
                return len >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G'
                        && b[4] == 0x0D && b[5] == 0x0A && b[6] == 0x1A && b[7] == 0x0A;
            case "gif":
                return len >= 6 && b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8'
                        && (b[4] == '7' || b[4] == '9') && b[5] == 'a';
            case "webp":
                return len >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                        && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P';
            default:
                return false;
        }
    }
}

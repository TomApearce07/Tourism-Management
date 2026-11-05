package com.viettour.controller.provider;

import com.viettour.dao.TourDAO;
import com.viettour.model.Account;
import com.viettour.model.Tour;
import com.viettour.util.ImageUploadUtil;
import com.viettour.util.TourFormUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Provider tạo tour mới (URL /provider/create-tour, form multipart khai báo trong web.xml).
 *
 * Quy tắc nghiệp vụ:
 *  - Dữ liệu được kiểm tra đầy đủ (xem TourFormUtil), sai thì quay lại trang quản lý kèm thông báo.
 *  - Ảnh được kiểm tra loại file và lưu bằng tên do server sinh (xem ImageUploadUtil).
 *  - Tour mới luôn ở trạng thái PENDING (chờ Manager duyệt) và thuộc về Provider đang đăng nhập.
 */
public class CreateTourServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Account account = (Account) request.getSession().getAttribute("account");
        String base = request.getContextPath();

        Tour tour = new Tour();
        String error;
        try {
            error = TourFormUtil.populate(request, tour, 1);
            if (error == null) {
                ImageUploadUtil.Result image = ImageUploadUtil.save(request.getPart("imageFile"), getServletContext());
                if (image.error != null) {
                    error = image.error;
                } else {
                    tour.setImageUrl(image.hasFile() ? image.fileName : ImageUploadUtil.DEFAULT_IMAGE);
                }
            }
        } catch (IllegalStateException e) {      // vượt giới hạn dung lượng khai báo trong web.xml
            error = "Dữ liệu tải lên quá lớn, vui lòng chọn ảnh nhỏ hơn!";
        }

        if (error != null) {
            request.getSession().setAttribute("providerError", error);
            response.sendRedirect(base + "/provider/dashboard?create=1");   // main.js tự mở lại hộp thoại tạo tour
            return;
        }

        tour.setProviderId(account.getAccountId());
        tour.setApprovalStatus("PENDING");

        TourDAO tourDAO = new TourDAO();
        boolean saved;
        try {
            saved = tourDAO.insertTour(tour);
        } finally {
            tourDAO.close();
        }
        if (!saved) {
            request.getSession().setAttribute("providerError", "Không thể lưu tour lúc này. Vui lòng thử lại!");
            response.sendRedirect(base + "/provider/dashboard?create=1");
            return;
        }
        response.sendRedirect(base + "/provider/dashboard");
    }
}

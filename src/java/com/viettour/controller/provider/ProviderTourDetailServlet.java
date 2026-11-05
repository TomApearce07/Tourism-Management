package com.viettour.controller.provider;

import com.viettour.dao.TourDAO;
import com.viettour.dao.TourDepartureDAO;
import com.viettour.model.Account;
import com.viettour.model.Tour;
import com.viettour.util.ImageUploadUtil;
import com.viettour.util.InputUtil;
import com.viettour.util.TourFormUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Provider xem và sửa tour của chính mình (URL /provider/tour-detail, form multipart khai báo trong web.xml).
 *
 * Quy tắc nghiệp vụ:
 *  - Chỉ sửa được tour thuộc về mình.
 *  - Ảnh cũ được lấy từ DB (không tin trường ẩn "existingImageUrl" từ form).
 *  - Sau khi sửa, tour quay về PENDING để Manager duyệt lại.
 */
public class ProviderTourDetailServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Account account = (Account) request.getSession().getAttribute("account");
        int id = InputUtil.parseInt(request.getParameter("id"), -1);

        TourDAO tourDAO = new TourDAO();
        TourDepartureDAO depDAO = new TourDepartureDAO();
        Tour tour;
        try {
            tour = tourDAO.getTourById(id);
            if (tour != null && tour.getProviderId() == account.getAccountId()) {
                request.setAttribute("departures", depDAO.getAllDepartures(id));   // để Provider quản lý ngày khởi hành
            }
        } finally {
            tourDAO.close();
            depDAO.close();
        }

        if (tour == null || tour.getProviderId() != account.getAccountId()) {
            response.sendRedirect(request.getContextPath() + "/provider/dashboard");
            return;
        }
        request.setAttribute("tour", tour);
        request.getRequestDispatcher("/views/provider/provider-tour-detail.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Account account = (Account) request.getSession().getAttribute("account");
        String base = request.getContextPath();

        int tourId = InputUtil.parseInt(request.getParameter("tourId"), -1);

        TourDAO tourDAO = new TourDAO();
        try {
            Tour current = tourDAO.getTourById(tourId);
            if (current == null || current.getProviderId() != account.getAccountId()) {
                response.sendRedirect(base + "/provider/dashboard");
                return;
            }

            Tour tour = new Tour();
            String error;
            try {
                error = TourFormUtil.populate(request, tour, 0);
                if (error == null) {
                    ImageUploadUtil.Result image = ImageUploadUtil.save(request.getPart("imageFile"), getServletContext());
                    if (image.error != null) {
                        error = image.error;
                    } else {
                        tour.setImageUrl(image.hasFile() ? image.fileName : current.getImageUrl());
                    }
                }
            } catch (IllegalStateException e) {  // vượt giới hạn dung lượng khai báo trong web.xml
                error = "Dữ liệu tải lên quá lớn, vui lòng chọn ảnh nhỏ hơn!";
            }

            if (error != null) {
                request.getSession().setAttribute("providerError", error);
                response.sendRedirect(base + "/provider/tour-detail?id=" + tourId);
                return;
            }

            tour.setTourId(tourId);
            tour.setProviderId(account.getAccountId());
            tour.setApprovalStatus("PENDING");           // chỉnh sửa xong phải được duyệt lại

            if (!tourDAO.updateTour(tour)) {
                request.getSession().setAttribute("providerError", "Không thể lưu thay đổi lúc này. Vui lòng thử lại!");
                response.sendRedirect(base + "/provider/tour-detail?id=" + tourId);
                return;
            }
        } finally {
            tourDAO.close();
        }
        response.sendRedirect(base + "/provider/dashboard?updated=true");
    }
}

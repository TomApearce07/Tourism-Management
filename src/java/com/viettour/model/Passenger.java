package com.viettour.model;

import java.sql.Date;

/** Một người đi tour trong đơn đặt (bảng BookingPassengers). */
public class Passenger {

    private int passengerId;
    private int bookingId;
    private String fullName;
    private String gender;          // MALE, FEMALE, OTHER hoặc null
    private Date dateOfBirth;
    private String idCard;
    private String phone;
    private String passengerType;   // ADULT, CHILD
    private boolean representative;

    public Passenger() {
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
    }

    public boolean isRepresentative() {
        return representative;
    }

    public void setRepresentative(boolean representative) {
        this.representative = representative;
    }

    /** Che CCCD/Passport cho Manager: giữ 4 ký tự đầu và 2 ký tự cuối, ví dụ 0012****45. */
    public String getMaskedIdCard() {
        if (idCard == null || idCard.isEmpty()) {
            return "";
        }
        if (idCard.length() <= 6) {
            return "****";
        }
        StringBuilder sb = new StringBuilder(idCard.substring(0, 4));
        for (int i = 4; i < idCard.length() - 2; i++) {
            sb.append('*');
        }
        sb.append(idCard.substring(idCard.length() - 2));
        return sb.toString();
    }

    public String getGenderLabel() {
        if ("MALE".equals(gender)) {
            return "Nam";
        }
        if ("FEMALE".equals(gender)) {
            return "Nữ";
        }
        if ("OTHER".equals(gender)) {
            return "Khác";
        }
        return "";
    }

    /** Ngày khởi hành dạng dd/MM/yyyy để hiển thị trên JSP (rỗng nếu chưa có). */
    public String getDobText() {
        return getDateOfBirth() == null ? "" : getDateOfBirth().toLocalDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}

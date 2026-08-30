package model;

public class Booking {
    private int bookingId;
    private int customerId;
    private int flightId;
    private String bookingDate;
    private String status;
    private double totalAmount;
    private String pnrCode;
    private String cabinClass; // Economy, Premium Economy, Business, First

    public Booking() {
        this.cabinClass = "Economy";
    }

    public Booking(int bookingId, int customerId, int flightId, String bookingDate, String status, double totalAmount) {
        this(bookingId, customerId, flightId, bookingDate, status, totalAmount, null, "Economy");
    }

    public Booking(int customerId, int flightId, String bookingDate, String status, double totalAmount) {
        this(0, customerId, flightId, bookingDate, status, totalAmount, null, "Economy");
    }

    public Booking(int bookingId, int customerId, int flightId, String bookingDate, String status, double totalAmount, String pnrCode, String cabinClass) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.flightId = flightId;
        this.bookingDate = bookingDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.pnrCode = pnrCode;
        this.cabinClass = cabinClass;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPnrCode() {
        return pnrCode;
    }

    public void setPnrCode(String pnrCode) {
        this.pnrCode = pnrCode;
    }

    public String getCabinClass() {
        return cabinClass != null ? cabinClass : "Economy";
    }

    public void setCabinClass(String cabinClass) {
        this.cabinClass = cabinClass;
    }
}

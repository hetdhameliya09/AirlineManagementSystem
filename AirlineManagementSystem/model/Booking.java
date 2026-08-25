package model;

public class Booking {
    private int bookingId;
    private int customerId;
    private int flightId;
    private String bookingDate;
    private String status;
    private double totalAmount;

    public Booking() {}

    public Booking(int bookingId, int customerId, int flightId, String bookingDate, String status, double totalAmount) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.flightId = flightId;
        this.bookingDate = bookingDate;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public Booking(int customerId, int flightId, String bookingDate, String status, double totalAmount) {
        this.customerId = customerId;
        this.flightId = flightId;
        this.bookingDate = bookingDate;
        this.status = status;
        this.totalAmount = totalAmount;
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

    @Override
    public String toString() {
        return "Booking [ID=" + bookingId + ", CustomerID=" + customerId + ", FlightID=" + flightId + ", Date=" + bookingDate + ", Status=" + status + ", Total=$" + totalAmount + "]";
    }
}

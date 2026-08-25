package model;

public class HotelBooking {
    private int hotelBookingId;
    private int customerId;
    private int hotelId;
    private String checkInDate;
    private String checkOutDate;
    private int numRooms;
    private double totalPrice;
    private String status;

    public HotelBooking() {}

    public HotelBooking(int hotelBookingId, int customerId, int hotelId, String checkInDate, String checkOutDate, int numRooms, double totalPrice, String status) {
        this.hotelBookingId = hotelBookingId;
        this.customerId = customerId;
        this.hotelId = hotelId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numRooms = numRooms;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public HotelBooking(int customerId, int hotelId, String checkInDate, String checkOutDate, int numRooms, double totalPrice, String status) {
        this.customerId = customerId;
        this.hotelId = hotelId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numRooms = numRooms;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public int getHotelBookingId() {
        return hotelBookingId;
    }

    public void setHotelBookingId(int hotelBookingId) {
        this.hotelBookingId = hotelBookingId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getNumRooms() {
        return numRooms;
    }

    public void setNumRooms(int numRooms) {
        this.numRooms = numRooms;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "HotelBooking [ID=" + hotelBookingId + ", CustomerID=" + customerId + ", HotelID=" + hotelId + ", CheckIn=" + checkInDate + ", CheckOut=" + checkOutDate + ", Rooms=" + numRooms + ", Total=$" + totalPrice + ", Status=" + status + "]";
    }
}

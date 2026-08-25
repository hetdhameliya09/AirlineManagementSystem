package model;

public class Ticket {
    private int ticketId;
    private int bookingId;
    private int passengerId;
    private String ticketNumber;
    private String seatNumber;
    private double price;
    private String issueDate;

    public Ticket() {}

    public Ticket(int ticketId, int bookingId, int passengerId, String ticketNumber, String seatNumber, double price, String issueDate) {
        this.ticketId = ticketId;
        this.bookingId = bookingId;
        this.passengerId = passengerId;
        this.ticketNumber = ticketNumber;
        this.seatNumber = seatNumber;
        this.price = price;
        this.issueDate = issueDate;
    }

    public Ticket(int bookingId, int passengerId, String ticketNumber, String seatNumber, double price, String issueDate) {
        this.bookingId = bookingId;
        this.passengerId = passengerId;
        this.ticketNumber = ticketNumber;
        this.seatNumber = seatNumber;
        this.price = price;
        this.issueDate = issueDate;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    @Override
    public String toString() {
        return "Ticket [ID=" + ticketId + ", BookingID=" + bookingId + ", PassengerID=" + passengerId + ", TicketNo=" + ticketNumber + ", Seat=" + seatNumber + ", Price=$" + price + ", Issued=" + issueDate + "]";
    }
}

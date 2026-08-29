package model;

public class Passenger {
    private int passengerId;
    private int bookingId;
    private String name;
    private int age;
    private String gender;
    private String seatNumber;

    public Passenger() {}

    public Passenger(int passengerId, int bookingId, String name, int age, String gender, String seatNumber) {
        this.passengerId = passengerId;
        this.bookingId = bookingId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.seatNumber = seatNumber;
    }

    public Passenger(int bookingId, String name, int age, String gender, String seatNumber) {
        this.bookingId = bookingId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.seatNumber = seatNumber;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    @Override
    public String toString() {
        return "Passenger [ID=" + passengerId + ", BookingID=" + bookingId + ", Name=" + name + ", Age=" + age + ", Gender=" + gender + ", Seat=" + seatNumber + "]";
    }
}

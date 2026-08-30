package model;

public class Passenger {
    private int passengerId;
    private int bookingId;
    private String name;
    private int age;
    private String gender;
    private String seatNumber;
    private String nationality;
    private String mealPreference;

    public Passenger() {
        this.nationality = "Indian";
        this.mealPreference = "Standard";
    }

    public Passenger(int passengerId, int bookingId, String name, int age, String gender, String seatNumber) {
        this(passengerId, bookingId, name, age, gender, seatNumber, "Indian", "Standard");
    }

    public Passenger(int bookingId, String name, int age, String gender, String seatNumber) {
        this(0, bookingId, name, age, gender, seatNumber, "Indian", "Standard");
    }

    public Passenger(int passengerId, int bookingId, String name, int age, String gender, String seatNumber, String nationality, String mealPreference) {
        this.passengerId = passengerId;
        this.bookingId = bookingId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.seatNumber = seatNumber;
        this.nationality = nationality;
        this.mealPreference = mealPreference;
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

    public String getNationality() {
        return nationality != null ? nationality : "Indian";
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getMealPreference() {
        return mealPreference != null ? mealPreference : "Standard";
    }

    public void setMealPreference(String mealPreference) {
        this.mealPreference = mealPreference;
    }
}

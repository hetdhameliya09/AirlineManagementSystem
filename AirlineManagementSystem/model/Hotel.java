package model;

public class Hotel {
    private int hotelId;
    private String hotelName;
    private String city;
    private double pricePerNight;
    private int availableRooms;
    private double rating;

    public Hotel() {}

    public Hotel(int hotelId, String hotelName, String city, double pricePerNight, int availableRooms, double rating) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.city = city;
        this.pricePerNight = pricePerNight;
        this.availableRooms = availableRooms;
        this.rating = rating;
    }

    public Hotel(String hotelName, String city, double pricePerNight, int availableRooms, double rating) {
        this.hotelName = hotelName;
        this.city = city;
        this.pricePerNight = pricePerNight;
        this.availableRooms = availableRooms;
        this.rating = rating;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(int availableRooms) {
        this.availableRooms = availableRooms;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Hotel [ID=" + hotelId + ", Name=" + hotelName + ", City=" + city + ", Price/Night=$" + pricePerNight + ", Rooms=" + availableRooms + ", Rating=" + rating + "]";
    }
}

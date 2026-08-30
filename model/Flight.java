package model;

public class Flight {
    private int flightId;
    private String flightNumber;
    private String airlineName;
    private String departureAirport;
    private String arrivalAirport;
    private String departureTime;
    private String arrivalTime;
    private double price;
    private int availableSeats;
    private String aircraftModel;
    private String flightStatus; // ON_TIME, BOARDING, DELAYED, DEPARTED, CANCELLED
    private String departureTerminal;
    private String gateNumber;
    private int durationMinutes;

    public Flight() {
        this.aircraftModel = "Airbus A320neo";
        this.flightStatus = "ON_TIME";
        this.departureTerminal = "T3";
        this.gateNumber = "B04";
        this.durationMinutes = 135;
    }

    public Flight(int flightId, String flightNumber, String airlineName, String departureAirport, String arrivalAirport, String departureTime, String arrivalTime, double price, int availableSeats) {
        this(flightId, flightNumber, airlineName, departureAirport, arrivalAirport, departureTime, arrivalTime, price, availableSeats, "Airbus A320neo", "ON_TIME", "T3", "B04", 135);
    }

    public Flight(String flightNumber, String airlineName, String departureAirport, String arrivalAirport, String departureTime, String arrivalTime, double price, int availableSeats) {
        this(0, flightNumber, airlineName, departureAirport, arrivalAirport, departureTime, arrivalTime, price, availableSeats, "Airbus A320neo", "ON_TIME", "T3", "B04", 135);
    }

    public Flight(int flightId, String flightNumber, String airlineName, String departureAirport, String arrivalAirport, String departureTime, String arrivalTime, double price, int availableSeats, String aircraftModel, String flightStatus, String departureTerminal, String gateNumber, int durationMinutes) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airlineName = airlineName;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.availableSeats = availableSeats;
        this.aircraftModel = aircraftModel;
        this.flightStatus = flightStatus;
        this.departureTerminal = departureTerminal;
        this.gateNumber = gateNumber;
        this.durationMinutes = durationMinutes;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getAircraftModel() {
        return aircraftModel != null ? aircraftModel : "Airbus A320neo";
    }

    public void setAircraftModel(String aircraftModel) {
        this.aircraftModel = aircraftModel;
    }

    public String getFlightStatus() {
        return flightStatus != null ? flightStatus : "ON_TIME";
    }

    public void setFlightStatus(String flightStatus) {
        this.flightStatus = flightStatus;
    }

    public String getDepartureTerminal() {
        return departureTerminal != null ? departureTerminal : "T3";
    }

    public void setDepartureTerminal(String departureTerminal) {
        this.departureTerminal = departureTerminal;
    }

    public String getGateNumber() {
        return gateNumber != null ? gateNumber : "B04";
    }

    public void setGateNumber(String gateNumber) {
        this.gateNumber = gateNumber;
    }

    public int getDurationMinutes() {
        return durationMinutes > 0 ? durationMinutes : 135;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}

package model;

public class Taxi {
    private int taxiId;
    private String driverName;
    private String phoneNumber;
    private String vehicleNumber;
    private String vehicleType;
    private double pricePerKm;
    private String status;

    public Taxi() {}

    public Taxi(int taxiId, String driverName, String phoneNumber, String vehicleNumber, String vehicleType, double pricePerKm, String status) {
        this.taxiId = taxiId;
        this.driverName = driverName;
        this.phoneNumber = phoneNumber;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.pricePerKm = pricePerKm;
        this.status = status;
    }

    public Taxi(String driverName, String phoneNumber, String vehicleNumber, String vehicleType, double pricePerKm, String status) {
        this.driverName = driverName;
        this.phoneNumber = phoneNumber;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.pricePerKm = pricePerKm;
        this.status = status;
    }

    public int getTaxiId() {
        return taxiId;
    }

    public void setTaxiId(int taxiId) {
        this.taxiId = taxiId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getPricePerKm() {
        return pricePerKm;
    }

    public void setPricePerKm(double pricePerKm) {
        this.pricePerKm = pricePerKm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Taxi [ID=" + taxiId + ", Driver=" + driverName + ", Phone=" + phoneNumber + ", VehicleNo=" + vehicleNumber + ", Type=" + vehicleType + ", Rate/km=$" + pricePerKm + ", Status=" + status + "]";
    }
}

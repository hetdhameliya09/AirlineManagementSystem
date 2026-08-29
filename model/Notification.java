package model;

public class Notification {
    private int notificationId;
    private int customerId;
    private String message;
    private String createdAt;
    private String status;

    public Notification() {}

    public Notification(int notificationId, int customerId, String message, String createdAt, String status) {
        this.notificationId = notificationId;
        this.customerId = customerId;
        this.message = message;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Notification(int customerId, String message, String createdAt, String status) {
        this.customerId = customerId;
        this.message = message;
        this.createdAt = createdAt;
        this.status = status;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Notification [ID=" + notificationId + ", CustomerID=" + customerId + ", Message=" + message + ", Date=" + createdAt + ", Status=" + status + "]";
    }
}

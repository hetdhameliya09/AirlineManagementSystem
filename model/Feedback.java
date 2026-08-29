package model;

public class Feedback {
    private int feedbackId;
    private int customerId;
    private int rating;
    private String comments;
    private String createdAt;

    public Feedback() {}

    public Feedback(int feedbackId, int customerId, int rating, String comments, String createdAt) {
        this.feedbackId = feedbackId;
        this.customerId = customerId;
        this.rating = rating;
        this.comments = comments;
        this.createdAt = createdAt;
    }

    public Feedback(int customerId, int rating, String comments, String createdAt) {
        this.customerId = customerId;
        this.rating = rating;
        this.comments = comments;
        this.createdAt = createdAt;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Feedback [ID=" + feedbackId + ", CustomerID=" + customerId + ", Rating=" + rating + "/5, Comments=" + comments + ", Date=" + createdAt + "]";
    }
}

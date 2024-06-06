package com.eCommerceSite.WhimsyCrafts.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "feedback")
public class Feedback {

    @Id
    private String id;

    @NotBlank
    private String productId;

    @NotBlank
    private String userId;

    private double rating;
    private String comment;
    private int likes;
    private int dislikes;
    private Set<String> likedBy = new HashSet<>();
    private Set<String> dislikedBy = new HashSet<>();

    public Feedback() {}

    public Feedback(String productId, String userId, double rating, String comment) {
        this.productId = productId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.likes = 0;
        this.dislikes = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public Set<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(Set<String> likedBy) {
        this.likedBy = likedBy;
    }

    public Set<String> getDislikedBy() {
        return dislikedBy;
    }

    public void setDislikedBy(Set<String> dislikedBy) {
        this.dislikedBy = dislikedBy;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id='" + id + '\'' +
                ", productId='" + productId + '\'' +
                ", userId='" + userId + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                '}';
    }
}


package com.eCommerceSite.WhimsyCrafts.controller;

import com.eCommerceSite.WhimsyCrafts.model.Feedback;
import com.eCommerceSite.WhimsyCrafts.repository.FeedbackRepository;
import com.eCommerceSite.WhimsyCrafts.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Map<String, Object>> submitFeedback(@RequestParam String productId, @RequestParam double rating, @RequestParam(required = false) String comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        if (!productRepository.existsById(productId)) {
            return ResponseEntity.status(400).body(Map.of("error", "Product does not exist"));
        }

        if (rating < 0.0 || rating > 5.0) {
            return ResponseEntity.status(400).body(Map.of("error", "Rating must be between 0.0 and 5.0"));
        }
        double roundedRating = Math.round(rating * 10) / 10.0;

        Feedback feedback = new Feedback();
        feedback.setProductId(productId);
        feedback.setUserId(userId);
        feedback.setRating(roundedRating);
        feedback.setComment(comment);

        feedbackRepository.save(feedback);

        // Prepare the response
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Feedback submitted successfully");
        response.put("productId", productId);
        response.put("rating", roundedRating);
        response.put("comment", comment);
        response.put("likeCount", feedback.getLikes());
        response.put("dislikeCount", feedback.getDislikes());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{feedbackId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<String> updateFeedback(@PathVariable String feedbackId, @RequestParam(required = false) Double rating, @RequestParam(required = false) String comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        Optional<Feedback> feedbackOpt = feedbackRepository.findById(feedbackId);
        if (feedbackOpt.isEmpty() || !feedbackOpt.get().getUserId().equals(userId)) {
            return ResponseEntity.status(403).body("You can only update your own feedback");
        }

        Feedback feedback = feedbackOpt.get();
        if (rating != null) {
            feedback.setRating(rating);
        }
        if (comment != null) {
            feedback.setComment(comment);
        }

        feedbackRepository.save(feedback);

        return ResponseEntity.ok("Feedback updated successfully");
    }

    @DeleteMapping("/{feedbackId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<String> deleteFeedback(@PathVariable String feedbackId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        Optional<Feedback> feedbackOpt = feedbackRepository.findById(feedbackId);
        if (feedbackOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Feedback not found");
        }

        Feedback feedback = feedbackOpt.get();
        if (!feedback.getUserId().equals(userId) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("You can only delete your own feedback");
        }

        feedbackRepository.deleteById(feedbackId);

        return ResponseEntity.ok("Feedback deleted successfully");
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<List<Feedback>> getProductFeedback(@PathVariable String productId) {
        if (!productRepository.existsById(productId)) {
            return ResponseEntity.status(400).body(List.of());
        }
        List<Feedback> feedbackList = feedbackRepository.findByProductId(productId);
        return ResponseEntity.ok(feedbackList);
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<List<Feedback>> getUserFeedback(@PathVariable String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserId = authentication.getName();

        if (!userId.equals(authenticatedUserId)) {
            return ResponseEntity.status(403).body(null);
        }

        List<Feedback> feedbackList = feedbackRepository.findByUserId(userId);
        return ResponseEntity.ok(feedbackList);
    }

    @GetMapping("/average-rating/{productId}")
    public ResponseEntity<Double> getProductAverageRating(@PathVariable String productId) {
        List<Feedback> feedbackList = feedbackRepository.findByProductId(productId);
        double averageRating = feedbackList.stream().collect(Collectors.averagingDouble(Feedback::getRating));
        double roundedRating = Math.round(averageRating * 10) / 10.0;
        return ResponseEntity.ok(roundedRating);
    }

    @PostMapping("/{feedbackId}/like")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<String> likeFeedback(@PathVariable String feedbackId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        Optional<Feedback> feedbackOptional = feedbackRepository.findById(feedbackId);
        if (feedbackOptional.isPresent()) {
            Feedback feedback = feedbackOptional.get();

            if (feedback.getDislikedBy().contains(userId)) {
                feedback.getDislikedBy().remove(userId);
                feedback.setDislikes(feedback.getDislikes() - 1);
            }

            if (feedback.getLikedBy().contains(userId)) {
                return ResponseEntity.status(400).body("You have already liked this feedback.");
            }

            feedback.getLikedBy().add(userId);
            feedback.setLikes(feedback.getLikes() + 1);
            feedbackRepository.save(feedback);

            return ResponseEntity.ok("Feedback liked successfully.");
        } else {
            return ResponseEntity.status(404).body("Feedback not found.");
        }
    }

    @PostMapping("/{feedbackId}/dislike")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<String> dislikeFeedback(@PathVariable String feedbackId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        Optional<Feedback> feedbackOptional = feedbackRepository.findById(feedbackId);
        if (feedbackOptional.isPresent()) {
            Feedback feedback = feedbackOptional.get();

            if (feedback.getLikedBy().contains(userId)) {
                feedback.getLikedBy().remove(userId);
                feedback.setLikes(feedback.getLikes() - 1);
            }

            if (feedback.getDislikedBy().contains(userId)) {
                return ResponseEntity.status(400).body("You have already disliked this feedback.");
            }

            feedback.getDislikedBy().add(userId);
            feedback.setDislikes(feedback.getDislikes() + 1);
            feedbackRepository.save(feedback);

            return ResponseEntity.ok("Feedback disliked successfully.");
        } else {
            return ResponseEntity.status(404).body("Feedback not found.");
        }
    }
}


package com.eCommerceSite.WhimsyCrafts.controller;

import com.eCommerceSite.WhimsyCrafts.model.Feedback;
import com.eCommerceSite.WhimsyCrafts.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/WhimsyCrafts/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Map<String, Object>> submitFeedback(@RequestParam String productId, @RequestParam double rating, @RequestParam(required = false) String comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return feedbackService.submitFeedback(productId, rating, comment, userId);
    }

    @PutMapping("/{feedbackId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<String> updateFeedback(@PathVariable String feedbackId, @RequestParam(required = false) Double rating, @RequestParam(required = false) String comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return feedbackService.updateFeedback(feedbackId, rating, comment, userId);
    }

    @DeleteMapping("/{feedbackId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<String> deleteFeedback(@PathVariable String feedbackId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return feedbackService.deleteFeedback(feedbackId, userId);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<List<Feedback>> getProductFeedback(@PathVariable String productId) {
        return feedbackService.getProductFeedback(productId);
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<List<Feedback>> getUserFeedback(@PathVariable String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserId = authentication.getName();
        return feedbackService.getUserFeedback(userId, authenticatedUserId);
    }

    @GetMapping("/average-rating/{productId}")
    public ResponseEntity<Double> getProductAverageRating(@PathVariable String productId) {
        return feedbackService.getProductAverageRating(productId);
    }

    @PostMapping("/{feedbackId}/like")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<String> likeFeedback(@PathVariable String feedbackId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return feedbackService.likeFeedback(feedbackId, userId);
    }

    @PostMapping("/{feedbackId}/dislike")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<String> dislikeFeedback(@PathVariable String feedbackId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return feedbackService.dislikeFeedback(feedbackId, userId);
    }
}



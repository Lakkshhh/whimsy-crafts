package com.eCommerceSite.WhimsyCrafts.repository;

import com.eCommerceSite.WhimsyCrafts.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    List<Feedback> findByProductId(String productId);
    List<Feedback> findByUserId(String userId);
}


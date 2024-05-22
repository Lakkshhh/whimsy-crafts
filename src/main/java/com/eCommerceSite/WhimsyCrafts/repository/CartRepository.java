package com.eCommerceSite.WhimsyCrafts.repository;

import com.eCommerceSite.WhimsyCrafts.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CartRepository extends MongoRepository<Cart, String> {
    List<Cart> findByUserIdAndStatus(String userId, String status);
}
package com.eCommerceSite.WhimsyCrafts.repository;

import com.eCommerceSite.WhimsyCrafts.model.ProductType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductTypeRepository extends MongoRepository<ProductType, String> {
    Optional<ProductType> findByName(String name);
}
package com.eCommerceSite.WhimsyCrafts.repository;
import com.eCommerceSite.WhimsyCrafts.model.Logo;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface LogoRepository extends MongoRepository<Logo, String> {
    Optional<Logo> findByName(String name);
}
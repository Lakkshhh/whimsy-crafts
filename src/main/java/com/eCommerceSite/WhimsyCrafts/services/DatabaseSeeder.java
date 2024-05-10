package com.eCommerceSite.WhimsyCrafts.services;

import com.eCommerceSite.WhimsyCrafts.model.ProductType;
import com.eCommerceSite.WhimsyCrafts.repository.ProductTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Autowired
    public DatabaseSeeder(ProductTypeRepository productTypeRepository) {
        this.productTypeRepository = productTypeRepository;
    }

    @Override
    public void run(String... args) {
        if (productTypeRepository.count() == 0) {
            // Seed the database with predefined ProductTypes
            ProductType tShirt = ProductType.tShirt();
            ProductType cushion = ProductType.cushion();
            ProductType bedSheet = ProductType.bedSheet();
            ProductType woodenBox = ProductType.woodenBox();
            ProductType vase = ProductType.vase();

            productTypeRepository.saveAll(List.of(tShirt, cushion, bedSheet, woodenBox, vase));
        }
    }
}
package com.eCommerceSite.WhimsyCrafts.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.eCommerceSite.WhimsyCrafts.model.Product;
import com.eCommerceSite.WhimsyCrafts.model.ProductType;
import com.eCommerceSite.WhimsyCrafts.repository.ProductRepository;
import com.eCommerceSite.WhimsyCrafts.repository.ProductTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Autowired
    private Environment env;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public ResponseEntity<?> addProduct(Product product) {
        if (product.getDesign() == null || product.getDesign().isEmpty() ||
                product.getPrice() <= 0 || product.getQuantity() <= 0 || product.getProductType() == null || product.getLogo() == null) {
            return ResponseEntity.badRequest().body("Invalid product details");
        }

        String cloudName = env.getProperty("cloudinary.cloud-name");
        String apiKey = env.getProperty("cloudinary.api-key");
        String apiSecret = env.getProperty("cloudinary.api-secret");

        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));

        String imageUrl = cloudinary.url()
                .transformation(new Transformation().crop("scale"))
                .generate(product.getLogo() + ".png");

        product.setLogo(imageUrl);

        Optional<ProductType> existingProductTypeOptional = productTypeRepository.findByName(product.getProductType().getName());
        if (existingProductTypeOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid ProductType: " + product.getProductType().getName());
        }

        ProductType existingProductType = existingProductTypeOptional.get();
        product.setProductType(existingProductType);

        Product savedProduct = productRepository.save(product);
        return ResponseEntity.ok(savedProduct);
    }

    public ResponseEntity<String> deleteProduct(String productId) {
        productRepository.deleteById(productId);
        return ResponseEntity.ok("Product deleted successfully");
    }
}
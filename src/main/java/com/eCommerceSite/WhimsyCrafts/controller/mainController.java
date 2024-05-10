package com.eCommerceSite.WhimsyCrafts.controller;

import com.cloudinary.Uploader;
import com.eCommerceSite.WhimsyCrafts.model.Logo;
import com.eCommerceSite.WhimsyCrafts.model.Product;
import com.eCommerceSite.WhimsyCrafts.model.ProductType;
import com.eCommerceSite.WhimsyCrafts.model.User;
import com.eCommerceSite.WhimsyCrafts.repository.LogoRepository;
import com.eCommerceSite.WhimsyCrafts.repository.ProductRepository;
import com.eCommerceSite.WhimsyCrafts.repository.ProductTypeRepository;
import com.eCommerceSite.WhimsyCrafts.repository.UserRepo;
import com.eCommerceSite.WhimsyCrafts.security.JwtTokenProvider;
import com.eCommerceSite.WhimsyCrafts.services.CloudinaryService;
import jakarta.servlet.http.Part;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import org.springframework.core.io.buffer.DataBuffer;

@RestController
@RequestMapping("/WhimsyCrafts")
public class mainController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private Environment env;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Autowired
    private LogoRepository logoRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/SignUp")
    public ResponseEntity<?> addUser(@RequestBody User user) {

        if (userRepo.existsByUsername(user.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepo.existsByEmail(user.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        user.setDefaultRole();
        userRepo.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

    }

    private class MessageResponse {
        private String message;
        public MessageResponse(String message) {
            this.message = message;
        }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @PostMapping("/SignIn")
    public String userLogin(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String providedPassword = request.get("password");

        User user = null;

        if (username != null) {
            user = userRepo.findByUsername(username);
        } else if (email != null) {
            user = userRepo.findByEmail(email);
        }

        if (user != null) {
            String storedHashedPassword = user.getPassword();
            String providedHashedPassword = DigestUtils.sha256Hex(providedPassword);

            if (storedHashedPassword.equals(providedHashedPassword)) {
                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
                        Collections.singleton(new SimpleGrantedAuthority(user.getRole().toUpperCase())));

                String token = jwtTokenProvider.generateToken(authentication);

                System.out.println("Login successful!");
                return "Login successful!\nToken: " + token;
            } else {
                throw new IllegalArgumentException("Invalid password");
            }
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

//    @GetMapping("/Login")
//    public String login() {
//        return "login";
//    }

    @GetMapping("/getUsers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @GetMapping("/getProducts")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    @PostMapping("/addProduct")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
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

    @DeleteMapping("/delete/{productId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable String productId) {
        productRepository.deleteById(productId);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @PostMapping(path="/addLogo", consumes = {"multipart/form-data"})
//    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> addLogo(@RequestParam("image") MultipartFile image,
                                          @RequestParam("name") String name) {
        try {
            String imageUrl = cloudinaryService.uploadLogo(name, image.getBytes());

            Logo logo = new Logo();
            logo.setName(name);
            logo.setUrl(imageUrl);
            logo = logoRepository.save(logo);

            return ResponseEntity.ok("Logo uploaded successfully! \nID: " + logo.getId() +
                    "\nName: " + logo.getName() +
                    "\nImage URL: " + logo.getUrl());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading logo: " + e.getMessage());
        }
    }

    @GetMapping("/getLogo/{name}")
    public ResponseEntity<?> getLogo(@PathVariable String name) {

        Optional<Logo> logoOptional = logoRepository.findByName(name);
        if (logoOptional.isPresent()) {
            String logoUrl = logoOptional.get().getUrl();
            return ResponseEntity.ok("Image URL: " + logoUrl);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deleteLogo/{name}")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteLogo(@PathVariable String name) {
        Optional<Logo> logoOptional = logoRepository.findByName(name);
        if (logoOptional.isPresent()) {
            cloudinaryService.deleteLogo(name);
            logoRepository.delete(logoOptional.get());

            return ResponseEntity.ok("Logo deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

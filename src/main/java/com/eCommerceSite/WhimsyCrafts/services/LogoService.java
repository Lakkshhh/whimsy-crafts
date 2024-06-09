package com.eCommerceSite.WhimsyCrafts.services;

import com.eCommerceSite.WhimsyCrafts.model.Logo;
import com.eCommerceSite.WhimsyCrafts.repository.LogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class LogoService {

    @Autowired
    private LogoRepository logoRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public ResponseEntity<String> addLogo(MultipartFile image, String name) {
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

    public ResponseEntity<?> getLogo(String name) {
        Optional<Logo> logoOptional = logoRepository.findByName(name);
        if (logoOptional.isPresent()) {
            String logoUrl = logoOptional.get().getUrl();
            return ResponseEntity.ok("Image URL: " + logoUrl);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<?> deleteLogo(String name) {
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


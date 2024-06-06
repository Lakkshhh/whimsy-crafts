package com.eCommerceSite.WhimsyCrafts.controller;

import com.eCommerceSite.WhimsyCrafts.model.Logo;
import com.eCommerceSite.WhimsyCrafts.repository.LogoRepository;
import com.eCommerceSite.WhimsyCrafts.services.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/WhimsyCrafts/logos")
public class LogoController {

    @Autowired
    private LogoRepository logoRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping(path = "/addLogo", consumes = {"multipart/form-data"})
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
    @PreAuthorize("hasAuthority('ADMIN')")
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

package com.eCommerceSite.WhimsyCrafts.controller;

import com.eCommerceSite.WhimsyCrafts.services.LogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/WhimsyCrafts/logos")
public class LogoController {

    @Autowired
    private LogoService logoService;

    @PostMapping(path = "/addLogo", consumes = {"multipart/form-data"})
    public ResponseEntity<String> addLogo(@RequestParam("image") MultipartFile image,
                                          @RequestParam("name") String name) {
        return logoService.addLogo(image, name);
    }

    @GetMapping("/getLogo/{name}")
    public ResponseEntity<?> getLogo(@PathVariable String name) {
        return logoService.getLogo(name);
    }

    @DeleteMapping("/deleteLogo/{name}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteLogo(@PathVariable String name) {
        return logoService.deleteLogo(name);
    }
}


package com.eCommerceSite.WhimsyCrafts.controller;

import com.eCommerceSite.WhimsyCrafts.model.User;
import com.eCommerceSite.WhimsyCrafts.repository.UserRepo;
import com.eCommerceSite.WhimsyCrafts.security.JwtTokenProvider;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/WhimsyCrafts")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private Environment env;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

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

    private static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
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

    @GetMapping("/Login")
    public String login() {
        return "login";
    }

    @GetMapping("/getUsers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
}

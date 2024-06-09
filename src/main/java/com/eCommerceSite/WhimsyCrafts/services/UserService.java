package com.eCommerceSite.WhimsyCrafts.services;

import com.eCommerceSite.WhimsyCrafts.model.User;
import com.eCommerceSite.WhimsyCrafts.repository.UserRepo;
import com.eCommerceSite.WhimsyCrafts.security.JwtTokenProvider;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public ResponseEntity<?> addUser(User user) {
        if (userRepo.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepo.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        user.setDefaultRole();
        userRepo.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    public String userLogin(Map<String, String> request) {
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

                return "Login successful!\nToken: " + token;
            } else {
                throw new IllegalArgumentException("Invalid password");
            }
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public static class MessageResponse {
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
}

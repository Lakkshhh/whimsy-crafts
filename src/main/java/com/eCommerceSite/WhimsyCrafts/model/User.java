package com.eCommerceSite.WhimsyCrafts.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.apache.commons.codec.digest.DigestUtils;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Autowired
    @JsonIgnore
    PasswordEncoder encoder;

    @Id
    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    private String role;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = "USER"; // Default role
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        String usernameRegex = "^[a-zA-Z0-9_#]+$";

        if (username != null && username.matches(usernameRegex)) {
            this.username = username;
        } else {
            throw new IllegalArgumentException("Invalid username format! Use letters, numbers, underscore, and/or hashtag.");
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        if (email.matches(emailRegex)) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("Invalid email format! Try again!");
        }
    }

    public String getPassword() { return password; }

    public void setPassword(String password) {
        this.password = DigestUtils.sha256Hex(password);
    }

    public void setDefaultRole() {
        this.role = "User";
    }
}

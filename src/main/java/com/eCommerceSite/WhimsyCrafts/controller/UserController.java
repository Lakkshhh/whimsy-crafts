package com.eCommerceSite.WhimsyCrafts.controller;

import com.eCommerceSite.WhimsyCrafts.model.User;
import com.eCommerceSite.WhimsyCrafts.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/WhimsyCrafts")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/SignUp")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @PostMapping("/SignIn")
    public String userLogin(@RequestBody Map<String, String> request) {
        return userService.userLogin(request);
    }

    @GetMapping("/Login")
    public String login() {
        return "login";
    }

    @GetMapping("/getUsers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}

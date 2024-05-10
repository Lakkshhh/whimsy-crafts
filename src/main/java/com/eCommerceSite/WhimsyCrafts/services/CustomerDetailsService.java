package com.eCommerceSite.WhimsyCrafts.services;

import com.eCommerceSite.WhimsyCrafts.model.User;
import com.eCommerceSite.WhimsyCrafts.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomerDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Autowired
    public CustomerDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user;

        if (usernameOrEmail.contains("@")) {
            user = userRepo.findByEmail(usernameOrEmail);
        } else {
            user = userRepo.findByUsername(usernameOrEmail);
        }

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(usernameOrEmail)
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }

}
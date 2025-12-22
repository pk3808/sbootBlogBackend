package com.pensieri.blog.security;

import com.pensieri.blog.model.User;
import com.pensieri.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // We use Email as the "Username" for login
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (!user.isVerified()) {
            throw new UsernameNotFoundException("User is not verified. Please verify your email first.");
        }

        // Return Spring Security's User object (Standard Spring class)
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(), 
                new ArrayList<>() // Authorities (Roles) can be added here
        );
    }
}

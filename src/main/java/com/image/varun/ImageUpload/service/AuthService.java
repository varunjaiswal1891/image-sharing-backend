package com.image.varun.ImageUpload.service;

import com.image.varun.ImageUpload.model.User;
import org.springframework.stereotype.Service;

import com.image.varun.ImageUpload.repository.UserRepository;
import com.image.varun.ImageUpload.security.JwtUtil;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // password check skipped for learning
        return jwtUtil.generateToken(username);
    }
}

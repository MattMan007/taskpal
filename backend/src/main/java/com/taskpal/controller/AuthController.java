package com.taskpal.controller;

import com.taskpal.dto.UserDTO;
import com.taskpal.security.FirebaseAuthenticationToken;
import com.taskpal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(Authentication authentication) {
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        UserDTO user = userService.createOrUpdateUser(
            firebaseAuth.getUid(),
            firebaseAuth.getName(),
            firebaseAuth.getEmail(),
            null
        );
        
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        
        return userService.getUserByFirebaseUid(firebaseAuth.getUid())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(
            @RequestBody Map<String, String> updates,
            Authentication authentication) {
        
        FirebaseAuthenticationToken firebaseAuth = (FirebaseAuthenticationToken) authentication;
        UserDTO currentUser = userService.getUserByFirebaseUid(firebaseAuth.getUid())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserDTO updatedUser = userService.updateUser(
            currentUser.getId(),
            updates.get("name"),
            updates.get("profilePictureUrl")
        );
        
        return ResponseEntity.ok(updatedUser);
    }
} 
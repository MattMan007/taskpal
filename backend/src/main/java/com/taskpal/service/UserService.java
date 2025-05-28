package com.taskpal.service;

import com.taskpal.dto.UserDTO;
import com.taskpal.model.User;
import com.taskpal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public UserDTO createOrUpdateUser(String firebaseUid, String name, String email, String profilePictureUrl) {
        Optional<User> existingUser = userRepository.findByFirebaseUid(firebaseUid);
        
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            user.setName(name);
            user.setEmail(email);
            if (profilePictureUrl != null) {
                user.setProfilePictureUrl(profilePictureUrl);
            }
        } else {
            user = new User(firebaseUid, name, email);
            if (profilePictureUrl != null) {
                user.setProfilePictureUrl(profilePictureUrl);
            }
        }
        
        user = userRepository.save(user);
        return new UserDTO(user);
    }
    
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserByFirebaseUid(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid)
                .map(UserDTO::new);
    }
    
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDTO::new);
    }
    
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserDTO::new);
    }
    
    @Transactional(readOnly = true)
    public List<UserDTO> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<UserDTO> searchUsersByEmail(String email) {
        return userRepository.findByEmailContainingIgnoreCase(email)
                .stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }
    
    public UserDTO updateUser(Long id, String name, String profilePictureUrl) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (name != null) {
            user.setName(name);
        }
        if (profilePictureUrl != null) {
            user.setProfilePictureUrl(profilePictureUrl);
        }
        
        user = userRepository.save(user);
        return new UserDTO(user);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByFirebaseUid(String firebaseUid) {
        return userRepository.existsByFirebaseUid(firebaseUid);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    // Helper method to get User entity (for internal use)
    @Transactional(readOnly = true)
    public User getUserEntityByFirebaseUid(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @Transactional(readOnly = true)
    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
} 
package com.taskpal.config;

import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

@Configuration
public class FirebaseConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);
    
    @Value("${firebase.project-id}")
    private String projectId;
    
    @Value("${firebase.credentials-path}")
    private String credentialsPath;
    
    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                ClassPathResource resource = new ClassPathResource(credentialsPath);
                
                // Check if the resource exists and is valid
                if (!resource.exists()) {
                    System.out.println("Firebase credentials file not found. Running in development mode without Firebase authentication.");
                    return;
                }
                
                try (InputStream serviceAccount = resource.getInputStream()) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .setProjectId(projectId)
                            .build();

                    FirebaseApp.initializeApp(options);
                    System.out.println("Firebase initialized successfully");
                } catch (Exception e) {
                    System.out.println("Firebase initialization failed: " + e.getMessage());
                    System.out.println("Running in development mode without Firebase authentication.");
                }
            }
        } catch (Exception e) {
            System.out.println("Firebase setup error: " + e.getMessage());
            System.out.println("Running in development mode without Firebase authentication.");
        }
    }
    
    @Bean
    public FirebaseAuth firebaseAuth() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                return FirebaseAuth.getInstance();
            }
        } catch (Exception e) {
            System.out.println("Could not create FirebaseAuth instance: " + e.getMessage());
        }
        
        // Return null for development mode - the authentication provider will handle this
        return null;
    }
} 
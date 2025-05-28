package com.taskpal.security;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

@Component
public class FirebaseAuthenticationProvider implements AuthenticationProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(FirebaseAuthenticationProvider.class);
    
    @Autowired(required = false)
    private FirebaseAuth firebaseAuth;
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (firebaseAuth == null) {
            // Development mode - skip Firebase authentication
            System.out.println("🔓 Development mode: Skipping Firebase authentication");
            return new FirebaseAuthenticationToken(
                    "dev-user", 
                    "dev-user@example.com", 
                    "Development User",
                    "dev-token",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }

        FirebaseAuthenticationToken authToken = (FirebaseAuthenticationToken) authentication;
        String idToken = authToken.getToken();
        
        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();
            
            logger.debug("Successfully authenticated user: {} ({})", name, email);
            
                        return new FirebaseAuthenticationToken(
                    uid, 
                    email, 
                    decodedToken.getName(),
                    idToken, 
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
            
        } catch (FirebaseAuthException e) {
            logger.error("Firebase authentication failed: {}", e.getMessage());
            throw new BadCredentialsException("Invalid Firebase token", e);
        }
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return FirebaseAuthenticationToken.class.isAssignableFrom(authentication);
    }
} 
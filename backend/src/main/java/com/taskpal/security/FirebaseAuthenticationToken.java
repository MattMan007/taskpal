package com.taskpal.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {
    
    private final String token;
    private final String uid;
    private final String email;
    private final String name;
    
    public FirebaseAuthenticationToken(String token) {
        super(null);
        this.token = token;
        this.uid = null;
        this.email = null;
        this.name = null;
        setAuthenticated(false);
    }
    
    public FirebaseAuthenticationToken(String uid, String email, String name, String token, 
                                     Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.token = token;
        setAuthenticated(true);
    }
    
    @Override
    public Object getCredentials() {
        return token;
    }
    
    @Override
    public Object getPrincipal() {
        return uid;
    }
    
    public String getToken() {
        return token;
    }
    
    public String getUid() {
        return uid;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getName() {
        return name;
    }
} 
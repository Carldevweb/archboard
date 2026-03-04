package com.Carldevweb.archboard.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public Long id() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof AuthUserPrincipal p) {
            return p.getId();
        }

        throw new IllegalStateException("Unsupported principal type: " + principal.getClass().getName());
    }

    public String email() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof AuthUserPrincipal p) {
            return p.getUsername();
        }

        throw new IllegalStateException("Unsupported principal type: " + principal.getClass().getName());
    }

    public String role() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof AuthUserPrincipal p) {
            return p.getRole();
        }

        throw new IllegalStateException("Unsupported principal type: " + principal.getClass().getName());
    }
}
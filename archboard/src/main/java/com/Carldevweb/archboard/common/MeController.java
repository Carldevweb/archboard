package com.Carldevweb.archboard.common;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeController {

    @GetMapping("/api/v1/me")
    public String me(Authentication auth) {
        return auth.getName();
    }
}
package com.Carldevweb.archboard.common;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MeControllerTest {

    private final MeController controller = new MeController();

    @Test
    void should_return_authenticated_username() {
        var auth = new UsernamePasswordAuthenticationToken("user@test.com", null);

        String result = controller.me(auth);

        assertEquals("user@test.com", result);
    }
}
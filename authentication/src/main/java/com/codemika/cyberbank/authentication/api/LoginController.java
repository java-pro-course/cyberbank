package com.codemika.cyberbank.authentication.api;

import com.codemika.cyberbank.authentication.service.AuthorizationService;
import com.codemika.cyberbank.authentication.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для входа.
 */
@RestController
@RequestMapping("api/auth/")
@RequiredArgsConstructor
public class LoginController {
    private final JwtUtil jwtUtil;
    private final AuthorizationService authorizationService;

    @GetMapping("login")
    public ResponseEntity<?> loginWithToken(String token) {
        return authorizationService.login(token);
    }

}
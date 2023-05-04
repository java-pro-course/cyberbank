package com.codemika.cyberbank.authentication.api;

import com.codemika.cyberbank.authentication.service.AuthorizationService;

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
    private final AuthorizationService authorizationService;

    @GetMapping("login")
    public ResponseEntity<?> loginWithToken(String token){
        return authorizationService.login(token);
    }

    @GetMapping("login")
    public ResponseEntity<?> login(@RequestParam String phone, @RequestParam String pass) {
        return authorizationService.login(phone, pass);
    }
}
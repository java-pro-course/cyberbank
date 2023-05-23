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

    @GetMapping("login-token")
    public ResponseEntity<?> loginWithToken(@RequestHeader("Authorization") String token) {
        return authorizationService.login(token);
    }

    @DeleteMapping("delete-by-phone")
    public ResponseEntity<?> deleteByPhone(String token, String phone, String password){
        return authorizationService.deleteUser(token, password, phone);
    }
    @DeleteMapping("delete-by-id")
    public ResponseEntity<?> deleteById(String token, Long id, String password){
        return authorizationService.deleteUser(token, password, id);
    }
    @DeleteMapping("delete-by-email")
    public ResponseEntity<?> deleteByEmail(String token, String email, String password){
        return authorizationService.deleteUserByEmail(token, password, email);
    }

    @GetMapping("login")
    // todo: сделать post/put-запрос, спрятать пользовательские данные в тело запроса
    public ResponseEntity<?> login(@RequestParam String phone, @RequestParam String pass) {
        return authorizationService.login(phone, pass);
    }
}
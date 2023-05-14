package com.codemika.cyberbank.authentication.api;

import com.codemika.cyberbank.authentication.annotation.UserCheck;
import com.codemika.cyberbank.authentication.dto.RqCreateUser;
import com.codemika.cyberbank.authentication.repository.UserRepository;
import com.codemika.cyberbank.authentication.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для регистрации
 */
@RestController
@RequestMapping("api/auth/")
@RequiredArgsConstructor
public class RegistrationController {
    private final AuthorizationService authorizationService;
    private final UserRepository userRepository;

    @UserCheck // обязательная аннотация для проверки пользователя
    @PostMapping("register")
    public ResponseEntity<?> registration(@RequestBody RqCreateUser rq) {
        return null;//authorizationService.registration(rq); // вообще тут можно ничего не ставить
    }
}

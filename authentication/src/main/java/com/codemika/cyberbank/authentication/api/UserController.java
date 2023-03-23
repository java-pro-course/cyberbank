package com.codemika.cyberbank.authentication.api;

import com.codemika.cyberbank.authentication.service.UserService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Data
public class UserController {
    private final UserService userService;
    @PostMapping("register-user")
    public ResponseEntity<?> register (@RequestParam String name, @RequestParam String surname, @RequestParam String patronymic,@RequestParam String phone, @RequestParam String password){
        return userService.registration(name, surname, patronymic, phone, password);
    }

}

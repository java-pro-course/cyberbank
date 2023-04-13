package com.codemika.cyberbank.authentication.api;

import com.codemika.cyberbank.authentication.service.AuthorizationService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Просто тестовый контроллер
 */
@Data
@RestController
@RequestMapping(value = "api/auth/") // перед всеми контроллерами этого метода будет ставиться этот префикс!
public class TestController {
    private final AuthorizationService service;

    @GetMapping("test") // полный url: http://localhost:8080/api/auth/test
    public ResponseEntity<?> testController() {
        return ResponseEntity.ok("Hello from auth!");
    }

    @GetMapping("get-all-users")
    public ResponseEntity<?> getAllUsers(){
        return service.getAllUsers();
    }
    @GetMapping("get-user-by-id")
    public ResponseEntity<?> getUserById(@RequestParam Long id){
        return service.getUserById(id);
    }
    @GetMapping("get-user-by-email")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email){
        return service.getUserByEmail(email);
    }
    @GetMapping("get-user-by-phone")
    public ResponseEntity<?> getUserByPhone(@RequestParam String phone){
        return service.getUserByPhone(phone);
    }
    @GetMapping("validate-user")
    public boolean validateUserById(@RequestParam Long id){return service.validateUserById(id);}

}

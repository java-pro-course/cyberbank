package com.codemika.cyberbank.authentication.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/auth/") // перед всеми контроллерами этого метода будет ставится этот префикс!
public class TestController {

    @GetMapping("test") // полный url: http://localhost:8080/api/auth/test
    public ResponseEntity<?> testController() {
        return ResponseEntity.ok("Hello from auth!");
    }
}

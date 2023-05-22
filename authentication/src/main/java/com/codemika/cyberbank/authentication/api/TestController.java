package com.codemika.cyberbank.authentication.api;

import com.codemika.cyberbank.authentication.annotation.CheckRole;
import com.codemika.cyberbank.authentication.service.AuthorizationService;
import com.codemika.cyberbank.authentication.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для тестирования
 */
@Data
@RestController
@RequestMapping(value = "api/auth/") // перед всеми контроллерами этого метода будет ставиться этот префикс!
public class TestController {
    private final AuthorizationService service;
    private final JwtUtil jwtUtil;

    @GetMapping("test") // полный url: http://localhost:8080/api/auth/test
    public ResponseEntity<?> testController() {
        return ResponseEntity.ok("Hello from auth!");
    }

    @CheckRole(isUser = true, isModer = true)
    @GetMapping("get-all-users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token) {
        return service.getAllUsers();
    }

    @CheckRole(isUser = true, isModer = true)
    @GetMapping("get-user-by-id")
    public ResponseEntity<?> getUserById(@RequestHeader("Authorization") String token, @RequestParam Long id) {
        return service.getUserById(id);
    }
    @CheckRole(isUser = true, isModer = true)
    @GetMapping("get-user-by-email")
    public ResponseEntity<?> getUserByEmail(@RequestHeader("Authorization") String token, @RequestParam String email) {
        return service.getUserByEmail(email);
    }

    @CheckRole(isUser = true, isModer = true, isTester = true)
    @GetMapping("get-user-by-phone")
    public ResponseEntity<?> getUserByPhone(@RequestHeader("Authorization") String token, @RequestParam String phone) {
        return service.getUserByPhone(phone);
    }

    @GetMapping("validate-user")
    public Boolean validateUserByToken(@RequestParam String token) {
        return service.validateUserByToken(token);
    }

    @GetMapping("get-token-claims")
    public ResponseEntity<?> beLuckyMethod(@RequestHeader("Authorization") String token) {
        Claims claimsParseToken = jwtUtil.getClaims(token);
        return ResponseEntity.ok(claimsParseToken.toString());
    }

    @CheckRole(isUser = true, isModer = true)
    @PostMapping("become-moder")
    public ResponseEntity<?> becomeModer(@RequestHeader("Authorization") String token, Long idNewModer) {
        return service.becomeModer(idNewModer);
    }
    @CheckRole(isUser = true, isModer = true, isTester = true)
    @PostMapping("become-tester")
    public ResponseEntity<?> becomeTester(@RequestHeader("Authorization") String token, Long idNewTester) {
        return service.becomeTester(idNewTester);
    }
    @CheckRole(isUser = true, isModer = true, isHacker = true)
    @PostMapping("become-hacker")
    public ResponseEntity<?> becomeHacker(@RequestHeader("Authorization") String token, Long idNewHacker) {
        return service.becomeHacker(idNewHacker);
    }
}

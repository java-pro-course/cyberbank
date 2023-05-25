package com.codemika.cyberbank.authentication.api;

import com.codemika.cyberbank.authentication.annotation.CheckRole;
import com.codemika.cyberbank.authentication.service.AuthorizationService;
import com.codemika.cyberbank.authentication.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для объединения всего, связанного с возможностями модеров
 */
@Data
@RestController
@RequestMapping(value = "api/auth/") // перед всеми контроллерами этого метода будет ставиться этот префикс!
public class ModerController {
    private final AuthorizationService service;
    private final JwtUtil jwtUtil;

    /**
     * Вывод всех пользователей банка (все их данные, кроме паролей)
     *
     * @param token токен модера
     * @return все пользователи банка
     */
    @CheckRole(isUser = true, isModer = true)
    @GetMapping("get-all-users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token) {
        return service.getAllUsers();
    }

    /**
     * Поиск пользователя по id
     *
     * @param token токен модера
     * @param id    id пользователя
     * @return искомый пользователь
     */
    @CheckRole(isUser = true, isModer = true)
    @GetMapping("get-user-by-id")
    public ResponseEntity<?> getUserById(@RequestHeader("Authorization") String token, @RequestParam Long id) {
        return service.getUserById(id);
    }

    /**
     * Поиск пользователя по адресу электронной почты
     *
     * @param token токен модера
     * @param email эл. почта пользователя
     * @return искомый пользователь
     */
    @CheckRole(isUser = true, isModer = true)
    @GetMapping("get-user-by-email")
    public ResponseEntity<?> getUserByEmail(@RequestHeader("Authorization") String token, @RequestParam String email) {
        return service.getUserByEmail(email);
    }

    /**
     * Поиск пользователя по номеру телефона
     *
     * @param token токен модера
     * @param phone телефон пользователя
     * @return искомый пользователь
     */
    @CheckRole(isUser = true, isModer = true, isTester = true)
    @GetMapping("get-user-by-phone")
    public ResponseEntity<?> getUserByPhone(@RequestHeader("Authorization") String token, @RequestParam String phone) {
        return service.getUserByPhone(phone);
    }

    /**
     * Валидация пользователя(Проверка его токена)
     *
     * @param token токен пользователя
     * @return валидный/невалидный
     */
    @GetMapping("validate-user")
    public Boolean validateUserByToken(@RequestParam String token) {
        return service.validateUserByToken(token);
    }

    /**
     * Выдача всех claims токена.
     *
     * @param token токен пользователя
     * @return claims
     */
    @GetMapping("get-token-claims")
    public ResponseEntity<?> beLuckyMethod(@RequestHeader("Authorization") String token) {
        Claims claimsParseToken = jwtUtil.getClaims(token);
        return ResponseEntity.ok(claimsParseToken.toString());
    }

    /**
     * Метод для становления модером
     *
     * @param token      токен уже существующего модера(или имеющего больший доступ пользователя)
     * @param idNewModer id пользователя, который станет модером
     * @return сообщение об успешном/не успешном становлении модером
     */
    @CheckRole(isUser = true, isModer = true)
    @PostMapping("become-moder")
    public ResponseEntity<?> becomeModer(@RequestHeader("Authorization") String token, Long idNewModer) {
        return service.becomeModer(idNewModer);
    }

    /**
     * Метод для становления тестировщиком
     *
     * @param token       токен уже существующего тестера(или имеющего такой же доступ пользователя)
     * @param idNewTester id пользователя, который станет тестером
     * @return сообщение об успешном/не успешном становлении тестером
     */
    @CheckRole(isUser = true, isModer = true, isTester = true)
    @PostMapping("become-tester")
    public ResponseEntity<?> becomeTester(@RequestHeader("Authorization") String token, Long idNewTester) {
        return service.becomeTester(idNewTester);
    }

    /**
     * Метод для становления хакером
     *
     * @param token       токен уже существующего хакером(или имеющего не меньший доступ пользователя)
     * @param idNewHacker id пользователя, который станет хакером
     * @return сообщение об успешном/не успешном становлении хакером
     */
    @CheckRole(isUser = true, isModer = true, isHacker = true)
    @PostMapping("become-hacker")
    public ResponseEntity<?> becomeHacker(@RequestHeader("Authorization") String token, Long idNewHacker) {
        return service.becomeHacker(idNewHacker);
    }
}

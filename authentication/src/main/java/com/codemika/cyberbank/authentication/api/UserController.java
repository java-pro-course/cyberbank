package com.codemika.cyberbank.authentication.api;

import com.codemika.cyberbank.authentication.annotation.CheckUser;
import com.codemika.cyberbank.authentication.dto.RqCreateUser;
import com.codemika.cyberbank.authentication.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для объединения всего, связанного с возможностями обычного пользователя
 */
@RestController
@RequestMapping("api/auth/")
@RequiredArgsConstructor
public class UserController {
    private final AuthorizationService authorizationService;

    /**
     * Регистрация(создание пользователя).
     * Не смотрите на "null", вся логика метода в аннотации!
     *
     * @param rq все данные пользователя
     * @return сообщение об успешной регистрации и токен для входа
     */
    @CheckUser // обязательная аннотация для проверки пользователя
    @PostMapping("register")
    public ResponseEntity<?> registration(@RequestBody RqCreateUser rq) {
        //ВСЯ ЛОГИКА В АННОТАЦИИ!!!
        return null;
    }

    /**
     * Обыкновенный логин по телефону и паролю
     *
     * @param phone номер телефона
     * @param pass  пароль
     * @return данные для входа из сервера
     */
    @GetMapping("login")
    // todo: сделать post/put-запрос, спрятать пользовательские данные в тело запроса
    public ResponseEntity<?> login(@RequestParam String phone, @RequestParam String pass) {
        return authorizationService.login(phone, pass);
    }

    /**
     * Выдача информации и смена токена
     *
     * @param token предыдущий(старый) токен
     * @return информация о пользователе и новый токен
     */
    @GetMapping("login-token")
    public ResponseEntity<?> loginWithToken(@RequestHeader("Authorization") String token) {
        return authorizationService.login(token);
    }

    /**
     * Удаление пользователя по номеру телефона
     *
     * @param token    токен пользователя
     * @param phone    номер телефона пользователя
     * @param password пароль пользователя
     * @return сообщение об успешном/не успешном удалении
     */
    @DeleteMapping("delete-by-phone")
    public ResponseEntity<?> deleteByPhone(@RequestHeader("Authorization") String token,
                                           @RequestParam String phone,
                                           @RequestParam String password) {
        return authorizationService.deleteUser(token, password, phone);
    }

    /**
     * Удаление пользователя по id
     *
     * @param token    токен пользователя
     * @param id       id пользователя
     * @param password пароль пользователя
     * @return сообщение об успешном/не успешном удалении
     */
    @DeleteMapping("delete-by-id")
    public ResponseEntity<?> deleteById(@RequestHeader("Authorization") String token,
                                        @RequestParam Long id,
                                        @RequestParam String password) {
        return authorizationService.deleteUser(token, password, id);
    }

    /**
     * Удаление пользователя по адресу электронной почты
     *
     * @param token    токен пользователя
     * @param email    адрес электронной почты пользователя
     * @param password пароль пользователя
     * @return сообщение об успешном/не успешном удалении
     */
    @DeleteMapping("delete-by-email")
    public ResponseEntity<?> deleteByEmail(@RequestHeader("Authorization") String token,
                                           @RequestParam String email,
                                           @RequestParam String password) {
        return authorizationService.deleteUserByEmail(token, password, email);
    }
}
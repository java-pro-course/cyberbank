package com.codemika.cyberbank.authentication.service;

import com.codemika.cyberbank.authentication.dto.RqCreateUser;
import com.codemika.cyberbank.authentication.dto.RsInfoUser;
import com.codemika.cyberbank.authentication.entity.UserEntity;
import com.codemika.cyberbank.authentication.repository.UserRepository;
import com.codemika.cyberbank.authentication.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.OverridesAttribute;
import java.util.Arrays;
import java.util.Optional;

/**
 * Сервис для авторизации
 */

@Data
@Service
public class AuthorizationService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private boolean check = false; // переменная проверенного пользователя
    private ResponseEntity<?> errorMessage; // сообщение, если что-то не так при регистрации

    /**
     * Регистрация пользователя
     *
     * @param rq запрос на создание пользователя
     * @return результат и новый токен
     */
    public ResponseEntity<?> registration(RqCreateUser rq) {
        if (!check) {
            return errorMessage;
        }

        UserEntity newUser = new UserEntity()
                .setName(rq.getName())
                .setSurname(rq.getSurname())
                .setPatronymic(rq.getPatronymic())
                .setEmail(rq.getEmail())
                .setPhone(rq.getPhone())
                .setPassword(rq.getPassword());

        userRepository.save(newUser);

        Claims claims = Jwts.claims();
        claims.put("id", newUser.getId());
        claims.put("name", newUser.getName());
        claims.put("surname", newUser.getSurname());
        claims.put("patronymic", newUser.getPatronymic());
        claims.put("email", newUser.getEmail());
        claims.put("phone", newUser.getPhone());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("Успешная регистрация! Ваш токен для подтверждения личности: " + jwtUtil.generateToken(claims));
    }

    /**
     * Вход пользователя по токену
     *
     * @param token токен
     * @return информация о пользователе
     */
    public ResponseEntity<?> login(String token) {
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Неверный токен!");
        }

        Claims claims = jwtUtil.getClaims(token);

        String name = claims.get("name", String.class);
        String surname = claims.get("surname", String.class);
        String patronymic = claims.get("patronymic", String.class);
        String email = claims.get("email", String.class);
        String phone = claims.get("phone", String.class);

        //TODO: Добавить карты, кредиты и т.д.
        String result = String.format("Добро пожаловать, %s %s %s!\n" +
                "Ваша эл. почта: %s\n" +
                "Ваш номер телефона: %s\n" +
                "Ваши карты: \n" +
                "Ваш новый токен: ", surname, name, patronymic, email, phone) + jwtUtil.generateToken(claims);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    /**
     * Поиск всех пользователей на сайте
     *
     * @return все пользователи нашего банка
     */
    public ResponseEntity<?> getAllUsers() {
        if (userRepository.findAll().isEmpty())
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("У нас ещё нет ни одного пользователя... Хотите стать первым пользователем?😔");

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(userRepository.findAll());
    }

    /**
     * Поиск пользователя по id (только для модеров)
     *
     * @param id идентификационный номер пользователя
     * @return искомого пользователя
     */
    public ResponseEntity<?> getUserById(Long id) {
        if (!userRepository.findById(id).isPresent())
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("Данный пользователь не существует!");

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(userRepository.findById(id));
    }

    /**
     * Поиск пользователя по эл. почте
     *
     * @param email эл. почта
     * @return имя, фамилию и отчество требуемого пользователя
     */
    public ResponseEntity<?> getUserByEmail(String email) {
        if (!userRepository.findByEmail(email).isPresent())
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("Данный пользователь не существует!");

        UserEntity rq = userRepository.findByEmail(email).get();
        RsInfoUser rs = new RsInfoUser()
                .setName(rq.getName())
                .setSurname(rq.getSurname())
                .setPatronymic(rq.getPatronymic());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(rs);
    }

    /**
     * Поиск пользователя по номеру телефона
     *
     * @param phone номер телефона
     * @return имя, фамилию и отчество требуемого пользователя
     */
    public ResponseEntity<?> getUserByPhone(String phone) {
        if (!userRepository.findByPhone(phone).isPresent())
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("Данный пользователь не существует!");

        UserEntity rq = userRepository.findByPhone(phone).get();
        RsInfoUser rs = new RsInfoUser()
                .setName(rq.getName())
                .setSurname(rq.getSurname())
                .setPatronymic(rq.getPatronymic());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(rs);
    }
    // TODO нужно перед удалением проверять есть ли у пользователя карты и удалять их тоже!
    public ResponseEntity<?> deleteUser(String token, String password, String phone){
        jwtUtil.validateToken(token);
        Claims claims = jwtUtil.getClaims(token);
        Optional<UserEntity> user = userRepository.findById(Long.valueOf(claims.get("id").toString()));
        if(user.isPresent()){
            if (!user.get().getPhone().equals(phone)){
                return ResponseEntity.badRequest().body("Неверный номер телефона!");
            }
            if (!user.get().getPassword().equals(password)){
                return ResponseEntity.badRequest().body("Неверный пароль!");
            }
            userRepository.deleteById(user.get().getId());
            return ResponseEntity.ok("Успешное удаление");
        }
        return ResponseEntity.badRequest().body("Пользователь не существует!");
    }
    public ResponseEntity<?> deleteUser(String token, String password, Long id){
        jwtUtil.validateToken(token);
        Claims claims = jwtUtil.getClaims(token);
        Optional<UserEntity> user = userRepository.findById(Long.valueOf(claims.get("id").toString()));
        if(user.isPresent()){
            if (!user.get().getId().equals(id)){
                return ResponseEntity.badRequest().body("Неверный id!");
            }
            if (!user.get().getPassword().equals(password)){
                return ResponseEntity.badRequest().body("Неверный пароль!");
            }
            userRepository.deleteById(id);
            return ResponseEntity.ok("Успешное удаление");
        }
        return ResponseEntity.badRequest().body("Пользователь не существует!");
    }
    public ResponseEntity<?> deleteUserByEmail(String token, String password, String email){
        jwtUtil.validateToken(token);
        Claims claims = jwtUtil.getClaims(token);
        Optional<UserEntity> user = userRepository.findById(Long.valueOf(claims.get("id").toString()));
        if(user.isPresent()){
            if (!user.get().getEmail().equals((email))){
                return ResponseEntity.badRequest().body("Неверная почта!");
            }
            if (!user.get().getPassword().equals(password)){
                return ResponseEntity.badRequest().body("Неверный пароль!");
            }
            userRepository.deleteById(user.get().getId());
            return ResponseEntity.ok("Успешное удаление");
        }
        return ResponseEntity.badRequest().body("Пользователь не существует!");
    }




    //Валидация пользователя по id
    public Boolean validateUserByToken(String token) {
        Claims claims = jwtUtil.getClaims(token);
        Long id = claims.get("id", Long.class);
        return userRepository.findById(id).isPresent();
    }
}

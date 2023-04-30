package com.codemika.cyberbank.authentication.service;

import com.codemika.cyberbank.authentication.dto.RqCreateUser;
import com.codemika.cyberbank.authentication.dto.RsInfoUser;
import com.codemika.cyberbank.authentication.entity.UserEntity;
import com.codemika.cyberbank.authentication.repository.UserRepository;
import com.codemika.cyberbank.authentication.util.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Сервис для авторизации
 */
 
@Data
@Service
@RequiredArgsConstructor
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
    public ResponseEntity<?> registration(RqCreateUser rq){
        if(!check){
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
                .body("Successful registration! Your token is: " + jwtUtil.generateToken(claims));
    }

    /**
     * Вход пользователя по токену
     *
     * @param token токен
     * @return информация о пользователе
     */
    public ResponseEntity<?> login(String token){
        if(!jwtUtil.validateToken(token)){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Token invalid!");
        }

        Claims claims = jwtUtil.getClaims(token);

        Long id = claims.get("id", Long.class);
        String name = claims.get("name", String.class);
        String surname = claims.get("surname", String.class);
        String patronymic = claims.get("patronymic", String.class);
        String email = claims.get("email", String.class);

        //TODO: Добавить карты, кредиты и т.д.
                String result = String.format("Welcome, %s %s %s!\n" +
                "Your email: %s\n" +
                "ID: %s\n" +
                "Cards: \n" +
                "New generated token: ", surname, name, patronymic, email, id) + jwtUtil.generateToken(claims);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    public ResponseEntity<?> getAllUsers() {
        if (userRepository.findAll().isEmpty())
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("We still have no users... Do u wanna sigh up?😔");

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
                    .body("This user does not exist!");

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
                .body("This user does not exist!");

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
                .body("This user does not exist!");

        UserEntity rq = userRepository.findByPhone(phone).get();
        RsInfoUser rs = new RsInfoUser()
                .setName(rq.getName())
                .setSurname(rq.getSurname())
                .setPatronymic(rq.getPatronymic());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(rs);
    }

    //Валидация пользователя по id
    public boolean validateUserById(Long id){
        return userRepository.findById(id).isPresent();
    }
}

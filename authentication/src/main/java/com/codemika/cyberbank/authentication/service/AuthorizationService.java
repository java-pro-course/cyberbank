package com.codemika.cyberbank.authentication.service;

import com.codemika.cyberbank.authentication.dto.RqCreateUser;
import com.codemika.cyberbank.authentication.dto.RsInfoUser;
import com.codemika.cyberbank.authentication.entity.RoleEntity;
import com.codemika.cyberbank.authentication.entity.RoleUserEntity;
import com.codemika.cyberbank.authentication.entity.UserEntity;
import com.codemika.cyberbank.authentication.repository.RoleRepository;
import com.codemika.cyberbank.authentication.repository.RoleUserRepository;
import com.codemika.cyberbank.authentication.repository.UserRepository;
import com.codemika.cyberbank.authentication.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис для авторизации
 */

@Data
@Service
public class AuthorizationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleUserRepository roleUserRepository;
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

        Optional<RoleEntity> role = roleRepository.findByRole("USER");

        if (!role.isPresent()) {
            return ResponseEntity.badRequest().body("Данная роль не существует");
        }

        userRepository.save(newUser);

        RoleUserEntity roleUser = new RoleUserEntity()
                .setUser(newUser)
                .setRole(role.get());

        roleUserRepository.save(roleUser);

        Claims claims = Jwts.claims();
        claims.put("id", newUser.getId());
        claims.put("name", newUser.getName());
        claims.put("surname", newUser.getSurname());
        claims.put("patronymic", newUser.getPatronymic());
        claims.put("email", newUser.getEmail());
        claims.put("phone", newUser.getPhone());
        claims.put("role", "USER");

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

        if (jwtUtil.getClaims(token).get("role", String.class) == null){
            claims.put("role", "USER");
        }

        String name = claims.get("name", String.class);
        String surname = claims.get("surname", String.class);
        String patronymic = claims.get("patronymic", String.class);
        String email = claims.get("email", String.class);
        String phone = claims.get("phone", String.class);

        //TODO: Добавить карты, кредиты и т.д.
        String result = String.format("Добро пожаловать, %s %s %s!\n" +
                "Ваша эл. почта: %s\n" +
                "Ваш номер телефона: %s\n" +
                //"Ваши карты: \n" +
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
                    .body("У нас ещё нет ни одного пользователя... Хотите стать первым?🥺");

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

    //Валидация пользователя по id
    public Boolean validateUserByToken(String token) {
        Claims claims = jwtUtil.getClaims(token);
        Long id = claims.get("id", Long.class);
        return userRepository.findById(id).isPresent();
    }

    /**
     * Вход пользователя по номеру телефона и паролю
     *
     * @param phone номер телефона
     * @param pass пароль
     * @return Результат входа и, в случае успеха, новый токен
     */
    public ResponseEntity<?> login(String phone, String pass) {
        if (!userRepository.findByPhone(phone).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Пользователь с таким номером телефона не существует!");
        }

        if (!userRepository.findByPhone(phone).get().getPassword().equals(pass)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пароль или номер телефона неверны");
        }

        Optional<UserEntity> tmpUser = userRepository.findByPhone(phone);


        Claims claims = Jwts.claims();
        claims.put("id", tmpUser.get().getId());
        claims.put("name", tmpUser.get().getName());
        claims.put("surname", tmpUser.get().getSurname());
        claims.put("patronymic", tmpUser.get().getPatronymic());
        claims.put("email", tmpUser.get().getEmail());
        claims.put("phone", tmpUser.get().getPhone());


        //TODO: Добавить карты, кредиты и т.д.
        String result = String.format("Добро пожаловать, %s %s %s!\n" +
                "Ваша эл. почта: %s\n" +
                "Ваш номер телефона: %s\n" +
                //"Ваши карты: \n" +
                "Ваш новый токен: ",
                tmpUser.get().getSurname(), tmpUser.get().getName(),
                tmpUser.get().getPatronymic(), tmpUser.get().getEmail(),
                phone) + jwtUtil.generateToken(claims);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}

package com.codemika.cyberbank.authentication.service;

import com.codemika.cyberbank.authentication.dto.RqCreateUser;
import com.codemika.cyberbank.authentication.dto.RsInfoUserPro;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.codemika.cyberbank.authentication.constants.RoleConstants.*;

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
    private final PasswordEncoder passwordEncoder;

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
                .setPassword(
                        passwordEncoder.encode(rq.getPassword()) // encode -> зашифровать
                );

        Optional<RoleEntity> role = roleRepository.findByRole(USER_ROLE);

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
        claims.put(IS_USER_ROLE_EXIST_CLAIMS_KEY, true);
        claims.put(IS_MODER_ROLE_EXIST_CLAIMS_KEY, false);
        claims.put(IS_TESTER_ROLE_EXIST_CLAIMS_KEY, false);
        claims.put(IS_HACKER_ROLE_EXIST_CLAIMS_KEY, false);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Успешная регистрация! Ваш токен для подтверждения личности: " + jwtUtil.generateToken(claims));
    }

    /**
     * Вход пользователя по номеру телефона и паролю
     *
     * @param phone номер телефона
     * @param pass  пароль
     * @return Результат входа и, в случае успеха, новый токен
     */
    public ResponseEntity<?> login(String phone, String pass) {
        Optional<UserEntity> tmpUser = userRepository.findByPhone(phone);
        if (!tmpUser.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Пользователь с таким номером телефона не существует!");
        }

        if (!passwordEncoder.matches(pass, tmpUser.get().getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пароль или номер телефона неверны");
        }

        List<RoleUserEntity> userRoles = roleUserRepository.findAllByUser(tmpUser.get());

        Claims claims = Jwts.claims();
        claims.put("id", tmpUser.get().getId());
        claims.put("name", tmpUser.get().getName());
        claims.put("surname", tmpUser.get().getSurname());
        claims.put("patronymic", tmpUser.get().getPatronymic());
        claims.put("email", tmpUser.get().getEmail());
        claims.put("phone", tmpUser.get().getPhone());

        claims.put(IS_USER_ROLE_EXIST_CLAIMS_KEY, false);
        claims.put(IS_MODER_ROLE_EXIST_CLAIMS_KEY, false);
        claims.put(IS_TESTER_ROLE_EXIST_CLAIMS_KEY, false);
        claims.put(IS_HACKER_ROLE_EXIST_CLAIMS_KEY, false);

        for (RoleUserEntity userRole : userRoles) {
            if (Objects.equals(userRole.getRole().getRole(), "USER")) {
                claims.replace(IS_USER_ROLE_EXIST_CLAIMS_KEY, true);
            } else if (Objects.equals(userRole.getRole().getRole(), "MODER")) {
                claims.replace("is_moder_role", true);
            } else if (Objects.equals(userRole.getRole().getRole(), "TESTER")) {
                claims.replace("is_tester_role", true);
            } else if (Objects.equals(userRole.getRole().getRole(), "HACKER")) {
                claims.replace("is_hacker_role", true);
            }
        }

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

    /**
     * Вход пользователя по токену (Просто смена токена и выдача инфы)
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

        Long id = claims.get("id", Long.class);
        String name = claims.get("name", String.class);
        String surname = claims.get("surname", String.class);
        String patronymic = claims.get("patronymic", String.class);
        String email = claims.get("email", String.class);
        String phone = claims.get("phone", String.class);

        claims.put(IS_USER_ROLE_EXIST_CLAIMS_KEY, false);
        claims.put(IS_MODER_ROLE_EXIST_CLAIMS_KEY, false);
        claims.put(IS_TESTER_ROLE_EXIST_CLAIMS_KEY, false);
        claims.put(IS_HACKER_ROLE_EXIST_CLAIMS_KEY, false);

        List<RoleUserEntity> userRoles = roleUserRepository.findAllByUser(userRepository.findById(id).get());

        for (RoleUserEntity userRole : userRoles) {
            if (Objects.equals(userRole.getRole().getRole(), "USER")) {
                claims.replace(IS_USER_ROLE_EXIST_CLAIMS_KEY, true);
            } else if (Objects.equals(userRole.getRole().getRole(), "MODER")) {
                claims.replace("is_moder_role", true);
            } else if (Objects.equals(userRole.getRole().getRole(), "TESTER")) {
                claims.replace("is_tester_role", true);
            } else if (Objects.equals(userRole.getRole().getRole(), "HACKER")) {
                claims.replace("is_hacker_role", true);
            }
        }
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
        List<UserEntity> users = userRepository.findAll();
        if (users.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("У нас ещё нет ни одного пользователя... Хотите стать первым?🥺");

        List<RsInfoUserPro> infoUsers = new ArrayList<>();
        //Преобразование Entity в Info
        for (UserEntity user : users){
            RsInfoUserPro infoUser = new RsInfoUserPro()
                    .setId(user.getId())
                    .setName(user.getName())
                    .setSurname(user.getSurname())
                    .setPatronymic(user.getPatronymic())
                    .setEmail(user.getEmail())
                    .setPhone(user.getPhone());
            infoUsers.add(infoUser);
        }

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(infoUsers);
    }

    /**
     * Поиск пользователя по id (только для модеров)
     *
     * @param id идентификационный номер пользователя
     * @return искомого пользователя
     */
    public ResponseEntity<?> getUserById(Long id) {
        Optional<UserEntity> user = userRepository.findById(id);
        if (!user.isPresent())
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("Данный пользователь не существует!");

        RsInfoUserPro infoUser = new RsInfoUserPro()
                .setId(user.get().getId())
                .setName(user.get().getName())
                .setSurname(user.get().getSurname())
                .setPatronymic(user.get().getPatronymic())
                .setEmail(user.get().getEmail())
                .setPhone(user.get().getPhone());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(infoUser);
    }

    /**
     * Поиск пользователя по эл. почте
     *
     * @param email эл. почта
     * @return имя, фамилию и отчество требуемого пользователя
     */
    public ResponseEntity<?> getUserByEmail(String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (!user.isPresent())
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("Данный пользователь не существует!");

        RsInfoUserPro infoUser = new RsInfoUserPro()
                .setId(user.get().getId())
                .setName(user.get().getName())
                .setSurname(user.get().getSurname())
                .setPatronymic(user.get().getPatronymic())
                .setEmail(user.get().getEmail())
                .setPhone(user.get().getPhone());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(infoUser);
    }

    /**
     * Поиск пользователя по номеру телефона
     *
     * @param phone номер телефона
     * @return имя, фамилию и отчество требуемого пользователя
     */
    public ResponseEntity<?> getUserByPhone(String phone) {
        Optional<UserEntity> user = userRepository.findByPhone(phone);
        if (!user.isPresent())
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("Данный пользователь не существует!");

        RsInfoUserPro infoUser = new RsInfoUserPro()
                .setId(user.get().getId())
                .setName(user.get().getName())
                .setSurname(user.get().getSurname())
                .setPatronymic(user.get().getPatronymic())
                .setEmail(user.get().getEmail())
                .setPhone(user.get().getPhone());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(infoUser);
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
        return userRepository.existsById(id);
    }


    public ResponseEntity<?> becomeModer(Long idNewModer) {
        Optional<UserEntity> user = userRepository.findById(idNewModer);

        if (!user.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Данный пользователь не существует!");
        }
        List<RoleUserEntity> userRoles = roleUserRepository.findAllByUser(userRepository.findById(idNewModer).get());

        for (RoleUserEntity userRole : userRoles) {
            if (Objects.equals(userRole.getRole().getRole(), MODER_ROLE)) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Данный пользователь уже имеет роль MODER");
            }
        }

        Optional<RoleEntity> roleModer = roleRepository.findByRole(MODER_ROLE);

        if (!roleModer.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Извините, произошла ошибка! Данной роли не существует.");
        }

        RoleUserEntity newRoleUser = new RoleUserEntity()
                .setUser(user.get())
                .setRole(roleModer.get());
        roleUserRepository.save(newRoleUser);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(String.format("Пользователь %s успешно получил роль MODER!", idNewModer));
    }
    public ResponseEntity<?> becomeTester(Long idNewTester) {
        Optional<UserEntity> user = userRepository.findById(idNewTester);
        if (!user.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Данный пользователь не существует!");
        }
        Optional<RoleEntity> roleModer = roleRepository.findByRole(TESTER_ROLE);

        List<RoleUserEntity> userRoles = roleUserRepository.findAllByUser(userRepository.findById(idNewTester).get());

        for (RoleUserEntity userRole : userRoles) {
            if (Objects.equals(userRole.getRole().getRole(), TESTER_ROLE)) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Данный пользователь уже имеет роль TESTER");
            }
        }
        if (!roleModer.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Извините, произошла ошибка! Данной роли не существует.");
        }

        RoleUserEntity newRoleUser = new RoleUserEntity()
                .setUser(user.get())
                .setRole(roleModer.get());
        roleUserRepository.save(newRoleUser);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(String.format("Пользователь %s успешно получил роль TESTER!", idNewTester));
    }
    public ResponseEntity<?> becomeHacker(Long idNewHacker) {
        Optional<UserEntity> user = userRepository.findById(idNewHacker);
        if (!user.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Данный пользователь не существует!");
        }
        Optional<RoleEntity> roleModer = roleRepository.findByRole(HACKER_ROLE);

        List<RoleUserEntity> userRoles = roleUserRepository.findAllByUser(userRepository.findById(idNewHacker).get());

        for (RoleUserEntity userRole : userRoles) {
            if (Objects.equals(userRole.getRole().getRole(), HACKER_ROLE)) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Данный пользователь уже имеет роль HACKER");
            }
        }

        if (!roleModer.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Извините, произошла ошибка! Данной роли не существует.");
        }

        RoleUserEntity newRoleUser = new RoleUserEntity()
                .setUser(user.get())
                .setRole(roleModer.get());
        roleUserRepository.save(newRoleUser);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(String.format("Пользователь %s успешно получил роль HACKER!", idNewHacker));
    }

}

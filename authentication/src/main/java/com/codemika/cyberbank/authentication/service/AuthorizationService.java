package com.codemika.cyberbank.authentication.service;

import com.codemika.cyberbank.authentication.annotation.UserCheck;
import com.codemika.cyberbank.authentication.dto.RqCreateUser;
import com.codemika.cyberbank.authentication.entity.UserEntity;
import com.codemika.cyberbank.authentication.repository.UserRepository;
import com.codemika.cyberbank.authentication.util.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * Регистрация пользователя
     *
     * @param rq
     * @return
     */
    @UserCheck(name = "")
    public ResponseEntity<?> registration(RqCreateUser rq){
        //TODO: Оформить все проверки
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
        claims.put("email", newUser.getPassword());
        claims.put("phone", newUser.getPhone());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("Successful registration! Your token is: " + jwtUtil.generateToken(claims));

    }

    /**
     * Вход пользователя по токену
     *
     * @param token
     * @return
     */
    public ResponseEntity<?> login(String token){
        Claims claims = jwtUtil.getClaims(token);

        String name = claims.get("name", String.class);
        String surname = claims.get("surname", String.class);
        String email = claims.get("email", String.class);
        String phone = claims.get("phone", String.class);

        if(!jwtUtil.validateToken(token)){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Something is wrong!");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(String.format("Welcome %s %s! Your email: %s. Phone number: %s. New generated token: ", surname, name, email, phone)
                        + jwtUtil.generateToken(claims));
    }
}

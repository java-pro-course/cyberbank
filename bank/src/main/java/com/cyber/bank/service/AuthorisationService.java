package com.cyber.bank.service;

import com.cyber.bank.dto.RqCreateUser;
import com.cyber.bank.entity.UserEntity;
import com.cyber.bank.repository.UserRepository;
import com.cyber.bank.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorisationService {
    //TODO: Добавить/исправить различные репозитории и другие классы
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * Регистрация пользователя
     *
     * TODO: исправить вывод и доработать клэймсы
     * @param rq
     * @return
     */
    public ResponseEntity<?> registration(RqCreateUser rq){
        UserEntity newUser = new UserEntity()
                .setName(rq.getName())
                .setSurname(rq.getSurname())
                .setEmail(rq.getEmail())
                .setPassword(rq.getPassword());

        userRepository.save(newUser);

        Claims claims = Jwts.claims();
        claims.put("id", newUser.getId());
        claims.put("name", newUser.getName());
        claims.put("surname", newUser.getSurname());
        claims.put("email", newUser.getPassword());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("Successful registration! Your token is: " + jwtUtil.generateToken(claims));

    }

    /**
     * Вход пользователя по токену
     *
     * TODO: возможно стоит исправить вывод и что-нибудь навесить
     * @param token
     * @return
     */
    public ResponseEntity<?> login(String token){
        Claims claims = jwtUtil.getClaims(token);

        String name = claims.get("name", String.class);
        String surname = claims.get("surname", String.class);
        String email = claims.get("email", String.class);

        if(!jwtUtil.validateToken(token)){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Something is wrong!");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(String.format("Welcome %s %s! Your email: %s. New generated token: ", surname, name, email)
                        + jwtUtil.generateToken(claims));
    }
}

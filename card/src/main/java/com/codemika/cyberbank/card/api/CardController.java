package com.codemika.cyberbank.card.api;

import com.codemika.cyberbank.card.dto.RqCreateCard;
import com.codemika.cyberbank.card.service.CardService;
import com.codemika.cyberbank.card.util.JwtUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/card")
@Data
public class CardController {
    private final CardService service;
    private final JwtUtil jwtUtil;

    /**
     * Оформление(создание) новой карты
     *
     * @param token токен пользователя, который оформляет карту
     * @param rq все данные карты(название, тип(деб/кред), пин-код)
     * @return Созданную карту
     */
    @PostMapping("create")
    public ResponseEntity<?> createCard(@RequestHeader("Authorization") String token, @RequestBody RqCreateCard rq) {
        return service.createCard(token, rq);
    }

    /**
     * Удаление карты
     *
     * @param ownerUserId id владельца
     * @param id id карты
     * @return
     */
    @DeleteMapping("delete")
    public ResponseEntity<?> deleteCard(Long ownerUserId, Long id){
        return ResponseEntity.ok(service.deleteCard(ownerUserId, id));
    }

    /**
     * Просмотр пользователем всех своих карт
     *
     * @param token токен пользователя(чьи карты)
     * @return Все карты
     */
    @GetMapping("get-all-card")
    public ResponseEntity<?> getAllCards(@RequestHeader("Authorization") String token) {

        if (token.isEmpty() || token.trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Token must not be empty!");
        }

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Token is invalid!");
        }

        return service.getAllCards(token);
    }

    //Для тестов
    @GetMapping("get-all-card-for-moder")
    public ResponseEntity<?> getAllCardsModer() {
        return service.getAllCards();
    }
}

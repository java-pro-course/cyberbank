package com.codemika.cyberbank.card.api;

import com.codemika.cyberbank.card.dto.RqCreateCard;
import com.codemika.cyberbank.card.service.CardService;
import com.codemika.cyberbank.card.util.JwtUtil;
import lombok.Data;
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
     * Метод для оформления(создания) новой карты
     * @param token токен пользователя, который оформляет карту
     * @param rq все данные карты(название, тип(деб/кред), пин-код)
     * @return созданную карту
     */
    @PostMapping("create")
    public ResponseEntity<?> createCard(@RequestHeader("Authorization") String token, @RequestBody RqCreateCard rq) {
        return service.createCard(token, rq);
    }
    @DeleteMapping("delete")
    public ResponseEntity<?> deleteCard(Long id,Long ownerUserId){
        return ResponseEntity.ok(service.deleteCard(ownerUserId, id));
    }

    /**
     * Метод для просмотра пользователем всех своих карт
     * @param token токен для определения пользователя(чтобы знать чьи карты показывать)
     * @return все карты определённого пользователя
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
    @PostMapping("money-transfer")
    public ResponseEntity<?> moneyTransfer(String token, String pincode, Long senderId, Long value, Long receivingId) {
        return service.moneyTransfer(token, pincode, senderId, value, receivingId);
    }
    @PostMapping("get-me-money")
    public ResponseEntity<?> getMeMoney(Long cardId, Long value) {
        return service.getMeMoney(cardId, value);
    }
}

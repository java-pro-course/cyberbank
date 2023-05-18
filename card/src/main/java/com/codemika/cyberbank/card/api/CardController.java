package com.codemika.cyberbank.card.api;

import com.codemika.cyberbank.card.dto.RqCreateCard;
import com.codemika.cyberbank.card.service.CardService;
import com.codemika.cyberbank.card.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/card")
@Data
public class CardController {
    private final JwtUtil jwtUtil;
    private final CardService cardService;

    /**
     * Оформление(создание) новой карты
     *
     * @param token токен пользователя, который оформляет карту
     * @param rq все данные карты(название, тип(деб/кред), пин-код)
     * @return созданную карту
     */
    @PostMapping("create")
    public ResponseEntity<?> createCard(@RequestHeader("Authorization") String token,
                                             @RequestBody RqCreateCard rq) throws JsonProcessingException {
        return cardService.createCard(token, rq);
    }

    /**
     * Удаление карты по id
     *
     * @param ownerUserId id владельца
     * @param id id карты
     * @return
     */
    @DeleteMapping("delete-by-id")
    public ResponseEntity<?> deleteCardById(@RequestParam Long ownerUserId, @RequestParam Long id){
        return ResponseEntity.ok(cardService.deleteCard(ownerUserId, id));
    }

    /**
     * Удаление карты по номеру
     *
     * @param ownerUserId id владельца
     * @param accountNumber номер карты
     * @param pincode пин-код карты
     * @return
     */
    @DeleteMapping("delete-by-number")
    public ResponseEntity<?> deleteCardByNumber(@RequestParam Long ownerUserId, @RequestParam String accountNumber,
                                                @RequestParam Short pincode){
        return ResponseEntity.ok(cardService.deleteCard(ownerUserId, accountNumber, pincode));
    }

    /**
     * Просмотр пользователем всех своих карт
     *
     * @param token токен пользователя(чьи карты)
     * @return Все карты
     */
    @GetMapping("get-all-cards")
    public ResponseEntity<?> getAllCards(@RequestHeader("Authorization") String token) {
        if (token.isEmpty() || token.trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Токен не должен быть пустым!");
        }

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Неверный токен!");
        }

        return cardService.getAllCards(token);
    }

    //TODO: Пошаманьте с методом moneyTransfer. Его нужно добавить в сервис
//    @PostMapping("money-transfer")
//    public ResponseEntity<?> moneyTransfer(String token,
//                                           String pincode,
//                                           Long senderId,
//                                           Long value,
//                                           Long receivingId) {
//        return cardService.moneyTransfer(token, pincode, senderId, value, receivingId);
//    }
    //TODO: Тут тоже нужно добавить метод в сервис.
//    @PostMapping("get-my-balance")
//    public ResponseEntity<?> getMyBalance(Long cardId, Long value) {
//        return cardService.getMyBalance(cardId, value);
//    }

    //Для тестов
    @GetMapping("get-all-card-for-moder")
    public ResponseEntity<?> getAllCardsModer() {
        return cardService.getAllCards();
    }
}

package com.codemika.cyberbank.card.api;

import com.codemika.cyberbank.card.dto.RqCreateCreditCard;
import com.codemika.cyberbank.card.dto.RqCreateDebitCard;
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
    private final JwtUtil jwtUtil;
    private final CardService cardService;

    /**
     * Оформление(создание) новой карты
     *
     * @param token токен пользователя, который оформляет карту
     * @param rq    все данные карты(название, тип(деб/кред), пин-код)
     * @return созданную карту
     */
    @PostMapping("create/debit")
    public ResponseEntity<?> createDebit(@RequestHeader("Authorization") String token,//проверка на налы.1
                                             @RequestBody RqCreateDebitCard rq) {
        return cardService.createDebit(token, rq);
    }
    @PostMapping("create/credit")
    public ResponseEntity<?> createCredit(@RequestHeader("Authorization") String token,
                                             @RequestBody RqCreateCreditCard rq) {
        return cardService.createCredit(token, rq);

    }

    /**
     * Изменение названия карты
     * @param id - id карты
     * @param newTitle - новое название карты
     * @return - изменение названия карты
     */
    @PostMapping("change-card-title")
    public ResponseEntity<?> changeCardTitle(Long id, String newTitle){
        return cardService.changeCardTitle(id, newTitle);
    }

    /**
     * Удаление карты
     * @param token токен владельца
     * @param id id карты
     * @return сообщение об успешном/не успешном удалении
     */
    @DeleteMapping("delete")
    public ResponseEntity<?> deleteCard(@RequestHeader("Authorization") String token, Long id){
        return ResponseEntity.ok(cardService.deleteCard(token, id));
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
    /**
     * Метод для перевода денег с карты на карту по id
     * @param token - токен пользователя, переводящего деньги
     * @param pincode - пин-код карты, с которой переводятся деньги
     * @param senderId - id карты, с которой переводятся деньги
     * @param value - сумма перевода (в рублях)
     * @param receivingId - id карты, на которую переводятся деньги
     * @return - перевод средств
     */
    @PostMapping("money-transfer")
    public ResponseEntity<?> moneyTransfer(@RequestHeader("Authorization") String token,
                                           String pincode,
                                           Long senderId,
                                           Long value,
                                           Long receivingId) {
        return cardService.moneyTransfer(token, pincode, senderId, value, receivingId);
    }

    /**
     * Метод для перевода денег с карты на карту по номерам карт
     * @param token - токен пользователя, переводящего деньги
     * @param pincode - пин-код карты, с которой переводятся деньги
     * @param senderAccountNumber - номер карты, с которой переводятся деньги
     * @param value - сумма перевода (в рублях)
     * @param receivingAccountNumber - номер карты, на которую переводятся деньги
     * @return - перевод средств
     */
    @PostMapping("money-transfer")
    public ResponseEntity<?> moneyTransfer(@RequestHeader("Authorization") String token,
                                           String pincode,
                                           String senderAccountNumber,
                                           Long value,
                                           String receivingAccountNumber) {
        return cardService.moneyTransfer(token, pincode, senderAccountNumber, value, receivingAccountNumber);
    }

    //Для тестов
    @GetMapping("get-all-card-for-moder")
    public ResponseEntity<?> getAllCardsModer() {
        return cardService.getAllCards();
    }
    @PostMapping("get-me-money")
    public ResponseEntity<?> getMeMoney(Long cardId, Long value) {
        return cardService.getMeMoney(cardId, value);
    }
}

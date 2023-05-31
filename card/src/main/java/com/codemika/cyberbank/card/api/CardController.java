package com.codemika.cyberbank.card.api;

import com.codemika.cyberbank.card.annotation.CheckRole;
import com.codemika.cyberbank.card.service.CardService;
import com.codemika.cyberbank.card.util.JwtUtil;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Общий контроллер, относящийся к ВСЕМ картам
 */
@RestController
@RequestMapping("api/card")
@Data
public class CardController {
    private final JwtUtil jwtUtil;
    private final CardService cardService;

    /**
     * Просмотр пользователем всех своих карт
     *
     * @param token токен пользователя(чьи карты)
     * @return Все карты
     */
    @CheckRole(isUser = true)
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

    @CheckRole(isUser = true, isModer = true)
    @GetMapping("get-all-card-for-moder")
    public ResponseEntity<?> getAllCardsModer(@RequestHeader("Authorization") String token) {
        return cardService.getAllCards();
    }

    @CheckRole(isUser = true, isTester = true)
    @PostMapping("get-me-money")
    public ResponseEntity<?> getMeMoney(@RequestHeader("Authorization") String token,
                                        @RequestParam Long cardId,
                                        @RequestParam Long value) {
        return cardService.getMeMoney(cardId, value);
    }

    @CheckRole(isUser = true)
    @PutMapping("freeze-card-by-id/{id}")
    public ResponseEntity<?> freezeCard(@RequestHeader("Authorization") String token,
                                        @PathVariable Long id,
                                        @RequestParam String pincode) {
        return cardService.freezeAndUnfreezeCard(token, id, pincode);
    }

    /**
     * Метод для перевода денег с карты на карту по номерам карт
     *
     * @param token                  токен пользователя, переводящего деньги
     * @param pincode                пин-код карты, с которой переводятся деньги
     * @param senderAccountNumber    номер карты, с которой переводятся деньги
     * @param value                  сумма перевода (в рублях)
     * @param receivingAccountNumber номер карты, на которую переводятся деньги
     * @return перевод средств
     */
    @CheckRole(isUser = true)
    @PostMapping("money-transfer")
    public ResponseEntity<?> moneyTransfer(@RequestHeader("Authorization") String token,
                                           @RequestParam String pincode,
                                           @RequestParam String senderAccountNumber,
                                           @RequestParam Long value,
                                           @RequestParam String receivingAccountNumber) {
        return cardService.mainMoneyTransfer(token, pincode, senderAccountNumber, value, receivingAccountNumber);
    }
    /**
     * Метод для перевода денег с карты на карту по номеру телефона
     *
     * @param token                  токен пользователя, переводящего деньги
     * @param pincode                пин-код карты, с которой переводятся деньги
     * @param senderAccountNumber    номер карты, с которой переводятся деньги
     * @param value                  сумма перевода (в рублях)
     * @param recPhone номер телефона, владельца карты, на которую происходит перевод
     * @return перевод средств
     */
    @CheckRole(isUser = true)
    @PostMapping("money-transfer-by-phone")
    public ResponseEntity<?> MoneyTransferByPhone(@RequestHeader("Authorization") String token,
                                           @RequestParam String pincode,
                                           @RequestParam String senderAccountNumber,
                                           @RequestParam Long value,
                                           @RequestParam String recPhone) {
        return cardService.mainMoneyTransferByPhone(token, pincode, senderAccountNumber, value, recPhone);
    }

}

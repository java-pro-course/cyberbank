package com.codemika.cyberbank.card.api;

import com.codemika.cyberbank.card.annotation.CheckRole;
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
    @CheckRole(isUser = true)
    @PostMapping("create-debit")
    public ResponseEntity<?> createDebit(@RequestHeader("Authorization") String token,
                                         @RequestBody RqCreateDebitCard rq) {
        return cardService.createDebit(token, rq);
    }

    @CheckRole(isUser = true)
    @PostMapping("create-credit")
    public ResponseEntity<?> createCredit(@RequestHeader("Authorization") String token,
                                          @RequestBody RqCreateCreditCard rq) {
        return cardService.createCredit(token, rq);

    }
    /**
     * Изменение названия карты по id карты
     * @param id id карты
     * @param newTitle новое название карты
     * @return изменение названия карты
     */
//    @CheckRole(isUser = true)
//    @PostMapping("change-card-title-by-id")
//    public ResponseEntity<?> changeCardTitle(@RequestHeader("Authorization") String token,
//                                             @RequestParam Long id,
//                                             @RequestParam String newTitle){
//        return cardService.changeCardTitle(token, id, newTitle);
//    }

    ///**
    // * Изменение названия дебетовой карты по id карты
    // * @param id id карты
    // * @param newTitle новое название карты
    // * @return изменение названия карты
    // */
    //   @PostMapping("change-debit-card-title-by-id")
    //   public ResponseEntity<?> changeCardTitle(@RequestHeader("Authorization") String token, Long id, String newTitle){
    //       return cardService.changeCardTitle(token, id, newTitle);
    //   }

    ///**
    // * Изменение названия кредитной карты по id карты
    // * @param id id карты
    // * @param newTitle новое название карты
    // * @return изменение названия карты
    // */
    //  @PostMapping("change-credit-card-title-by-id")
    //   public ResponseEntity<?> changeCardTitle(@RequestHeader("Authorization") String token, Long id, String newTitle){
    //       return cardService.changeCardTitle(token, id, newTitle);
    //   }

    /**
     * Изменение названия карты по номеру карты
     * @param accountNumber номер карты
     * @param newTitle новое название карты
     * @return изменение названия карты
     */
//    @CheckRole(isUser = true)
//    @PostMapping("change-card-title-by-account-number")
//    public ResponseEntity<?> changeCardTitle(@RequestHeader("Authorization") String token,
//                                             @RequestParam String accountNumber,
//                                             @RequestParam String newTitle){
//        return cardService.changeCardTitle(token, accountNumber, newTitle);
//    }

    ///**
    // * Изменение названия дебетовой карты по номеру карты
    // * @param accountNumber номер карты
    // * @param newTitle новое название карты
    // * @return изменение названия карты
    // */
    //@PostMapping("change-debit-card-title-by-account-number")
    //public ResponseEntity<?> changeCardTitle(@RequestHeader("Authorization") String token, String accountNumber, String newTitle){
    //    return cardService.changeCardTitle(token, accountNumber, newTitle);
    //}

    ///**
    // * Изменение названия кредитной карты по номеру карты
    // * @param accountNumber номер карты
    // * @param newTitle новое название карты
    // * @return изменение названия карты
    // */
    //@PostMapping("change-credit-card-title-by-account-number")
    //public ResponseEntity<?> changeCardTitle(@RequestHeader("Authorization") String token, String accountNumber, String newTitle){
    //    return cardService.changeCardTitle(token, accountNumber, newTitle);
    //}

    /**
     * Изменение пин-кода карты по id карты
     * @param id id карты
     * @param newPinCode новый пин-код карты
     * @return изменение пин-кода карты
     */
//    @CheckRole(isUser = true)
//    @PostMapping("change-card-pincode-by-id")
//    public ResponseEntity<?> changePinCode(@RequestHeader("Authorization") String token,
//                                           @RequestParam Long id,
//                                           @RequestParam String pincode,
//                                           @RequestParam String newPinCode){
//        return cardService.changeCardPinCode(token, id, pincode, newPinCode);
//    }

    ///**
    // * Изменение пин-кода дебетовой карты по id карты
    // * @param id id карты
    // * @param newPinCode новый пин-код карты
    // * @return изменение пин-кода карты
    // */
    //@PostMapping("change-debit-card-pincode-by-id")
    //public ResponseEntity<?> changePinCode(@RequestHeader("Authorization") String token, Long id, String pincode, String newPinCode){
    //    return cardService.changeCardPinCode(token, id, pincode, newPinCode);
    //}

    ///**
    // * Изменение пин-кода кредитной карты по id карты
    // * @param id id карты
    // * @param newPinCode новый пин-код карты
    // * @return изменение пин-кода карты
    // */
    //@PostMapping("change-credit-card-pincode-by-id")
    //public ResponseEntity<?> changePinCode(@RequestHeader("Authorization") String token, Long id, String pincode, String newPinCode){
    //    return cardService.changeCardPinCode(token, id, pincode, newPinCode);
    //}

    /**
     * Изменение пин-кода карты по номеру карты
     * @param accountNumber номер карты
     * @param newPinCode новый пин-код карты
     * @return изменение карты
     */
//    @CheckRole(isUser = true)
//    @PostMapping("change-card-pincode-by-account-number")
//    public ResponseEntity<?> changePinCode(@RequestHeader("Authorization") String token,
//                                           @RequestParam  String accountNumber,
//                                           @RequestParam  String pincode,
//                                           @RequestParam  String newPinCode){
//        return cardService.changeCardPinCode(token, accountNumber, pincode, newPinCode);
//    }

    ///**
    // * Изменение пин-кода дебетовой карты по номеру карты
    // * @param accountNumber номер карты
    // * @param newPinCode новый пин-код карты
    // * @return изменение карты
    // */
    //@PostMapping("change-debit-card-pincode-by-account-number")
    //public ResponseEntity<?> changePinCode(@RequestHeader("Authorization") String token, String accountNumber, String pincode, String newPinCode){
    //    return cardService.changeCardPinCode(token, accountNumber, pincode, newPinCode);
    //}

    ///**
    // * Изменение пин-кода кредитной карты по номеру карты
    // * @param accountNumber номер карты
    // * @param newPinCode новый пин-код карты
    // * @return изменение карты
    // */
    //@PostMapping("change-credit-card-pincode-by-account-number")
    //public ResponseEntity<?> changePinCode(@RequestHeader("Authorization") String token, String accountNumber, String pincode, String newPinCode){
    //    return cardService.changeCardPinCode(token, accountNumber, pincode, newPinCode);
    //}

    /**
     * Удаление дебетовой карты
     *
     * @param token токен владельца
     * @param id    id карты
     * @return сообщение об успешном/не успешном удалении
     */
    @CheckRole(isUser = true)
    @DeleteMapping("delete-debit-card")
    public ResponseEntity<?> deleteDebitCard(@RequestHeader("Authorization") String token, Long id) {
        return ResponseEntity.ok(cardService.deleteDebitCard(token, id));
    }

    /**
     * Удаление кредитной карты
     *
     * @param token токен владельца
     * @param id    id карты
     * @return сообщение об успешном/не успешном удалении
     */
    @CheckRole(isUser = true)
    @DeleteMapping("delete-credit-card")
    public ResponseEntity<?> deleteCreditCard(@RequestHeader("Authorization") String token, Long id) {
        return ResponseEntity.ok(cardService.deleteCreditCard(token, id));
    }

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

//    /**
//     * Метод для перевода денег с карты на карту по id
//     * @param token токен пользователя, переводящего деньги
//     * @param pincode пин-код карты, с которой переводятся деньги
//     * @param senderId id карты, с которой переводятся деньги
//     * @param value сумма перевода (в рублях)
//     * @param receivingId id карты, на которую переводятся деньги
//     * @return перевод средств
//     */
//    @CheckRole(isUser = true)
//    @PostMapping("money-transfer-by-id")
//    public ResponseEntity<?> moneyTransfer(@RequestHeader("Authorization") String token,
//                                           @RequestParam String pincode,
//                                           @RequestParam Long senderId,
//                                           @RequestParam Long value,
//                                           @RequestParam Long receivingId) {
//        return cardService.moneyTransfer(token, pincode, senderId, value, receivingId);
//    }

    ///**
    // * Метод для перевода денег с карты на карту по id (дебет.)
    // * @param token - токен пользователя, переводящего деньги
    // * @param pincode - пин-код карты, с которой переводятся деньги
    // * @param senderId - id карты, с которой переводятся деньги
    // * @param value - сумма перевода (в рублях)
    // * @param receivingId - id карты, на которую переводятся деньги
    // * @return - перевод средств
    // */
    //@PostMapping("debit-money-transfer-by-id")
    //public ResponseEntity<?> moneyTransfer(@RequestHeader("Authorization") String token,
    //                                       String pincode,
    //                                       Long senderId,
    //                                       Long value,
    //                                       Long receivingId) {
    //    return cardService.moneyTransfer(token, pincode, senderId, value, receivingId);
    //}

    ///**
    // * Метод для перевода денег с карты на карту по id (кредит.)
    // * @param token - токен пользователя, переводящего деньги
    // * @param pincode - пин-код карты, с которой переводятся деньги
    // * @param senderId - id карты, с которой переводятся деньги
    // * @param value - сумма перевода (в рублях)
    // * @param receivingId - id карты, на которую переводятся деньги
    // * @return - перевод средств
    // */
    //@PostMapping("credit-money-transfer-by-id")
    //public ResponseEntity<?> moneyTransfer(@RequestHeader("Authorization") String token,
    //                                       String pincode,
    //                                       Long senderId,
    //                                       Long value,
    //                                       Long receivingId) {
    //    return cardService.moneyTransfer(token, pincode, senderId, value, receivingId);
    //}

//    /**
//     * Метод для перевода денег с карты на карту по номерам карт
//     * @param token - токен пользователя, переводящего деньги
//     * @param pincode - пин-код карты, с которой переводятся деньги
//     * @param senderAccountNumber - номер карты, с которой переводятся деньги
//     * @param value - сумма перевода (в рублях)
//     * @param receivingAccountNumber - номер карты, на которую переводятся деньги
//     * @return - перевод средств
//     */
//    @PostMapping("money-transfer-by-account-number")
//    public ResponseEntity<?> moneyTransfer(@RequestHeader("Authorization") String token,
//                                           @RequestParam String pincode,
//                                           @RequestParam String senderAccountNumber,
//                                           @RequestParam Long value,
//                                           @RequestParam String receivingAccountNumber) {
//        return cardService.moneyTransfer(token, pincode, senderAccountNumber, value, receivingAccountNumber);
//    }

    // /**
    //  * Метод для перевода денег с карты на карту по номерам карт (дебет.)
    //  * @param token - токен пользователя, переводящего деньги
    //  * @param pincode - пин-код карты, с которой переводятся деньги
    //  * @param senderAccountNumber - номер карты, с которой переводятся деньги
    //  * @param value - сумма перевода (в рублях)
    //  * @param receivingAccountNumber - номер карты, на которую переводятся деньги
    // * @return - перевод средств
    //  */
    //@PostMapping("debit-money-transfer-by-account-number")
    //public ResponseEntity<?> moneyTransfer(@RequestHeader("Authorization") String token,
    //                                       String pincode,
    //                                       String senderAccountNumber,
    //                                       Long value,
    //                                       String receivingAccountNumber) {
    //    return cardService.moneyTransfer(token, pincode, senderAccountNumber, value, receivingAccountNumber);
    //}

    ///**
    // * Метод для перевода денег с карты на карту по номерам карт (кредит.)
    // * @param token - токен пользователя, переводящего деньги
    // * @param pincode - пин-код карты, с которой переводятся деньги
    // * @param senderAccountNumber - номер карты, с которой переводятся деньги
    // * @param value - сумма перевода (в рублях)
    // * @param receivingAccountNumber - номер карты, на которую переводятся деньги
    // * @return - перевод средств
    // */
    //@PostMapping("credit-money-transfer-by-account-number")
    //public ResponseEntity<?> moneyTransfer(@RequestHeader("Authorization") String token,
    //                                       String pincode,
    //                                       String senderAccountNumber,
    //                                       Long value,
    //                                       String receivingAccountNumber) {
    //    return cardService.moneyTransfer(token, pincode, senderAccountNumber, value, receivingAccountNumber);
    //}

    //Для тестов
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

    @PutMapping("freeze-card-by-id/{id}")
    public ResponseEntity<?> freezeCard(@RequestHeader("Authorization") String token,
                                        @PathVariable Long id) {
        return cardService.FreezeAndUnfreezeCard(token, id);
    }
}

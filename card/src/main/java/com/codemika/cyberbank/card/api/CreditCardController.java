package com.codemika.cyberbank.card.api;

import com.codemika.cyberbank.card.annotation.CheckRole;
import com.codemika.cyberbank.card.dto.RqCreateCreditCard;
import com.codemika.cyberbank.card.service.CardService;
import com.codemika.cyberbank.card.service.CreditCardService;
import com.codemika.cyberbank.card.util.JwtUtil;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер, относящийся ТОЛЬКО к кредитным картам
 */
@RestController
@RequestMapping("api/card/credit")
@Data
public class CreditCardController {
    private final JwtUtil jwtUtil;
    private final CardService service;
    private final CreditCardService creditService;

    /**
     * Оформление(создание) новой карты
     *
     * @param token токен пользователя
     * @param rq    все данные для карты
     * @return новая карта
     */
    @CheckRole(isUser = true)
    @PostMapping("create")
    public ResponseEntity<?> createCredit(@RequestHeader("Authorization") String token,
                                          @RequestBody RqCreateCreditCard rq) {
        return creditService.create(token, rq);
    }

    /**
     * Удаление кредитной карты
     *
     * @param token токен владельца
     * @param id    id карты
     * @return сообщение об успешном/не успешном удалении
     */
    @CheckRole(isUser = true)
    @DeleteMapping("delete-by-id")
    public ResponseEntity<?> deleteCreditCard(@RequestHeader("Authorization") String token,
                                              @RequestParam Long id,
                                              @RequestParam String pincode) {
        return creditService.delete(token, id, pincode);
    }

    /**
     * Удаление карты по номеру телефона
     *
     * @param token         токен владельца
     * @param accountNumber номер карты
     * @param pincode       пин-код карты(проверяется)
     * @return сообщение об успешном/не успешном удалении
     */
    @CheckRole(isUser = true)
    @DeleteMapping("delete-by-number")
    public ResponseEntity<?> deleteCardByNumber(@RequestHeader("Authorization") String token,
                                                @RequestParam String accountNumber,
                                                @RequestParam String pincode) {
        return creditService.delete(token, accountNumber, pincode);
    }

    /**
     * Изменение названия карты по id карты
     *
     * @param token    токен владельца карты
     * @param id       id карты
     * @param newTitle новое название карты
     * @return изменение названия карты
     */
    @CheckRole(isUser = true)
    @PostMapping("change-title-by-id")
    public ResponseEntity<?> changeCardTitle(@RequestHeader("Authorization") String token,
                                             @RequestParam Long id,
                                             @RequestParam String newTitle) {
        return null;//cardService.changeCardTitle(token, id, newTitle);
    }

    /**
     * Изменение названия кредитной карты по номеру карты
     *
     * @param token         токен владельца
     * @param accountNumber номер карты
     * @param newTitle      новое название карты
     * @return изменение названия карты
     */
    @CheckRole(isUser = true)
    @PostMapping("change-title-by-account-number")
    public ResponseEntity<?> changeCardTitle(@RequestHeader("Authorization") String token,
                                             @RequestParam String accountNumber,
                                             @RequestParam String newTitle) {
        return null; //cardService.changeCardTitle(token, accountNumber, newTitle);
    }

    /**
     * Изменение пин-кода карты по её id
     *
     * @param token      токен владельца
     * @param id         id карты
     * @param pincode    старый пин-код карты
     * @param newPinCode новый пин-код карты
     * @return изменение пин-кода
     */
    @CheckRole(isUser = true)
    @PostMapping("change-pincode-by-id")
    public ResponseEntity<?> changePinCode(@RequestHeader("Authorization") String token,
                                           @RequestParam Long id,
                                           @RequestParam String pincode,
                                           @RequestParam String newPinCode) {
        return null; //cardService.changeCardPinCode(token, id, pincode, newPinCode);
    }

    /**
     * Изменение пин-кода кредитной карты по номеру карты
     *
     * @param token         токен владельца
     * @param accountNumber номер карты
     * @param newPinCode    новый пин-код карты
     * @return изменение карты
     */
    @PostMapping("change-pincode-by-account-number")
    public ResponseEntity<?> changePinCode(@RequestHeader("Authorization") String token,
                                           @RequestParam String accountNumber,
                                           @RequestParam String pincode,
                                           @RequestParam String newPinCode) {
        return null; //cardService.changeCardPinCode(token, accountNumber, pincode, newPinCode);
    }
}

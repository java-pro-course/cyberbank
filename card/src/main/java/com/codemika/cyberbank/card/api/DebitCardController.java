package com.codemika.cyberbank.card.api;

import com.codemika.cyberbank.card.annotation.CheckRole;
import com.codemika.cyberbank.card.dto.RqCreateDebitCard;
import com.codemika.cyberbank.card.service.CardService;
import com.codemika.cyberbank.card.service.DebitCardService;
import com.codemika.cyberbank.card.util.JwtUtil;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер, относящийся ТОЛЬКО к дебетовым картам
 */
@RestController
@RequestMapping("api/card/debit")
@Data
public class DebitCardController {
    private final JwtUtil jwtUtil;
    private final CardService service;
    private final DebitCardService debitService;

    /**
     * Оформление(создание) новой карты
     *
     * @param token токен пользователя, который оформляет карту
     * @param rq    все данные карты(название, тип(деб/кред), пин-код)
     * @return созданную карту
     */
    @CheckRole(isUser = true)
    @PostMapping("create")
    public ResponseEntity<?> createDebit(@RequestHeader("Authorization") String token,
                                         @RequestBody RqCreateDebitCard rq) {
        return debitService.create(token, rq);
    }

    /**
     * Удаление карты
     *
     * @param token токен владельца
     * @param id    id карты
     * @return сообщение об успешном/не успешном удалении
     */
    @CheckRole(isUser = true)
    @DeleteMapping("delete")
    public ResponseEntity<?> deleteDebitCard(@RequestHeader("Authorization") String token, Long id) {
        return debitService.delete(token, id);
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
    public ResponseEntity<?> changeDebitCardTitle(@RequestHeader("Authorization") String token,
                                                  @RequestParam Long id,
                                                  @RequestParam String newTitle) {
        return null;//cardService.changeCardTitle(token, id, newTitle);
    }

    /**
     * Изменение названия дебетовой карты по номеру карты
     *
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
     * @param pincode    старый пин-код
     * @param newPinCode новый пин-код
     * @return изменение пин-кода
     */
    @PostMapping("change-pincode-by-id")
    public ResponseEntity<?> changePinCode(@RequestHeader("Authorization") String token,
                                           @RequestParam Long id,
                                           @RequestParam String pincode,
                                           @RequestParam String newPinCode) {
        return null; //cardService.changeCardPinCode(token, id, pincode, newPinCode);
    }
    /**
     * Изменение пин-кода карты по номеру карты
     * @param token токен владельца
     * @param accountNumber номер карты
     * @param newPinCode новый пин-код карты
     * @return изменение карты
     */
    @PostMapping("change-pincode-by-account-number")
    public ResponseEntity<?> changePinCode(@RequestHeader("Authorization") String token,
                                           @RequestParam String accountNumber,
                                           @RequestParam String pincode,
                                           @RequestParam String newPinCode){
        return null; //cardService.changeCardPinCode(token, accountNumber, pincode, newPinCode);
    }
}

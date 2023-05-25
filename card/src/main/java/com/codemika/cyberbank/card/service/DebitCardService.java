package com.codemika.cyberbank.card.service;

import com.codemika.cyberbank.card.dto.RqCreateDebitCard;
import com.codemika.cyberbank.card.entity.DebitCardEntity;
import com.codemika.cyberbank.card.repository.DebitCardRepository;
import com.codemika.cyberbank.card.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static com.codemika.cyberbank.card.service.CardService.generateAccountNumber;

@Service
@Slf4j
@RequiredArgsConstructor
public class DebitCardService {
    private final DebitCardRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:8081/api/auth/validate-user/?token=";
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * Создание дебетовой карты
     *
     * @param token пользователя(будущего владельца)
     * @param rq    параметры карты
     * @return Созданную карту
     */
    public ResponseEntity<?> create(String token, RqCreateDebitCard rq) {
        if (rq.getTitle().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вы не указали название карты");
        }
        if (rq.getPincode().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вы не указали пин-код");
        }
        if (!rq.getPincode().toLowerCase().matches("[0-9]+"))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пин-код должен состоять из цифр! Например: 3856");

        if (rq.getPincode().trim().length() != 4)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Длина пин-кода должна быть 4 цифры!!! Например: 3856");

        if (rq.getPincode().equals("1234") || rq.getPincode().equals("2580")
                || rq.getPincode().equals("0000") || rq.getPincode().equals("4321")
                || rq.getPincode().equals("9999") || rq.getPincode().equals("6666")
                || rq.getPincode().equals("1111") || rq.getPincode().equals("8520")
                || rq.getPincode().equals("5678") || rq.getPincode().equals("0852")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Извините, но мы запретили некоторые излишне простые пин-коды для вашей безопасности.");
        }
        //Достаём id из токена
        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);

        String typeNewCard = "Дебетовая";
        //Подготавливаем результат
        DebitCardEntity card = new DebitCardEntity()
                .setTitle(rq.getTitle())
                .setType(typeNewCard)
                .setOwnerUserId(ownerUserId)
                .setBalance(0L)
                .setPincode(
                        passwordEncoder.encode(rq.getPincode())
                )
                .setAccountNumber(
                        generateAccountNumber(16)
                );

        if (repository.findAllByAccountNumber(card.getAccountNumber()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Такая карта уже существует!");
        }

        ResponseEntity<Boolean> response = restTemplate.getForEntity(url + token, Boolean.class);

        //Проверка валидности пользователя
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.info(card.toString());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Такого пользователя не существует!");
        }

        card = repository.save(card);

        return ResponseEntity.ok(card);
    }

    /**
     * Удаление карты
     *
     * @param token токен-владельца
     * @param id    id карты
     * @return Результат удаления
     */
    public ResponseEntity<?> delete(String token, Long id, String pincode) {
        Optional<DebitCardEntity> card = repository.findById(id);

        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);

        if (!card.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Карта с  ID: " + id + " не существует");
        }
        if (!card.get().getOwnerUserId().equals(ownerUserId)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Вы не можете удалить чужую карту!");
        }
        if (!passwordEncoder.matches(card.get().getPincode(), pincode))
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Неверный пин-код!");
        if (card.get().getBalance() != 0) {
            return ResponseEntity
                    .badRequest()
                    .body("Вы не можете удалить карту, на которой есть деньги! " +
                            "Пожалуйста, снимите их или переведите на другую карту!");
        }

        repository.deleteById(id);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("Карта была успешно удалена!");
    }

    public ResponseEntity<?> delete(String token, String accountNumber, String pincode) {
        Optional<DebitCardEntity> card = repository.findCardByAccountNumber(accountNumber);

        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);

        if (!card.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Карты с номером: " + accountNumber + " не существует!");
        }

        if (!card.get().getOwnerUserId().equals(ownerUserId)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Вы не являетесь владельцем данной карты!");
        }

        if (!passwordEncoder.matches(card.get().getPincode(), pincode)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Неверный пин-код!");
        }

        if (card.get().getBalance() != 0) {
            return ResponseEntity
                    .badRequest()
                    .body("Вы не можете удалить карту на которой есть деньги!" +
                            "Пожалуйста, снимите их или переведите на другую карту");
        }

        repository.deleteByAccountNumber(accountNumber);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("Карта была успешно удалена");
    }

    /**
     * Изменение пин-кода карты по номеру карты
     *
     * @param token         токен пользователя
     * @param accountNumber номер карты
     * @param pincode       старый пин-код
     * @param newPinCode    новый пин-код карты
     * @return сообщение об изменении пин-кода карты
     */
    public ResponseEntity<?> changePincode(String token, String accountNumber, String pincode, String newPinCode) {
        Optional<DebitCardEntity> card = repository.findCardByAccountNumber(accountNumber);

        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);

        if (!card.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Карты с таким номером не существует!");
        }
        if (!card.get().getOwnerUserId().equals(ownerUserId)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Нельзя менять данные чужой карты!");
        }
        if (!passwordEncoder.matches(card.get().getPincode(), pincode)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пин-код неверный!");
        }

        repository.updateCardPinCode(newPinCode, card.get().getId());
        return ResponseEntity.ok("Пин-код карты изменен.");
    }

    /**
     * Изменение пин-кода по id карты
     *
     * @param token      токен пользователя
     * @param id         id карты
     * @param pincode    старый пин-код
     * @param newPinCode новый пин-код карты
     * @return сообщение об изменении пин-кода карты
     */
    public ResponseEntity<?> changePincode(String token, Long id, String pincode, String newPinCode) {
        Optional<DebitCardEntity> card = repository.findById(id);

        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);

        if (!card.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Карты с таким id не существует!");
        }
        if (!card.get().getOwnerUserId().equals(ownerUserId)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Нельзя менять данные чужой карты!");
        }
        if (!passwordEncoder.matches(card.get().getPincode(), pincode)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пин-код неверный!");
        }

        repository.updateCardPinCode(newPinCode, card.get().getId());
        return ResponseEntity.ok("Пин-код карты изменен.");
    }

    /**
     * Изменение названия карты по номеру карты
     *
     * @param token         токен пользователя
     * @param accountNumber номер карты
     * @param newTitle      новое название карты
     * @return сообщение об изменении названия карты
     */
    public ResponseEntity<?> changeTitle(String token, String accountNumber, String newTitle) {
        Optional<DebitCardEntity> card = repository.findCardByAccountNumber(accountNumber);

        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);

        if (!card.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Карты с таким номером не существует!");
        }
        if (!card.get().getOwnerUserId().equals(ownerUserId))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Нельзя менять данные чужой карты!");

        repository.updateCardTitle(newTitle, card.get().getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Название карты изменено.");
    }

    /**
     * Изменение названия карты по id карты
     *
     * @param token    токен пользователя
     * @param id       id карты
     * @param newTitle новое название карты
     * @return сообщение об изменении названия карты
     */
    public ResponseEntity<?> changeTitle(String token, Long id, String newTitle) {
        Optional<DebitCardEntity> card = repository.findById(id);

        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);

        if (!card.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Карты с таким номером не существует!");
        }
        if (!card.get().getOwnerUserId().equals(ownerUserId))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Нельзя менять данные чужой карты!");

        repository.updateCardTitle(newTitle, card.get().getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Название карты изменено.");
    }
}

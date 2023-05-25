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
    public final JwtUtil jwtUtil;

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
                .setPincode(rq.getPincode().trim())
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
    public ResponseEntity<?> delete(String token, Long id) {
        Optional<DebitCardEntity> card = repository.findById(id);

        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);

        if (!card.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("Карта с  ID: " + id + " не существует");
        }
        if (!card.get().getOwnerUserId().equals(ownerUserId)) {
            return ResponseEntity
                    .badRequest()
                    .body("Вы не можете удалить чужую карту!");
        }
        if (card.get().getBalance() != 0) {
            return ResponseEntity
                    .badRequest()
                    .body("Вы не можете удалить карту, на которой есть деньги! " +
                            "Пожалуйста, снимите их или переведите на другую карту!");
        }

        repository.deleteById(id);
        return ResponseEntity
                .ok()
                .body("Карта была успешно удалена!");
    }


}

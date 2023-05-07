package com.codemika.cyberbank.card.service;

import com.codemika.cyberbank.card.dto.RqCreateCard;
import com.codemika.cyberbank.card.entity.CardEntity;
import com.codemika.cyberbank.card.repository.CardRepository;
import com.codemika.cyberbank.card.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardService {
    private final CardRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:8081/api/auth/validate-user/?token=";
    public final JwtUtil jwtUtil;

    /**
     * Создание карты
     *
     * @param token пользователя(будущего владельца)
     * @param rq    параметры карты
     * @return Созданную карту
     */
    public ResponseEntity<?> createCard(String token, RqCreateCard rq) {
        //Проверка на валидный пин-код
        if (!rq.getPincode().toLowerCase().matches("[0-9]+"))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пин-код должен состоять из цифр! Например: 3856");

        if (rq.getPincode().trim().length() != 4)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Длина пин-кода должна быть 4 цифры!!! Например: 3856");

        //Достаём id из токена
        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);

        //Подготавливаем результат
        CardEntity card = new CardEntity()
                .setTitle(rq.getTitle())
                .setType(rq.getType().trim().toLowerCase())
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
                    .body("Такого пользователя не существует! Попробуйте снова.");
        }

        card = repository.save(card);

        return ResponseEntity.ok(card);
    }

    public ResponseEntity<?> changeCardTitle(Long id, String newTitle) {
        Optional<CardEntity> card = repository.findById(id);
        if (!card.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Карты с таким id не существует");
        }
        repository.updateCardTitle(newTitle, card.get().getId());
        return ResponseEntity.ok("Название карты изменено.");
    }

    /**
     * Удаление карты
     *
     * @param ownerUserId id владельца
     * @param id          id карты
     * @return Результат удаления
     */
    public ResponseEntity<?> deleteCard(Long ownerUserId, Long id) {
        Optional<CardEntity> card = repository.findById(id);

        if (!card.isPresent()) {
            return ResponseEntity.badRequest().body("Card with ID: " + id + " isn't present");
        }
        if (!card.get().getOwnerUserId().equals(ownerUserId)) {
            return ResponseEntity.badRequest().body("You cannot delete the card because it does not belong to you");
        }
        if (card.get().getBalance() != 0) {
            return ResponseEntity.badRequest().body("You cannot delete a card with the balance available on it." +
                    "Please cash out at the nearest ATM or transfer money to another card.");
        }

        repository.deleteById(id);
        return ResponseEntity.ok().body("The card has been successfully deleted");
    }

    /**
     * Генерация случайного номера карты
     *
     * @param n размер строки(у нас 16)
     * @return Случайную строку
     */
    private static String generateAccountNumber(int n) {
        String alphabet = "0123456789";

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int index = (int) (alphabet.length() * Math.random());
            result.append(alphabet.charAt(index));
        }

        return result.toString();
    }

    /**
     * Вывод всех карт пользователя
     *
     * @param token уникальный токен авторизации
     * @return Все карты
     */
    public ResponseEntity<?> getAllCards(String token) {
        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long id = claimsParseToken.get("id", Long.class);

        List<CardEntity> cards = repository.findAllByOwnerUserId(id);

        if (cards.isEmpty()) return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("This user have no cards!");

        return ResponseEntity.ok(cards);
    }
    //todo после создания ролей, добавить сюда проверку на содержание токена роли МОДЕР

    /**
     * ТОЛЬКО ДЛЯ МОДЕРОВ
     * Получение ВСЕХ карт в банке
     *
     * @return Все карты банка
     */
    public ResponseEntity<?> getAllCards() {
        List<CardEntity> cards = repository.findAll();

        if (cards.isEmpty()) return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("All users have no cards!");

        return ResponseEntity.ok(cards);
    }

}

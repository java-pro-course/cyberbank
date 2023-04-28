package com.codemika.cyberbank.card.service;

import com.codemika.cyberbank.card.dto.RqCreateDebetCard;
import com.codemika.cyberbank.card.entity.CardEntity;
import com.codemika.cyberbank.card.repository.CardRepository;
import com.codemika.cyberbank.card.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DebetCardService {
    private final CardRepository repository;
    public final JwtUtil jwtUtil;

    /**
     * Согдание дебетовой карты
     * @param token токен владельца
     * @param rq данные для её создания
     * @return новая карта
     */
    public ResponseEntity<?> createDebetCard(String token, RqCreateDebetCard rq) {
        //Проверка на валидный пин-код//todo exception pin-code can be null!
        if (rq.getPincode().isEmpty())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пин-код не должен быть пустым!");
        if (!rq.getPincode().toLowerCase().matches("[0-9]+"))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пин-код должен состоять только из цифр!!! Например, 3856");

        if (rq.getPincode().trim().length() != 4)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пин-код должен состоять из 4-х цифр!!! Например, 3856");

        //Достаём id из токена
        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);

        //Подготавливаем результат
        CardEntity card = new CardEntity()
                .setTitle(rq.getTitle())
                .setType("Дебетовая")
                .setOwnerUserId(ownerUserId) // отправляем сначала запрос в auth и проверяем этот id!
                // для отправки запроса используем RestTemplate!
                .setBalance(0L)
                .setPincode(rq.getPincode().trim())
                .setAccountNumber(
                        generateAccountNumber(16)
                );

        // TODO: проверять id-пользователя из rq на валидность
        card = repository.save(card);
        return ResponseEntity.ok(card);
    }

    /**
     * Удаление дебетовой карты
     * @param ownerUserId id-владельца
     * @param id id-карты
     * @return сообщение
     */
    public ResponseEntity<?> deleteDebetCard(Long ownerUserId, Long id) {
        Optional<CardEntity> card = repository.findById(id);
        if (!card.isPresent()) {
            return ResponseEntity.badRequest().body("Карта с ID: " + id + " не существует");
        }
        if (!card.get().getOwnerUserId().equals(ownerUserId)) {
            return ResponseEntity.badRequest().body("Вы не можете удалить чужую карту");
        }
        if (card.get().getBalance() != 0) {
            return ResponseEntity.badRequest().body("Чтобы удалить карту, переведите все деньги с неё на другую или" +
                    " снимите их в ближайшем банкомате.");
        }
        repository.deleteById(id);
        return ResponseEntity.ok().body("Карта была успешно удаленна!");
    }

    /**
     * Метод для генерации случайного номера карты
     *
     * @param n размер строки (у нас 16)
     * @return случайную строку
     */
    private String generateAccountNumber(int n) {
        String alphabet = "0123456789";

        StringBuilder result = new StringBuilder();
        while (repository.findCardByAccountNumber(String.valueOf(result)).isPresent() || result.equals("")) {
            for (int i = 0; i < n; i++) {
                int index = (int) (alphabet.length() * Math.random());
                result.append(alphabet.charAt(index));
            }
        }

        return result.toString();
    }

    /**
     * Метод для вывода всех карт определённого пользователя.
     *
     * @param token уникальный токен авторизации, содержащий id его пользователя.
     * @return все карты пользователя, чей токен мы получаем.
     */
    public ResponseEntity<?> getAllCards(String token) {
        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long id = claimsParseToken.get("id", Long.class);

        List<CardEntity> cards = repository.findAllByOwnerUserId(id);

        if (cards.isEmpty()) return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Данный пользователь не имеет карт");

        return ResponseEntity.ok(cards);
    }
    //todo после создания ролей, добавить сюда проверку на содержание токена роли МОДЕР

    /**
     * ТОЛЬКО ДЛЯ МОДЕРОВ
     * Метод для получения ВСЕХ карт в банке
     *
     * @return все карты банка
     */
    public ResponseEntity<?> getAllCards() {

        List<CardEntity> cards = repository.findAll();

        if (cards.isEmpty()) return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Ни один пользователь не имеет карт!");

        return ResponseEntity.ok(cards);
    }

    /**
     * Метод для перевода денег с карты на карту
     * @param token токен переводящего деньги
     * @param pincode пин-код карты, с которой переводятся деньги
     * @param id id-карты, с которой переводятся деньги
     * @param value количество переводимых денег (в рублях)
     * @param receivingId id-карты, на которую переводятся деньги
     * @return сообщение об переводе и текущий баланс
     */
    @Transactional
    public ResponseEntity<?> moneyTransfer(String token, String pincode, Long id, Long value, Long receivingId) {
        Optional<CardEntity> card = repository.findById(id);
        Optional<CardEntity> receivingCard = repository.findById(receivingId);

        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);

        if (value == null)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Некорректная сумма перевода");
        if(value <= 0)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вы не можете переводить отрицательные суммы");

        if (!card.isPresent())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Карты с id: " + id + " не существует");

        if (!pincode.equals(repository.findById(id).get().getPincode()))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Неверный пин-код");

        if (!receivingCard.isPresent())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Карты с id: " + receivingId + " не существует");

        if (!card.get().getOwnerUserId().equals(ownerUserId))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пользователь с id: " + ownerUserId + " не обладает картой с id: " + id);

        if (card.get().getBalance() < value)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("На карте недостаточно средств");

        if (id.equals(receivingId))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вы не можете перевести деньги на свою карту");

        receivingCard
                .get()
                .setBalance(receivingCard.get().getBalance() + value);
        card.get()
                .setBalance(card.get().getBalance() - value);

        repository.moneyTransfer(card.get().getBalance(), id);
        repository.moneyTransfer(receivingCard.get().getBalance(), receivingId);

        return ResponseEntity
                .ok("Перевод доставлен! На данный момент ваш баланс " + card.get().getBalance() + " рублей");
    }

    /** Метод только для пользователей с ролями МОДЕР и ТЕСТЕР
     * Создание денег из воздуха
     * @param cardId карта, на которую зачисляются деньги
     * @param value количество денег
     * @return сообщение
     */
    @Transactional
    public ResponseEntity<?> getMeMoney(Long cardId,  Long value) {
        Optional<CardEntity> card = repository.findById(cardId);
        if(value <= 0)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вы не можете переводить отрицательные суммы");

        if (!card.isPresent())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Карты с id: " + cardId + " не существует");

        card.get()
                .setBalance(card.get().getBalance() + value);

        repository.moneyTransfer(card.get().getBalance(), cardId);

        return ResponseEntity
                .ok("Вы успешно получили " + value + " рублей");
    }
}

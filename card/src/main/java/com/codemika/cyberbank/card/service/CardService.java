package com.codemika.cyberbank.card.service;

import com.codemika.cyberbank.card.dto.RqCreateCreditCard;
import com.codemika.cyberbank.card.dto.RqCreateDebitCard;
import com.codemika.cyberbank.card.entity.CardEntity;
import com.codemika.cyberbank.card.repository.CardRepository;
import com.codemika.cyberbank.card.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
     * Создание дебетовой карты
     *
     * @param token пользователя(будущего владельца)
     * @param rq    параметры карты
     * @return Созданную карту
     */
    public ResponseEntity<?> createDebit(String token, RqCreateDebitCard rq) {
        if (rq.getTitle().isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вы не указали название карты");
        }
        if (rq.getPincode().isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вы не указали пин-код");
        }
        if(!rq.getPincode().toLowerCase().matches("[0-9]+"))
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
                || rq.getPincode().equals("5678") || rq.getPincode().equals("0852")){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Извините, но мы запретили некоторые излишне простые пин-коды для вашей безопасности.");
        }
        //Достаём id из токена
        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);
        String typeNewCard = "Дебетовая";
        //Подготавливаем результат
        CardEntity card = new CardEntity()
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
     * Создание кредитной карты
     *
     * @param token пользователя(будущего владельца)
     * @param rq параметры карты
     * @return Созданную карту
     */
    public ResponseEntity<?> createCredit(String token, RqCreateCreditCard rq) {
        int maxValue = (int) ((rq.getMonthlyIncome() * rq.getCreditTerm() * 0.5) / (1 + (0.15 * rq.getCreditTerm()))); // хз насколько я правильно эту формулу вписал.

        if (rq.getValue() > maxValue) {
            return ResponseEntity
                    .badRequest()
                    .body("Учитывая ваши данные, мы не можем выдать вам кредит на сумму: " + rq.getValue());
        }
        if (rq.getTitle().isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вы не указали название карты");
        }
        if (rq.getPincode().isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вы не указали пин-код");
        }
        //Проверка на валидный пин-код
        if(!rq.getPincode().toLowerCase().matches("[0-9]+"))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пин-код должен состоять из цифр! Например: 3856");
        if(rq.getPincode().trim().length() != 4)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Длина пин-кода должна быть 4 цифры!!! Например: 3856");
        if (rq.getPincode().equals("1234") || rq.getPincode().equals("2580")
                || rq.getPincode().equals("0000") || rq.getPincode().equals("4321")
                || rq.getPincode().equals("9999") || rq.getPincode().equals("6666")
                || rq.getPincode().equals("1111") || rq.getPincode().equals("8520")
                || rq.getPincode().equals("5678") || rq.getPincode().equals("0852")){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Извините, но мы запретили некоторые излишне простые пин-коды для вашей безопасности.");
        }
        //Достаём id из токена
        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);
        String typeNewCard = "Кредитная";
        //Подготавливаем результат
        CardEntity card = new CardEntity()
                .setTitle(rq.getTitle())
                .setType(typeNewCard)
                .setOwnerUserId(ownerUserId)
                .setBalance(rq.getValue())
                .setPincode(rq.getPincode().trim())
                .setAccountNumber(
                        generateAccountNumber(16)
                );

        if(repository.findAllByAccountNumber(card.getAccountNumber()).isPresent()){
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

        if (!pincode.equals(card.get().getPincode()))
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
     /**
     * Изменение названия карты
     * @param id - id карты
     * @param newTitle - новое название карты
     * @return - сообщение об изменении названия карты
     */
    public ResponseEntity<?> changeCardTitle(Long id, String newTitle) {
        Optional<CardEntity> card = repository.findById(id);
        if (!card.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Карты с таким id не существует!");
        }
        repository.updateCardTitle(newTitle, card.get().getId());
        return ResponseEntity.ok("Название карты изменено.");
    }

    /**
     * Удаление карты
     *
     * @param token токен-владельца
     * @param id id карты
     * @return Результат удаления
     */
    public ResponseEntity<?> deleteCard(String token, Long id){
        Optional<CardEntity> card = repository.findById(id);

        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);

        if(!card.isPresent()){
            return ResponseEntity.badRequest().body("Карта с  ID: " + id + " не существует");
        }
        if (!card.get().getOwnerUserId().equals(ownerUserId)){
            return ResponseEntity.badRequest().body("Вы не можете удалить чужую карту!");
        }
        if(card.get().getBalance() != 0){
            return ResponseEntity.badRequest().body("Вы не можете удалить карту, на которой есть деньги! " +
                    "Пожалуйста, снимите их или переведите на другую карту!");
        }

        repository.deleteById(id);
        return ResponseEntity.ok().body("Карта была успешно удалена!");
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
                .body("У вас нет карт!");

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

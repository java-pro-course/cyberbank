package com.codemika.cyberbank.card.service;

import com.codemika.cyberbank.card.dto.RqCreateCard;
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
     * @param rq параметры карты
     * @return Созданную карту
     */
    public ResponseEntity<?> createCard(String token, RqCreateCard rq) {
        //Проверка на валидный пин-код
        if(!rq.getPincode().toLowerCase().matches("[0-9]+"))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Пин-код должен состоять из цифр! Например: 3856");

        if(rq.getPincode().trim().length() != 4)
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
                .setPincode(Short.valueOf(rq.getPincode().trim()))
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
                    .body("Такого пользователя не существует! Попробуйте снова.");
        }

        card = repository.save(card);

        return ResponseEntity.ok(card);
    }

    /**
     * Создание дебетовой карты
     *
     * @param token пользователя(будущего владельца)
     * @param rq    параметры карты
     * @return Созданную карту
     */
    public ResponseEntity<?> createCard(String token, RqCreateDebitCard rq) {
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
                .setPincode(Short.valueOf(rq.getPincode().trim()))
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
    public ResponseEntity<?> createCard(String token, RqCreateCreditCard rq) {
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
                .setPincode(Short.valueOf(rq.getPincode().trim()))
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
     * Удаление карты 1
     *
     * @param ownerUserId id владельца
     * @param id id карты
     * @return Результат удаления
     */
    public ResponseEntity<?> deleteCard(Long ownerUserId, Long id){
        Optional<CardEntity> card = repository.findById(id);

        if(!card.isPresent()){
            return ResponseEntity.badRequest().body("Карты с id " + id + " не существует!");
        }
        if (!card.get().getOwnerUserId().equals(ownerUserId)){
            return ResponseEntity.badRequest().body("Вы не можете удалить карту, потому что она не принадлежит вам!");
        }
        if(card.get().getBalance() != 0){
            return ResponseEntity.badRequest().body("Пожалуйста, выведите деньги с карты перед её удалением!");
        }

        repository.deleteById(id);
        return ResponseEntity.ok().body("Вы успешно удалили карту!");
    }

    /**
     * Удаление карты через номер и пин-код
     *
     * @param ownerUserId id владельца
     * @param accountNumber номер карты
     * @param pincode пин-код карты
     * @return Результат удаления
     */
    public ResponseEntity<?> deleteCard(Long ownerUserId, String accountNumber, Short pincode){
        Optional<CardEntity> card = repository.findAllByAccountNumber(accountNumber);

        if(!card.isPresent()){
            return ResponseEntity.badRequest().body("Карты с номером " + accountNumber + " не существует!");
        }
        if (!card.get().getOwnerUserId().equals(ownerUserId) || !(card.get().getPincode().equals(pincode))){
            return ResponseEntity.badRequest().body("Вы не можете удалить карту, потому что она не принадлежит вам!");
        }
        if(card.get().getBalance() != 0){
            return ResponseEntity.badRequest().body("Пожалуйста, выведите деньги с карты перед её удалением!");
        }
        Long id = card.get().getId();
        repository.deleteById(id);
        return ResponseEntity.ok().body("Вы успешно удалили карту!");
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
                                        .body("У этого пользователя нет карт!");

        return ResponseEntity.ok(cards);
    }
    //todo после создания ролей, добавить сюда проверку на содержание токена роли МОДЕР
    /** ТОЛЬКО ДЛЯ МОДЕРОВ
     * Получение ВСЕХ карт в банке
     *
     * @return Все карты банка
     */
    public ResponseEntity<?> getAllCards() {
        List<CardEntity> cards = repository.findAll();

        if (cards.isEmpty()) return ResponseEntity
                                        .status(HttpStatus.NOT_FOUND)
                                        .body("НИ У КОГО ИЗ ПОЛЬЗОВАТЕЛЕЙ НЕТ КАРТ!");

        return ResponseEntity.ok(cards);
    }

}

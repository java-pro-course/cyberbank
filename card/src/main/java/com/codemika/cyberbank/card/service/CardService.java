package com.codemika.cyberbank.card.service;

import com.codemika.cyberbank.card.entity.DebitCardEntity;
import com.codemika.cyberbank.card.repository.CreditCardRepository;
import com.codemika.cyberbank.card.repository.DebitCardRepository;
import com.codemika.cyberbank.card.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardService {
    private final DebitCardRepository debitRepository;
    public final JwtUtil jwtUtil;


    /**
     * Метод для перевода денег с карты на карту
     *
     * @param token токен переводящего деньги
     * @param pincode пин-код карты, с которой переводятся деньги
     * @param id id-карты, с которой переводятся деньги
     * @param value количество переводимых денег (в рублях)
     * @param receivingId id-карты, на которую переводятся деньги
     * @return сообщение об переводе и текущий баланс
     */
//    @Transactional
//    public ResponseEntity<?> moneyTransfer(String token, String pincode, Long id, Long value, Long receivingId) {
//        Optional<DebitCardEntity> card = repository.findById(id);
//        Optional<DebitCardEntity> receivingCard = repository.findById(receivingId);
//
//        Claims claimsParseToken = jwtUtil.getClaims(token);
//        Long ownerUserId = claimsParseToken.get("id", Long.class);
//
//        if (value == null)
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("Некорректная сумма перевода");
//        if (value <= 0)
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("Вы не можете переводить отрицательные суммы");
//
//        if (!card.isPresent())
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body("Карты с id " + id + " не существует");
//
//        if (!pincode.equals(card.get().getPincode()))
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("Неверный пин-код");
//
//        if (!receivingCard.isPresent())
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body("Карты с id " + receivingId + " не существует");
//
//        if (!card.get().getOwnerUserId().equals(ownerUserId))
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("Пользователь с id " + ownerUserId + " не обладает картой с id " + id);
//
//        if (card.get().getBalance() < value)
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("На карте недостаточно средств");
//
//        if (id.equals(receivingId))
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("Вы не можете перевести деньги на свою карту");
//
//        receivingCard
//                .get()
//                .setBalance(receivingCard.get().getBalance() + value);
//        card.get()
//                .setBalance(card.get().getBalance() - value);
//
//        repository.moneyTransfer(card.get().getBalance(), id);
//        repository.moneyTransfer(receivingCard.get().getBalance(), receivingId);
//
//        return ResponseEntity
//                .ok("Перевод доставлен! На данный момент ваш баланс " + card.get().getBalance() + " рублей");
//}
    /**
     * Метод для перевода денег с карты на карту по номерам карт
     * @param token - токен пользователя, переводящего деньги
     * @param pincode - пин-код карты, с которой переводятся деньги
     * @param accountNumber - номер карты, с которой переводятся деньги
     * @param value - количество переводимых денег (в рублях)
     * @param receivingAccountNumber - номер карты, на которую переводятся деньги
     * @return - сообщение о переводе и текущем балансе
     */
//    @Transactional
//    public ResponseEntity<?> moneyTransfer(String token, String pincode, String accountNumber, Long value, String receivingAccountNumber) {
//        Optional<DebitCardEntity> card = repository.findCardByAccountNumber(accountNumber);
//        Optional<DebitCardEntity> receivingCard = repository.findCardByAccountNumber(receivingAccountNumber);
//
//        Claims claimsParseToken = jwtUtil.getClaims(token);
//        Long ownerUserId = claimsParseToken.get("id", Long.class);
//
//        if (value == null)
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("Некорректная сумма перевода");
//        if(value <= 0)
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("Вы не можете переводить отрицательные суммы");
//
//        if (!card.isPresent())
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body("Карты с номером карты " + accountNumber + " не существует");
//
//        if (!pincode.equals(card.get().getPincode()))
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("Неверный пин-код");
//
//        if (!receivingCard.isPresent())
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body("Карты с номером карты " + receivingAccountNumber + " не существует");
//
//        if (!card.get().getOwnerUserId().equals(ownerUserId))
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("Пользователь с id " + ownerUserId + " не обладает картой с номером карты " + accountNumber);
//
//        if (card.get().getBalance() < value)
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("На карте недостаточно средств");
//
//        if (accountNumber.equals(receivingAccountNumber))
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("Вы не можете перевести деньги на свою карту");
//
//        receivingCard
//                .get()
//                .setBalance(receivingCard.get().getBalance() + value);
//        card.get()
//                .setBalance(card.get().getBalance() - value);
//
//        repository.moneyTransfer(card.get().getBalance(), card.get().getId());
//        repository.moneyTransfer(receivingCard.get().getBalance(), receivingCard.get().getId());
//
//        return ResponseEntity
//                .ok("Перевод доставлен! На данный момент ваш баланс " + card.get().getBalance() + " рублей");
//    }

    /**
     * Изменение названия карты по id карты
     * @param token - токен пользователя
     * @param id - id карты
     * @param newTitle - новое название карты
     * @return - сообщение об изменении названия карты
     */
//    public ResponseEntity<?> changeCardTitle(String token, Long id, String newTitle) {
//        Optional<DebitCardEntity> card = repository.findById(id);
//        Claims claimsParseToken = jwtUtil.getClaims(token);
//        Long ownerUserId = claimsParseToken.get("id", Long.class);
//        if (!card.isPresent()) {
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body("Карты с таким номером не существует!");
//        }
//        if (!card.get().getOwnerUserId().equals(ownerUserId))
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("Нельзя менять данные чужой карты!");
//        repository.updateCardTitle(newTitle, card.get().getId());
//        return ResponseEntity.ok("Название карты изменено.");
//    }

    /**
     * Изменение названия карты по номеру карты
     * @param token - токен пользователя
     * @param accountNumber - номер карты
     * @param newTitle - новое название карты
     * @return - сообщение об изменении названия карты
     */
//    public ResponseEntity<?> changeCardTitle(String token, String accountNumber, String newTitle) {
//        Optional<DebitCardEntity> card = repository.findCardByAccountNumber(accountNumber);
//        Claims claimsParseToken = jwtUtil.getClaims(token);
//        Long ownerUserId = claimsParseToken.get("id", Long.class);
//        if (!card.isPresent()) {
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body("Карты с таким номером не существует!");
//        }
//        if (!card.get().getOwnerUserId().equals(ownerUserId))
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("Нельзя менять данные чужой карты!");
//
//        repository.updateCardTitle(newTitle, card.get().getId());
//        return ResponseEntity.ok("Название карты изменено.");
//    }

    /**
     * Изменение пин-кода по id карты
     * @param token - токен пользователя
     * @param id - id карты
     * @param pincode - старый пин-код
     * @param newPinCode - новый пин-код карты
     * @return - сообщение об изменении пин-кода карты
     */
//    public ResponseEntity<?> changeCardPinCode(String token, Long id, String pincode, String newPinCode) {
//        Optional<DebitCardEntity> card = repository.findById(id);
//        Claims claimsParseToken = jwtUtil.getClaims(token);
//        Long ownerUserId = claimsParseToken.get("id", Long.class);
//        if (!card.isPresent()) {
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body("Карты с таким id не существует!");
//        }
//        if (!card.get().getOwnerUserId().equals(ownerUserId))
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("Нельзя менять данные чужой карты!");
//        if (!card.get().getPincode().equals(pincode)){
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("Пин-код неверный!");
//        }
//
//        repository.updateCardPinCode(newPinCode, card.get().getId());
//        return ResponseEntity.ok("Пин-код карты изменен.");
//    }

    /**
     * Изменение пин-кода карты по номеру карты
     * @param token - токен пользователя
     * @param accountNumber - номер карты
     * @param pincode - старый пин-код
     * @param newPinCode - новый пин-код карты
     * @return - сообщение об изменении пин-кода карты
     */
//    public ResponseEntity<?> changeCardPinCode(String token, String accountNumber, String pincode, String newPinCode) {
//        Optional<DebitCardEntity> card = repository.findCardByAccountNumber(accountNumber);
//        Claims claimsParseToken = jwtUtil.getClaims(token);
//        Long ownerUserId = claimsParseToken.get("id", Long.class);
//        if (!card.isPresent()) {
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body("Карты с таким номером не существует!");
//        }
//        if (!card.get().getOwnerUserId().equals(ownerUserId))
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("Нельзя менять данные чужой карты!");
//        if (!card.get().getPincode().equals(pincode)) {
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body("Пин-код неверный!");
//        }
//
//        repository.updateCardPinCode(newPinCode, card.get().getId());
//        return ResponseEntity.ok("Пин-код карты изменен.");
//    }

    /**
     * Генерация случайного номера карты
     *
     * @param n размер строки(у нас 16)
     * @return Случайную строку
     */
    public static String generateAccountNumber(int n) {
        String alphabet = "0123456789";

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int index = (int) (alphabet.length() * Math.random());
            result.append(alphabet.charAt(index));
        }

        return result.toString();
    }

    /**
     * Заморозка и разморозка карты
     *
     * @param token   токен владельца
     * @param cardId  id карты
     * @param pincode пин-код карты(не точно, это проверяется)
     * @return сообщение об успешной/не успешной заморозке/разморозке
     */
    @Transactional
    public ResponseEntity<?> FreezeAndUnfreezeCard(String token, Long cardId, String pincode) {
        Optional<DebitCardEntity> cardEntity = debitRepository.findById(cardId);
        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long id = claimsParseToken.get("id", Long.class);

        if (!cardEntity.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Данной карты не существует!");
        }
        if (!Objects.equals(cardEntity.get().getOwnerUserId(), id)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вы не являетесь владельцем данной карты!");
        }
        if (!Objects.equals(cardEntity.get().getPincode(), pincode)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вами введён неверный пин-код!");
        }

        debitRepository.updateById(!cardEntity.get().getIsActive(), cardId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Ваша карта была успешно " + (
                        cardEntity.get().getIsActive()
                                ? "заморожена!"  //Если карта была размороженной(true)
                                : "разморожена!" //Если карта была замороженной(false)
                ));
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

        List<DebitCardEntity> cards = debitRepository.findAllByOwnerUserId(id);// todo временно

        if (cards.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("У вас нет карт!");

        return ResponseEntity.ok(cards);
    }

    /**
     * ТОЛЬКО ДЛЯ МОДЕРОВ
     * Получение ВСЕХ карт в банке
     *
     * @return Все карты банка
     */
    public ResponseEntity<?> getAllCards() {
        List<DebitCardEntity> cards = debitRepository.findAll();// TODO временно

        if (cards.isEmpty()) return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("All users have no cards!");

        return ResponseEntity.ok(cards);
    }

    /**
     * Метод только для пользователей с ролями МОДЕР и ТЕСТЕР
     * Создание денег из воздуха
     *
     * @param cardId карта, на которую зачисляются деньги
     * @param value  количество денег
     * @return сообщение
     */
    @Transactional
    public ResponseEntity<?> getMeMoney(Long cardId, Long value) {
        Optional<DebitCardEntity> card = debitRepository.findById(cardId);
        if (value <= 0)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Вы не можете переводить отрицательные суммы");

        if (!card.isPresent())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Карты с id: " + cardId + " не существует");

        card.get()
                .setBalance(card.get().getBalance() + value);

        debitRepository.moneyTransfer(card.get().getBalance(), cardId);

        return ResponseEntity
                .ok("Вы успешно получили " + value + " рублей");
    }
}
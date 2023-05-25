package com.codemika.cyberbank.card.service;

import com.codemika.cyberbank.card.dto.RqCreateCreditCard;
import com.codemika.cyberbank.card.entity.CreditCardEntity;
import com.codemika.cyberbank.card.repository.CreditCardRepository;
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
public class CreditCardService {
    private final CreditCardRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:8081/api/auth/validate-user/?token=";
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * Создание кредитной карты
     *
     * @param token пользователя(будущего владельца)
     * @param rq    параметры карты
     * @return Созданную карту
     */
    public ResponseEntity<?> create(String token, RqCreateCreditCard rq) {
        int maxValue = (int) ((rq.getMonthlyIncome() * rq.getCreditTerm() * 0.5) / (1 + (0.15 * rq.getCreditTerm()))); // хз насколько я правильно эту формулу вписал.

        if (rq.getValue() > maxValue) {
            return ResponseEntity
                    .badRequest()
                    .body("Учитывая ваши данные, мы не можем выдать вам кредит на сумму: " + rq.getValue());
        }
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
        //Проверка на валидный пин-код
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
        String typeNewCard = "Кредитная";
        //Подготавливаем результат
        CreditCardEntity card = new CreditCardEntity()
                .setTitle(rq.getTitle())
                .setType(typeNewCard)
                .setOwnerUserId(ownerUserId)
                .setBalance(rq.getValue())
                .setPincode(
                        passwordEncoder.encode(rq.getPincode())
                )
                .setCreditTerm(rq.getCreditTerm())
                .setAccountNumber(
                        generateAccountNumber(16)
                );

        if (repository.findAllByAccountNumber(card.getAccountNumber()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Такая карта уже существует!");
        }

        ResponseEntity<Boolean> response = restTemplate.getForEntity(url + token, Boolean.class);

        //Проверка валидности пользователя
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.info(card.toString());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Такого пользователя не существует!");
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(repository.save(card));
    }

    public ResponseEntity<?> delete(String token, Long id, String pincode) {
        Optional<CreditCardEntity> card = repository.findById(id);

        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);

        if (!card.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Карта с id: " + id + " не существует!");
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

        repository.deleteById(id);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("Карта была успешно удалена");
    }

    public ResponseEntity<?> delete(String token, String accountNumber, String pincode) {
        Optional<CreditCardEntity> card = repository.findCardByAccountNumber(accountNumber);

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
}

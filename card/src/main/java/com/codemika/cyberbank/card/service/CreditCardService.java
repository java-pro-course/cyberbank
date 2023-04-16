package com.codemika.cyberbank.card.service;

import com.codemika.cyberbank.card.dto.RqCreateCreditCard;
import com.codemika.cyberbank.card.dto.RqCreateDebetCard;
import com.codemika.cyberbank.card.entity.CardEntity;
import com.codemika.cyberbank.card.repository.CardRepository;
import com.codemika.cyberbank.card.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreditCardService {
    private final CardRepository repository;
    public final JwtUtil jwtUtil;

    public ResponseEntity<?> createCreditCard(String token, RqCreateCreditCard rq) {
        //Проверка на валидный пин-код
        if (!rq.getPincode().toLowerCase().matches("[0-9]+"))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("The pincode must consist of digits!!! For example 3856");

        // это для расчета максимальной суммы кредита
        int maxValue = (int) ((rq.getMonthlyIncome() * rq.getCreditTerm() * 0.5) / (1 + (0.15 * rq.getCreditTerm()))); // хз насколько я правильно эту формулу вписал.

        if (rq.getValue() > maxValue)
        {
            return ResponseEntity.badRequest().body("given your details, we cannot issue you a loan in the amount: " + rq.getValue());
        }
        if (rq.getPincode().trim().length() != 4)
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("The pincode must consist of 4 digits!!! For example 3856");

        //Достаём id из токена
        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long ownerUserId = claimsParseToken.get("id", Long.class);

        //Подготавливаем результат
        CardEntity card = new CardEntity()
                .setTitle(rq.getTitle())
                .setType("Кредитная")
                .setOwnerUserId(1L) // отправляем сначала запрос в auth и проверяем этот id!
                // для отправки запроса используем RestTemplate!
                .setBalance(0L)
                .setPincode(rq.getPincode().trim())
                .setAccountNumber(
                        generateAccountNumber(16)
                )
                .setBalance(rq.getValue());

        // TODO: проверять номер карты на уникальность
        // TODO: проверять id-пользователя из rq на валидность
        card = repository.save(card);
        return ResponseEntity.ok(card);
    }

    public ResponseEntity<?> deleteCreditCard(Long ownerUserId, Long id) {
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
        // TODO надо будет как-нибудь добавить проверку погашен ли кредит.
        repository.deleteById(id);
        return ResponseEntity.ok().body("The card has been successfully deleted");
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
}

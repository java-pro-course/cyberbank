package com.codemika.cyberbank.card.service;

import com.codemika.cyberbank.card.dto.RqCreateCard;
import com.codemika.cyberbank.card.entity.CardEntity;
import com.codemika.cyberbank.card.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository repository;

    public ResponseEntity<?> createCard(RqCreateCard rq) {
        CardEntity card = new CardEntity()
                .setTitle(rq.getTitle())
                .setType(rq.getType())
                .setOwnerUserId(rq.getOwnerUserId()) // отправляем сначала запрос в auth и проверяем этот id!
                                                     // для отправки запроса используем RestTemplate!
                .setBalance(0L)
                .setAccountNumber(
                        generateAccountNumber(16)
                );

        // TODO: проверять номер карты на уникальность
        // TODO: проверять id-пользователя из rq на валидность

        card = repository.save(card);
        return ResponseEntity.ok(card);
    }

    /**
     * Метод для генерации случайного номера карты
     * @param n размер строки (у нас 16)
     * @return случайную строку
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
}

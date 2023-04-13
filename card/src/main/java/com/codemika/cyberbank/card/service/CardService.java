package com.codemika.cyberbank.card.service;

import com.codemika.cyberbank.card.dto.RqCreateCard;
import com.codemika.cyberbank.card.entity.CardEntity;
import com.codemika.cyberbank.card.repository.CardRepository;
import com.codemika.cyberbank.card.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository repository;
    public final JwtUtil jwtUtil;

    public ResponseEntity<?> createCard(RqCreateCard rq, Long id) {
        CardEntity card = new CardEntity()
                .setTitle(rq.getTitle())
                .setType(rq.getType())
                .setOwnerUserId(id) // отправляем сначала запрос в auth и проверяем этот id!
                //TODO: исправить 400 ошибку                                     // для отправки запроса используем RestTemplate!
                .setBalance(0L)
                .setPincode(rq.getPincode())
                .setAccountNumber(
                        generateAccountNumber(16)
                );
        if(repository.findAllByAccountNumber().isPresent())
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

    /**
     * Метод для вывода всех карт определённого пользователя.
     * @param token уникальный токен авторизации, содержащий id его пользователя.
     * @return все карты пользователя, чей токен мы получаем.
     */
    public ResponseEntity<?> getAllCards(String token) {
        Claims claimsParseToken = jwtUtil.getClaims(token);
        Long id = claimsParseToken.get("id", Long.class);

        List<CardEntity> cards = repository.findAllByOwnerUserId(id);

        return ResponseEntity.ok(cards);
    }
}

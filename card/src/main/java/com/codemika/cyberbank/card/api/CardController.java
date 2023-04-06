package com.codemika.cyberbank.card.api;

import com.codemika.cyberbank.card.dto.RqCreateCard;
import com.codemika.cyberbank.card.service.CardService;
import com.codemika.cyberbank.card.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService service;
    private final JwtUtil jwtUtil;

    @PostMapping("create")
    public ResponseEntity<?> createCard(@RequestBody RqCreateCard rq) {
        return service.createCard(rq);
    }
    @GetMapping("get-all-card")
    public ResponseEntity<?> getAllCards(@RequestHeader("Authorization") String token) {

        if (token.isEmpty() || token.trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Token must not be empty!");
        }

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Token is invalid!");
        }

        return service.getAllCards(token);
    }
    @PostMapping("money-transfer")
    public ResponseEntity<?> MoneyTransfer(Long id,  Long ownerUserId,  Long value, Long receivingId) {
        return service.moneyTransfer(id, ownerUserId, value, receivingId);
    }
}

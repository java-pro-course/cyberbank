package com.codemika.cyberbank.card.api;

import com.codemika.cyberbank.card.dto.RqCreateCard;
import com.codemika.cyberbank.card.service.CardService;
import com.codemika.cyberbank.card.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService service;
    private final JwtUtil jwtUtil;

    @PostMapping("create")
    public ResponseEntity<?> createCard(@RequestHeader("Authorization") String token, @RequestBody RqCreateCard rq) {
        Claims claims = jwtUtil.getClaims(token);
        Long id = claims.get("id", Long.class);
        return service.createCard(rq, id);
    }

    @GetMapping("get-all-cards")
    public ResponseEntity<?> getAllCards(@RequestHeader("Authorization") String token) {

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.ok("token invalid!");
        }

        return service.getAllCards(token);
    }
}

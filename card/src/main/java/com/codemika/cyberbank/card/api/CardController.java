package com.codemika.cyberbank.card.api;

import com.codemika.cyberbank.card.dto.RqCreateCard;
import com.codemika.cyberbank.card.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService service;

    @PostMapping("create")
    public ResponseEntity<?> createCard(@RequestBody RqCreateCard rq) {
        return service.createCard(rq);
    }
}

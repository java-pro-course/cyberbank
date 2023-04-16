package com.codemika.cyberbank.card.api;

import com.codemika.cyberbank.card.dto.RqCreateCard;
import com.codemika.cyberbank.card.entity.CardEntity;
import com.codemika.cyberbank.card.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("api/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService service;

    @PostMapping("create")
    public ResponseEntity<?> createCard(@RequestBody RqCreateCard rq) {
        return service.createCard(rq);
    }

    @PutMapping("freeze-card-by-id/{id}")
    public ResponseEntity<?> freezeCard(@RequestBody RqCreateCard card, @PathVariable Long id, @PathVariable Date time, @PathVariable Long userId){
        return service.FreezeCard(card, id, time, userId);
    }
}

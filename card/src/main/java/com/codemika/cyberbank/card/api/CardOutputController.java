package com.codemika.cyberbank.card.api;

import com.codemika.cyberbank.card.service.CardService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("api/card/output")
@Data
public class CardOutputController {
    private final CardService cardService;
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("get-user-credit-cards")
    public ResponseEntity<?> getCreditCards(@RequestParam String token){
        cardService.getAllCreditCards(token);
        return null;
    }
}

package com.codemika.cyberbank.card.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Это класс для jwt-токенов
 */
@Component
@Slf4j
public class JwtUtil {
    /**
     * SIGN - это ключ для генерации и расшифровки токена.
     */
   private final String SIGN = "SuPErSecRETsign228CyBERbANk";

    /**
     * Создание нового токена(используется при входе)
     *
     * Длительность токена 69,(4) дней.
     * @param claims информация, содержащаяся в токене.
     * @return Токен
     */
    public String generateToken(Claims claims){
        long nowMillis = System.currentTimeMillis();
        long expirationMillis = nowMillis + 6_000_000_000L;
        Date exp = new Date(expirationMillis);

        return Jwts.builder().
                setIssuedAt(new Date(System.currentTimeMillis()))
                .setClaims(claims)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS512, SIGN)
                .compact();
    }

    /**
     * Проверка токена
     *
     * @param token токен
     * @return true или false, правильный токен или нет
     */
    public boolean validateToken(String token){
        token = token.replace("\"", "");
        token = token.trim();
        boolean isTokenValid = false;

        try{
            Jwts.parser().setSigningKey(SIGN).parseClaimsJws(token);
            isTokenValid = true;
        }catch (RuntimeException e){
            log.error("Token is invalid");
            log.error(e.getMessage() + "=>"+ e);
        }

        return isTokenValid;
    }

    /**
     * Извлечение информации из токена
     *
     * @param token токен
     * @return Информацию, содержащуюся в токене
     */
    public Claims getClaims(String token){
        token = token.replace("\"", "");
        token = token.trim();

        try{
            return Jwts.parser()
                    .setSigningKey(SIGN)
                    .parseClaimsJws(token)
                    .getBody();
        }catch (RuntimeException e){
            log.error("Token is invalid");
            log.error(e.getMessage() + "=>"+ e);
        }

        return null;
    }
}

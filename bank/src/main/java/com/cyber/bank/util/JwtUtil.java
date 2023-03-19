package com.cyber.bank.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtUtil {
    public String generateToken(Claims claims) {
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + 1000000L;
        Date exp = new Date(expMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS512, "secret")
                .compact();
    }

    public boolean validateToken(final String token) {
        try {
            Jwts.parser().setSigningKey("secret").parseClaimsJws(token);
            return true;
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
        return false;
    }

    public Claims getClaims(String token){
        try{
            return Jwts.parser()
                    .setSigningKey("secret")
                    .parseClaimsJws(token)
                    .getBody();
        }catch(Exception e){
            log.error(e.getMessage());
        }
        return null;
    }
}

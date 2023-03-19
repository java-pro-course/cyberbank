package com.codemika.cyberbank.authentication.jwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtUtil {
   private final String SIGN = "SuPErSecRETsign228CyBERbANk";
    public String generateToken(Claims claims){
        long nowMillis = System.currentTimeMillis();
        long expirationMillis = nowMillis + 10000000000000L;
        Date exp = new Date(expirationMillis);
        return Jwts.builder().
                setIssuedAt(new Date(System.currentTimeMillis()))
                .setClaims(claims)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS512, SIGN)
                .compact();

    }
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

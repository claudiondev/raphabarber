package com.claudio.dev.raphabarber.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

   @org.springframework.beans.factory.annotation.Value("${api.security.token.secret}")
   private String secret;

   private java.security.Key getChaveAssinatura() {
       return io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes());
   }

    public String gerarToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date (System.currentTimeMillis() + 86400000))
                .signWith(getChaveAssinatura())
                .compact();

    }

    public String extrairEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getChaveAssinatura())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

     public boolean validarToken(String token, String email) {return extrairEmail(token).equals(email); }

    /**
     * Extrai o subject (email) do token
     */
    public String extractSubject(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getChaveAssinatura())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Valida se o token é válido (sem expiração e sem erro de assinatura)
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getChaveAssinatura())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

package com.claudio.dev.raphabarber.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Objects;

@Service
public class JwtService {

   @org.springframework.beans.factory.annotation.Value("${api.security.token.secret}")
   private String secret;

   @PostConstruct
   private void validarChave() {
       if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
           throw new IllegalStateException("api.security.token.secret precisa ter no mínimo 32 bytes (256 bits)");
       }
   }

   private java.security.Key getChaveAssinatura() {
       return io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
   }

    private static final String ISSUER = "raphabarber-api";
    private static final String TIPO_ACCESS = "access";

    public String gerarToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuer(ISSUER)
                .claim("type", TIPO_ACCESS)
                .setIssuedAt(new Date())
                .setExpiration(new Date (System.currentTimeMillis() + 86400000))
                .signWith(getChaveAssinatura())
                .compact();

    }

    public String extrairEmail(String token) {
        return extractSubject(token);
    }

     public boolean validarToken(String token, String email) {return Objects.equals(extrairEmail(token), email); }

    // retorna null se o token for inválido, expirado, ou tiver issuer/tipo diferentes do esperado
    public String extractSubject(String token) {
        try {
            io.jsonwebtoken.Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getChaveAssinatura())
                    .requireIssuer(ISSUER)
                    .require("type", TIPO_ACCESS)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenValid(String token) {
        return extractSubject(token) != null;
    }
}

package com.claudio.dev.raphabarber.config;

import com.claudio.dev.raphabarber.service.JwtService;
import com.claudio.dev.raphabarber.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public JwtAuthenticationFilter(JwtService jwtService, UsuarioService usuarioService) {
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = extractTokenFromRequest(request);

            if (token != null && jwtService.isTokenValid(token)) {
                String email = jwtService.extractSubject(token);
                UserDetails userDetails = usuarioService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // não propaga: deixa sem autenticação e o SecurityConfig bloqueia via .authenticated()
            logger.error("Erro ao validar JWT: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}


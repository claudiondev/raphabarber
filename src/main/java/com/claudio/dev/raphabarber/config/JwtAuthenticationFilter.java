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

/**
 * Filtro JWT que intercepta todas as requisições e valida o token
 * Executa UMA VEZ por requisição (OncePerRequestFilter)
 */
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
            // 1. Extrair o token do header Authorization
            String token = extractTokenFromRequest(request);

            // 2. Se tem token e é válido, validar
            if (token != null && jwtService.isTokenValid(token)) {
                // 3. Extrair o email/subject do token
                String email = jwtService.extractSubject(token);

                // 4. Carregar o usuário do banco
                UserDetails userDetails = usuarioService.loadUserByUsername(email);

                // 5. Criar um objeto de autenticação
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );

                // 6. Colocar no SecurityContext (Spring sabe que está autenticado)
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Se houver erro, apenas continua (vai ser bloqueado pelo @authenticated)
            logger.error("Erro ao validar JWT: " + e.getMessage());
        }

        // 7. Continuar a requisição
        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o token do header Authorization
     * Formato: "Bearer <token>"
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer "
        }
        return null;
    }
}


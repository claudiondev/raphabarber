package com.claudio.dev.raphabarber.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desativa CSRF pois usamos JWT
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Ativa a configuração de CORS abaixo
                .authorizeHttpRequests(authz -> authz
                        // Libera rotas de autenticação (Login e Cadastro)
                        .requestMatchers("/auth/**").permitAll()

                        // Libera visualização de serviços para qualquer um
                        .requestMatchers(HttpMethod.GET, "/servicos/**").permitAll()

                        // Apenas ADMIN pode alterar serviços (POST, PUT, DELETE)
                        .requestMatchers(HttpMethod.POST, "/servicos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/servicos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/servicos/**").hasRole("ADMIN")

                        // Agendamentos exigem que o usuário esteja logado
                        .requestMatchers("/agendamentos/**").authenticated()

                        // Qualquer outra rota exige autenticação
                        .anyRequest().authenticated()
                )
                // Define que a API não guarda estado (Stateless) porque usa Token
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Adiciona o filtro do JWT antes do filtro padrão de autenticação
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // --- CONFIGURAÇÃO DE SEGURANÇA DEFINITIVA ---
        // Aqui você libera apenas o seu computador e o seu site oficial na Vercel.
        // Isso impede que sites maliciosos acessem seus dados.
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "https://raphabarber-front.vercel.app" // SUBSTITUA PELO SEU LINK REAL DA VERCEL
        ));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos (Authorization é essencial para o JWT)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));

        // Permite que o navegador envie o Token/Cookies com segurança
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
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
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/auth/**").permitAll()
                        // documentação da API - não expõe dado nenhum, só a especificação dos endpoints
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/servicos/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/servicos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/servicos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/servicos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/portfolio/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/portfolio/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/portfolio/**").hasRole("ADMIN")
                        .requestMatchers("/agendamentos/**").authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @org.springframework.beans.factory.annotation.Value("${cors.allowed-origins:http://localhost:*}")
    private String corsAllowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // domínio exato de produção vem de CORS_ALLOWED_ORIGINS (ex: https://raphabarber.vercel.app) -
        // sem wildcard "*.vercel.app", que permitia qualquer projeto Vercel de terceiros chamar a API autenticado
        configuration.setAllowedOriginPatterns(Arrays.stream(corsAllowedOrigins.split(","))
                .map(String::trim)
                .toList());
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
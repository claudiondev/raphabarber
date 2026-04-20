package com.claudio.dev.raphabarber.config;

import com.claudio.dev.raphabarber.model.Usuario;
import com.claudio.dev.raphabarber.model.UserRole;
import com.claudio.dev.raphabarber.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeeder {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.findByEmail(adminEmail).isEmpty()) {
                Usuario admin = new Usuario();
                admin.setEmail(adminEmail);
                admin.setSenha(passwordEncoder.encode(adminPassword));
                admin.setRole(UserRole.ADMIN);

                usuarioRepository.save(admin);
                log.info("Usuário Admin configurado via variáveis de segurança!");
            }
        };
    }
}
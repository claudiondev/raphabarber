package com.claudio.dev.raphabarber.config;

import com.claudio.dev.raphabarber.model.Usuario;
import com.claudio.dev.raphabarber.model.UserRole;
import com.claudio.dev.raphabarber.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeeder {

    // Busca os valores definidos no application.properties
    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Agora usamos as variáveis locais que escondem o texto real
            if (usuarioRepository.findByEmail(adminEmail).isEmpty()) {

                Usuario admin = new Usuario();
                admin.setEmail(adminEmail);
                admin.setSenha(passwordEncoder.encode(adminPassword));
                admin.setRole(UserRole.ADMIN);

                usuarioRepository.save(admin);
                System.out.println("✅ Usuário Admin configurado via variáveis de segurança!");
            }
        };
    }
}
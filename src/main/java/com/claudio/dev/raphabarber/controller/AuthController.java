package com.claudio.dev.raphabarber.controller;
import com.claudio.dev.raphabarber.model.UserRole;
import com.claudio.dev.raphabarber.model.Usuario;
import com.claudio.dev.raphabarber.repository.UsuarioRepository;
import com.claudio.dev.raphabarber.service.JwtService;
import com.claudio.dev.raphabarber.service.RateLimiterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Registro e login - endpoints públicos, com rate limiting por IP")
public class AuthController {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RateLimiterService rateLimiterService;

    @Operation(summary = "Registrar novo cliente", description = "Cria uma conta com role CLIENTE. Limite de 5 tentativas a cada 15 min por IP.")
    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@Valid @RequestBody Usuario usuario, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        if (rateLimiterService.excedeuLimite("registrar:" + ip)) {
            return ResponseEntity.status(429).body(Map.of("erro", "Muitas tentativas. Tente novamente em alguns minutos."));
        }

        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "E-mail já cadastrado!"));
        }
        usuario.setRole(UserRole.CLIENTE);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(Map.of("mensagem", "Usuário registrado com sucesso!"));
    }

    @Operation(summary = "Login", description = "Retorna um JWT válido por 24h. Limite de 5 tentativas a cada 15 min por IP.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        if (rateLimiterService.excedeuLimite("login:" + ip)) {
            return ResponseEntity.status(429).body(Map.of("erro", "Muitas tentativas. Tente novamente em alguns minutos."));
        }

        Usuario encontrado = usuarioRepository.findByEmail(usuario.getEmail())
                .orElse(null);
        if (encontrado == null || !passwordEncoder.matches(usuario.getSenha(), encontrado.getSenha())) {
            return ResponseEntity.status(401).body(Map.of("erro", "E-mail ou senha incorretos"));
        }
        String token = jwtService.gerarToken(encontrado.getEmail());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "id", encontrado.getId(),
                "email", encontrado.getEmail(),
                "role", encontrado.getRole()
        ));
    }
}
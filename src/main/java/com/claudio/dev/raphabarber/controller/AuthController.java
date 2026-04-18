package com.claudio.dev.raphabarber.controller;
import com.claudio.dev.raphabarber.model.UserRole;
import com.claudio.dev.raphabarber.model.Usuario;
import com.claudio.dev.raphabarber.repository.UsuarioRepository;
import com.claudio.dev.raphabarber.service.JwtService;
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
public class AuthController {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "E-mail já cadastrado!"));
        }
        if (usuario.getRole() == null) {
            usuario.setRole(UserRole.CLIENTE);
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(Map.of("mensagem", "Usuário registrado com sucesso!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
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
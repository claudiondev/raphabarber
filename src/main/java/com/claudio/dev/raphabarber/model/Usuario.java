package com.claudio.dev.raphabarber.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "usuarios")

public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Column(unique = true, nullable = false)
    private String email;
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String senha;
    private String codigoRecuperacao;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // WRITE_ONLY: aceita no login/registro, mas nunca aparece na resposta (mesmo aninhado, ex: Agendamento.cliente)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getCodigoRecuperacao() {
        return codigoRecuperacao;
    }

    public void setCodigoRecuperacao(String codigoRecuperacao) {
        this.codigoRecuperacao = codigoRecuperacao;
    }

    public UserRole getRole() {
        return role;
    }
    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.ADMIN) {
            // admin também acumula a role de cliente (ex: pode agendar para si mesmo)
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_CLIENTE"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"));
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
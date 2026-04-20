package com.claudio.dev.raphabarber.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name = "portfolio")
@Data
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Exige https e extensão de imagem - bloqueia javascript:, file:// e URLs de domínios/formatos arbitrários
    @NotBlank(message = "A URL da imagem é obrigatória")
    // aceita query string/fragmento depois da extensão (comum em CDNs como Cloudinary/S3 presigned URLs)
    @Pattern(
            regexp = "(?i)^https://.+\\.(png|jpg|jpeg|webp|gif)([?#].*)?$",
            message = "A URL deve ser https e apontar para uma imagem (png, jpg, jpeg, webp ou gif)"
    )
    @Column(nullable = false, length = 1000)
    private String urlImagem;

    @NotBlank(message = "A legenda é obrigatória")
    @Column(nullable = false)
    private String legenda;
}
package com.banco.cast.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String senha;
    private boolean admin = false;
    @OneToOne(mappedBy = "titular", cascade = CascadeType.ALL)
    private Conta conta;

    public Usuario(String nome, String senha, boolean admin) {
        this.nome = nome;
        this.senha = senha;
        this.admin = admin;
    }

    public record UsuarioRequest(String nome, String senha, boolean admin) {}
}


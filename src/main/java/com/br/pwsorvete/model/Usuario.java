package com.br.pwsorvete.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome de usuário é obrigatório")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "A senha é obrigatória")
    private String password;

    private boolean isAdmin;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @Email(message = "Email inválido")
    private String email;

    private boolean ativo = true;

    public Usuario() {}

    //Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}


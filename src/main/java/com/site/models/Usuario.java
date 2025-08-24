package com.site.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome não pode ser vazio")
    private String nome;

    @Email(message = "Email deve ser válido")
    @NotBlank(message = "O email não pode ser vazio")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "A senha não pode ser vazia")
    private String senha;

    // Adicionamos ADMIN aqui
    public enum Role {
        USER,
        SUBSCRIBER,
        ADMIN
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "acesso_valido_ate")
    private LocalDateTime acessoValidoAte;

    @Column(unique = true)
    private String cpf;

    // <<< MUDANÇA AQUI: Adição do campo telefone
    private String telefone;


    // Getters e Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public LocalDateTime getAcessoValidoAte() { return acessoValidoAte; }
    public void setAcessoValidoAte(LocalDateTime acessoValidoAte) { this.acessoValidoAte = acessoValidoAte; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    // <<< MUDANÇA AQUI: Getters e Setters para telefone
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }


    // hashCode e equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id) && Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}
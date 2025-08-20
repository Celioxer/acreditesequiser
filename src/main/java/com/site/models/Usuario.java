package com.site.models;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String senha;

    @Column(nullable = true)
    private String telefone;

    // Campo CPF adicionado para que o código de pagamento funcione
    @Column(nullable = true)
    private String cpf;

    @Column(name = "acesso_valido_ate")
    private LocalDateTime acessoValidoAte; // Novo campo para controlar o acesso

    // Métodos UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
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
        // A conta está habilitada somente se a data de validade for no futuro ou agora.
        return acessoValidoAte != null && acessoValidoAte.isAfter(LocalDateTime.now());
    }

    // Getters e Setters
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getTelefone() { return telefone;}
    public void setTelefone(String telefone) {this.telefone = telefone;}

    public LocalDateTime getAcessoValidoAte() { return acessoValidoAte; }
    public void setAcessoValidoAte(LocalDateTime acessoValidoAte) { this.acessoValidoAte = acessoValidoAte; }

    // Getters e Setters para o novo campo CPF
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
}

package com.site.dto;

import com.site.models.Usuario;
import java.time.LocalDateTime;

public class AdminUserViewDTO {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private Usuario.Role role;
    private LocalDateTime acessoValidoAte;
    private String status;
    private Long diasParaVencer;

    public AdminUserViewDTO(Long id, String nome, String email, String telefone, Usuario.Role role, LocalDateTime acessoValidoAte, String status, Long diasParaVencer) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.role = role;
        this.acessoValidoAte = acessoValidoAte;
        this.status = status;
        this.diasParaVencer = diasParaVencer;
    }

    // --- GETTERS E SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ****** GETTER E SETTER ADICIONADOS ******
    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    // ***************************************

    public Usuario.Role getRole() {
        return role;
    }

    public void setRole(Usuario.Role role) {
        this.role = role;
    }

    // ****** GETTER E SETTER ADICIONADOS ******
    public LocalDateTime getAcessoValidoAte() {
        return acessoValidoAte;
    }

    public void setAcessoValidoAte(LocalDateTime acessoValidoAte) {
        this.acessoValidoAte = acessoValidoAte;
    }
    // ***************************************

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDiasParaVencer() {
        return diasParaVencer;
    }

    public void setDiasParaVencer(Long diasParaVencer) {
        this.diasParaVencer = diasParaVencer;
    }
}
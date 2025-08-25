package com.site.dto;

import com.site.models.Usuario;

public class AdminUserViewDTO {
    private Long id;
    private String nome;
    private String email;
    private Usuario.Role role;
    private Long diasParaVencer;
    private String status;

    // Construtor vazio (boa prática para DTOs)
    public AdminUserViewDTO() {
    }

    public AdminUserViewDTO(Long id, String nome, String email, Usuario.Role role, Long diasParaVencer, String status) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.diasParaVencer = diasParaVencer;
        this.status = status;
    }

    // <<< SOLUÇÃO: Getters e Setters públicos para todas as propriedades

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

    public Usuario.Role getRole() {
        return role;
    }

    public void setRole(Usuario.Role role) {
        this.role = role;
    }

    public Long getDiasParaVencer() {
        return diasParaVencer;
    }

    public void setDiasParaVencer(Long diasParaVencer) {
        this.diasParaVencer = diasParaVencer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
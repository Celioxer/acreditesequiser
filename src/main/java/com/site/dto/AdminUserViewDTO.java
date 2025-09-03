package com.site.dto;

import com.site.models.Usuario;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminUserViewDTO {
    // Campos existentes
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private Usuario.Role role;
    private LocalDateTime acessoValidoAte;
    private String status;
    private Long diasParaVencer;

    // ****** NOVOS CAMPOS ESSENCIAIS ADICIONADOS ******
    private BigDecimal ultimoPagamentoValor;
    private String ultimoMetodoPagamento;
    private Long totalPagamentos;
    // ***********************************************

    // Construtor atualizado para receber TUDO
    public AdminUserViewDTO(Long id, String nome, String email, String telefone, Usuario.Role role,
                            LocalDateTime acessoValidoAte, String status, Long diasParaVencer,
                            BigDecimal ultimoPagamentoValor, String ultimoMetodoPagamento, Long totalPagamentos) { // Adicionados aqui
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.role = role;
        this.acessoValidoAte = acessoValidoAte;
        this.status = status;
        this.diasParaVencer = diasParaVencer;
        this.ultimoPagamentoValor = ultimoPagamentoValor; // Adicionado aqui
        this.ultimoMetodoPagamento = ultimoMetodoPagamento; // Adicionado aqui
        this.totalPagamentos = totalPagamentos; // Adicionado aqui
    }

    // ****** GETTERS PARA OS NOVOS CAMPOS ******
    public BigDecimal getUltimoPagamentoValor() { return ultimoPagamentoValor; }
    public String getUltimoMetodoPagamento() { return ultimoMetodoPagamento; }
    public Long getTotalPagamentos() { return totalPagamentos; }
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
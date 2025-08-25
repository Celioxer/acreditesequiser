package com.site.dto;

public class AdminDashboardDTO {
    private long totalPagantesAtivos;

    public AdminDashboardDTO(long totalPagantesAtivos) {
        this.totalPagantesAtivos = totalPagantesAtivos;
    }

    // Getters e Setters
    public long getTotalPagantesAtivos() { return totalPagantesAtivos; }
    public void setTotalPagantesAtivos(long totalPagantesAtivos) { this.totalPagantesAtivos = totalPagantesAtivos; }
}
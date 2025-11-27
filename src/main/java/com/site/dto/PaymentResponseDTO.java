package com.site.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentResponseDTO {
    private String status;
    @JsonProperty("external_reference") // Mapeia o nome do JSON
    private String externalReference;
    private PayerDTO payer;
    // ... getters e setters ...

    public static class PayerDTO {
        private String email;
        // ... getters e setters ...
    }
}
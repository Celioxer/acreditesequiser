package com.site.controllers;

import com.site.services.QrAuthService; // Você precisará criar este serviço
import com.site.dto.*; // Pacote para seus DTOs (Data Transfer Objects)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/qr") // Um novo caminho base para a API
public class QrAuthController {

    @Autowired
    private QrAuthService qrAuthService;

    // 1. Endpoint para o site iniciar o processo e pedir um token
    @PostMapping("/initiate")
    public ResponseEntity<InitiateQrResponse> initiate() {
        String sessionToken = qrAuthService.createPendingSession();
        return ResponseEntity.ok(new InitiateQrResponse(sessionToken));
    }

    // 2. Endpoint para o site ficar verificando o status
    @GetMapping("/status/{sessionToken}")
    public ResponseEntity<QrStatusResponse> getStatus(@PathVariable String sessionToken) {
        QrStatusResponse response = qrAuthService.getSessionStatus(sessionToken);
        return ResponseEntity.ok(response);
    }

    // 3. Endpoint para o APP MOBILE confirmar o login
    @PostMapping("/confirm")
    // @PreAuthorize("isAuthenticated()") // Este endpoint deve ser seguro!
    public ResponseEntity<Void> confirm(@RequestBody ConfirmQrRequest request) {
        // Aqui você pega o usuário que está logado no App Mobile
        String userId = "ID_DO_USUARIO_LOGADO_NO_APP";
        qrAuthService.confirmSession(request.getSessionToken(), userId);
        return ResponseEntity.ok().build();
    }
}
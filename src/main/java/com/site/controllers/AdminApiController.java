package com.site.controllers;

import com.site.dto.AdminDashboardDTO;
import com.site.dto.AdminUserViewDTO;
import com.site.dto.PaymentHistoryDTO;
import com.site.models.Usuario;
import com.site.services.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final AdminService adminService;

    public AdminApiController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDTO> getDashboardData() {
        return ResponseEntity.ok(adminService.getDashboardData());
    }

    // ****** MÉTODO ATUALIZADO PARA ACEITAR FILTROS E PESQUISA ******
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserViewDTO>> getAllUsers(
            @RequestParam(value = "status", required = false, defaultValue = "") String status,
            @RequestParam(value = "search", required = false, defaultValue = "") String search
    ) {
        // Agora chamamos um novo método no serviço que sabe como filtrar os dados
        List<AdminUserViewDTO> users = adminService.findUsersFiltered(status, search);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{userId}/payments")
    public ResponseEntity<List<PaymentHistoryDTO>> getPaymentHistory(@PathVariable Long userId) {
        List<PaymentHistoryDTO> history = adminService.getPaymentHistoryForUser(userId);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/users/{userId}/role")
    public ResponseEntity<Void> updateUserRole(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        // Este método foi mantido como estava, pois já está correto.
        Usuario.Role newRole = Usuario.Role.valueOf(body.get("role"));
        adminService.updateUserRole(userId, newRole);
        return ResponseEntity.ok().build();
    }

    // ****** MÉTODO ANTIGO REMOVIDO E SUBSTITUÍDO PELO NOVO ABAIXO ******
    // O antigo @PostMapping("/users/{userId}/subscription") foi removido.

    // ****** NOVO MÉTODO PARA DEFINIR A DATA DE VENCIMENTO ******
    @PostMapping("/users/{userId}/subscription/set-date")
    public ResponseEntity<Void> setSubscriptionDate(
            @PathVariable Long userId,
            @RequestBody Map<String, String> payload
    ) {
        String newExpirationDateStr = payload.get("newExpirationDate");
        if (newExpirationDateStr == null || newExpirationDateStr.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        adminService.setSubscriptionDate(userId, newExpirationDateStr);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/raffle/draw")
    public ResponseEntity<AdminUserViewDTO> drawWinner() {
        // Este método foi mantido como estava, pois já está correto.
        Optional<AdminUserViewDTO> winnerOptional = adminService.drawRandomActiveSubscriber();
        return winnerOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
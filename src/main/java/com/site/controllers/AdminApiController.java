package com.site.controllers;

import com.site.dto.AdminDashboardDTO;
import com.site.dto.AdminEmailRequestDTO;
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

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserViewDTO>> getAllUsers(
            @RequestParam(value = "status", required = false, defaultValue = "") String status,
            @RequestParam(value = "search", required = false, defaultValue = "") String search
    ) {
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
        Usuario.Role newRole = Usuario.Role.valueOf(body.get("role"));
        adminService.updateUserRole(userId, newRole);
        return ResponseEntity.ok().build();
    }

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
        Optional<AdminUserViewDTO> winnerOptional = adminService.drawRandomActiveSubscriber();
        return winnerOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // <<< MÉTODO MOVIDO PARA DENTRO DA CLASSE >>>
    @PostMapping("/send-email")
    public ResponseEntity<Void> sendEmailToUsers(@RequestBody AdminEmailRequestDTO emailRequest) {
        adminService.sendEmailToSelectedUsers(
                emailRequest.getUserIds(),
                emailRequest.getSubject(),
                emailRequest.getBody()
        );
        return ResponseEntity.ok().build();
    }
}
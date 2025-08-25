package com.site.controllers;

import com.site.dto.AdminDashboardDTO;
import com.site.dto.AdminUserViewDTO;
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
    public ResponseEntity<List<AdminUserViewDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsersForAdminPanel());
    }

    @PostMapping("/users/{userId}/role")
    public ResponseEntity<Void> updateUserRole(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        Usuario.Role newRole = Usuario.Role.valueOf(body.get("role"));
        adminService.updateUserRole(userId, newRole);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{userId}/subscription")
    public ResponseEntity<Void> addSubscriptionDays(@PathVariable Long userId, @RequestBody Map<String, Integer> body) {
        Integer daysToAdd = body.get("days");
        if (daysToAdd == null || daysToAdd <= 0) {
            return ResponseEntity.badRequest().build();
        }
        adminService.addSubscriptionDays(userId, daysToAdd);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/raffle/draw")
    public ResponseEntity<AdminUserViewDTO> drawWinner() {
        Optional<AdminUserViewDTO> winnerOptional = adminService.drawRandomActiveSubscriber();

        return winnerOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
package com.site.services;

import com.site.dto.AdminDashboardDTO;
import com.site.dto.AdminUserViewDTO;
import com.site.models.Usuario;
import com.site.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UsuarioRepository usuarioRepository;

    public AdminService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public AdminDashboardDTO getDashboardData() {
        long pagantesAtivos = usuarioRepository.countByRoleAndAcessoValidoAteAfter(Usuario.Role.SUBSCRIBER, LocalDateTime.now());
        return new AdminDashboardDTO(pagantesAtivos);
    }

    public List<AdminUserViewDTO> getAllUsersForAdminPanel() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToAdminUserViewDTO)
                .collect(Collectors.toList());
    }

    public void updateUserRole(Long userId, Usuario.Role newRole) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setRole(newRole);
        usuarioRepository.save(usuario);
    }

    public void addSubscriptionDays(Long userId, int daysToAdd) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userId));

        LocalDateTime currentValidDate = usuario.getAcessoValidoAte();
        LocalDateTime newValidDate;

        if (currentValidDate == null || currentValidDate.isBefore(LocalDateTime.now())) {
            newValidDate = LocalDateTime.now().plusDays(daysToAdd);
        } else {
            newValidDate = currentValidDate.plusDays(daysToAdd);
        }

        usuario.setAcessoValidoAte(newValidDate);

        if (usuario.getRole() == Usuario.Role.USER) {
            usuario.setRole(Usuario.Role.SUBSCRIBER);
        }

        usuarioRepository.save(usuario);
    }

    /**
     * Sorteia um apoiador ativo aleatoriamente.
     * @return Um DTO do usuário sorteado, ou um Optional vazio se não houver apoiadores ativos.
     */
    public Optional<AdminUserViewDTO> drawRandomActiveSubscriber() {
        List<Usuario> activeSubscribers = usuarioRepository.findByRoleAndAcessoValidoAteAfter(Usuario.Role.SUBSCRIBER, LocalDateTime.now());

        if (activeSubscribers.isEmpty()) {
            return Optional.empty();
        }

        Random random = new Random();
        Usuario winner = activeSubscribers.get(random.nextInt(activeSubscribers.size()));

        return Optional.of(convertToAdminUserViewDTO(winner));
    }

    private AdminUserViewDTO convertToAdminUserViewDTO(Usuario usuario) {
        Long diasParaVencer = null;
        String status = "";

        if (usuario.getRole() == Usuario.Role.SUBSCRIBER) {
            if (usuario.getAcessoValidoAte() != null && usuario.getAcessoValidoAte().isAfter(LocalDateTime.now())) {
                diasParaVencer = ChronoUnit.DAYS.between(LocalDateTime.now(), usuario.getAcessoValidoAte());
                status = "Ativo";
            } else {
                status = "Vencido";
            }
        } else if (usuario.getRole() == Usuario.Role.USER) {
            status = "Não Assinante";
        } else if (usuario.getRole() == Usuario.Role.ADMIN) {
            status = "Administrador";
        }

        return new AdminUserViewDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                diasParaVencer,
                status
        );
    }
}
package com.site.services;

import com.site.dto.AdminDashboardDTO;
import com.site.dto.AdminUserViewDTO;
import com.site.models.Usuario;
import com.site.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    // NOVO MÉTODO PARA BUSCAR USUÁRIOS COM FILTRO E PESQUISA
    public List<AdminUserViewDTO> findUsersFiltered(String status, String search) {
        List<Usuario> allUsers = usuarioRepository.findAll();

        // Filtra em memória
        return allUsers.stream()
                .map(this::convertToAdminUserViewDTO)
                .filter(userDto -> {
                    boolean statusMatch = status == null || status.isEmpty() || userDto.getStatus().equalsIgnoreCase(status.replace("_", " "));
                    boolean searchMatch = search == null || search.isEmpty() ||
                            userDto.getNome().toLowerCase().contains(search.toLowerCase()) ||
                            userDto.getEmail().toLowerCase().contains(search.toLowerCase());
                    return statusMatch && searchMatch;
                })
                .collect(Collectors.toList());
    }

    public void updateUserRole(Long userId, Usuario.Role newRole) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setRole(newRole);
        usuarioRepository.save(usuario);
    }

    // NOVO MÉTODO PARA DEFINIR A DATA DE VENCIMENTO EXATA
    @Transactional
    public void setSubscriptionDate(Long userId, String newDateStr) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userId));

        LocalDate newDate = LocalDate.parse(newDateStr);
        LocalDateTime newExpirationDateTime = newDate.atTime(LocalTime.MAX);

        usuario.setAcessoValidoAte(newExpirationDateTime);

        if (usuario.getRole() == Usuario.Role.USER) {
            usuario.setRole(Usuario.Role.SUBSCRIBER);
        }

        usuarioRepository.save(usuario);
    }

    // O método 'getAllUsersForAdminPanel' foi removido por ser substituído por 'findUsersFiltered'.
    // O método 'addSubscriptionDays' foi removido por ser substituído por 'setSubscriptionDate'.

    /**
     * Sorteia um apoiador ativo aleatoriamente.
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
        String status = "NUNCA PAGOU";

        if (usuario.getAcessoValidoAte() != null) {
            if (usuario.getAcessoValidoAte().isAfter(LocalDateTime.now())) {
                diasParaVencer = ChronoUnit.DAYS.between(LocalDateTime.now(), usuario.getAcessoValidoAte()) + 1;
                status = "ATIVO";
            } else {
                diasParaVencer = 0L;
                status = "EXPIRADO";
            }
        }

        if(usuario.getRole() == Usuario.Role.ADMIN) {
            status = "ADMINISTRADOR";
        }

        return new AdminUserViewDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getRole(),
                usuario.getAcessoValidoAte(),
                status,
                diasParaVencer
        );
    }
}
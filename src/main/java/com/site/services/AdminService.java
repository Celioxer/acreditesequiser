package com.site.services;

import com.site.dto.AdminDashboardDTO;
import com.site.dto.AdminUserViewDTO;
import com.site.dto.PaymentHistoryDTO;
import com.site.models.PaymentHistory;
import com.site.models.Usuario;
import com.site.repositories.PaymentHistoryRepository;
import com.site.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final PaymentHistoryRepository paymentHistoryRepository;

    public AdminService(UsuarioRepository usuarioRepository, PaymentHistoryRepository paymentHistoryRepository) {
        this.usuarioRepository = usuarioRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
    }

    public AdminDashboardDTO getDashboardData() {
        long pagantesAtivos = usuarioRepository.countByRoleAndAcessoValidoAteAfter(Usuario.Role.SUBSCRIBER, LocalDateTime.now());
        return new AdminDashboardDTO(pagantesAtivos);
    }

    public List<AdminUserViewDTO> findUsersFiltered(String status, String search) {
        List<Usuario> allUsers = usuarioRepository.findAll();

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

    public List<PaymentHistoryDTO> getPaymentHistoryForUser(Long userId) {
        // Esta linha agora vai funcionar porque o método existe no repositório
        List<PaymentHistory> history = paymentHistoryRepository.findByUsuarioIdOrderByPaymentDateDesc(userId);
        return history.stream()
                .map(p -> new PaymentHistoryDTO(
                        p.getPaymentId(),
                        p.getAmount(),
                        p.getPaymentDate(),
                        p.getPaymentMethodId(),
                        p.getInstallments()))
                .collect(Collectors.toList());
    }

    public void updateUserRole(Long userId, Usuario.Role newRole) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setRole(newRole);
        usuarioRepository.save(usuario);
    }

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

        Optional<PaymentHistory> ultimoPagamentoOpt = paymentHistoryRepository.findTopByUsuarioIdOrderByPaymentDateDesc(usuario.getId());
        BigDecimal ultimoPagamentoValor = ultimoPagamentoOpt.map(PaymentHistory::getAmount).orElse(null);
        String ultimoMetodoPagamento = ultimoPagamentoOpt.map(PaymentHistory::getPaymentMethodId).orElse(null);
        long totalPagamentos = paymentHistoryRepository.countByUsuarioId(usuario.getId());

        return new AdminUserViewDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getRole(),
                usuario.getAcessoValidoAte(),
                status,
                diasParaVencer,
                ultimoPagamentoValor,
                ultimoMetodoPagamento,
                totalPagamentos
        );
    }
}
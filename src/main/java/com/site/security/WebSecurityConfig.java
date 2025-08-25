package com.site.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public WebSecurityConfig(AuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. Regras para acesso público (sem login)
                        .requestMatchers("/", "/home", "/register/**", "/login", "/css/**", "/js/**", "/img/**").permitAll()
                        .requestMatchers("/api/mercadopago/webhook").permitAll()
                        .requestMatchers("/api/auth/qr/initiate", "/api/auth/qr/status/**").permitAll()

                        // 2. Regras para páginas de pagamento (qualquer usuário logado)
                        .requestMatchers("/subscription", "/checkout", "/process-card-payment").hasAnyAuthority("ROLE_USER", "ROLE_SUBSCRIBER", "ROLE_ADMIN")

                        // 3. Regras para conteúdo restrito (assinantes ou admins)
                        .requestMatchers("/apoiadores", "/episodios", "/conteudo-protegido").hasAnyAuthority("ROLE_SUBSCRIBER", "ROLE_ADMIN")
                        .requestMatchers("/admin/**", "/api/admin/**").hasAuthority("ROLE_ADMIN")
                        // 4. Regra final: Qualquer outra requisição deve ser feita por um ADMIN
                        .anyRequest().hasAuthority("ROLE_ADMIN")
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/?logout")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        // Desabilita CSRF para a API e o formulário de registro
                        .ignoringRequestMatchers("/register", "/api/**")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
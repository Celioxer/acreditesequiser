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
                        .requestMatchers(
                                "/error", // ✅ GARANTINDO QUE A PÁGINA DE ERRO É SEMPRE PÚBLICA
                                "/", "/home", "/register/**", "/login",
                                "/forgot-password", "/reset-password",
                                "/css/**", "/js/**", "/img/**", "/termos-e-condicoes",
                                "/politica-de-privacidade",
                                "/politica-de-cookies","/sitemap.xml",
                                "/ads.txt"

                        ).permitAll()
                        .requestMatchers("/api/mercadopago/webhook").permitAll()
                        .requestMatchers("/api/auth/qr/initiate", "/api/auth/qr/status/**").permitAll()

                        // 2. Regras para páginas de pagamento (qualquer usuário logado)
                        .requestMatchers("/subscription", "/checkout", "/process-card-payment").hasAnyAuthority("ROLE_USER", "ROLE_SUBSCRIBER", "ROLE_ADMIN")

                        // 3. Regras para conteúdo restrito (assinantes ou admins)
                        .requestMatchers("/episodios", "/conteudo-protegido").hasAnyAuthority("ROLE_SUBSCRIBER", "ROLE_ADMIN")
                        .requestMatchers("/admin/**", "/api/admin/**").hasAuthority("ROLE_ADMIN")

                        // 4. Regra final: Qualquer outra requisição deve ser feita por um usuário autenticado
                        .anyRequest().authenticated()
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
                        // Adicionamos o endpoint de pagamento e /error à lista de exceções do CSRF
                        .ignoringRequestMatchers(
                                "/process-card-payment",
                                "/register",
                                "/api/**",
                                "/forgot-password",
                                "/reset-password",
                                "/error" // ✅ ADICIONADO POR PRÁTICA
                        )
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
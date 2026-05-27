package sv.edu.udb.banco.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            final UserDetailsService userDetailsService,
            final PasswordEncoder passwordEncoder
    ) {
        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                        .requestMatchers("/login", "/error").permitAll()
                        .requestMatchers("/gerencia/**").hasAnyRole("GERENTE_SUCURSAL", "GERENTE_GENERAL")
                        .requestMatchers("/dashboard", "/movimientos", "/transferencias", "/prestamos", "/pagos")
                        .hasAnyRole("CLIENTE", "CAJERO", "DEPENDIENTE", "GERENTE_SUCURSAL", "GERENTE_GENERAL")
                        .requestMatchers("/home", "/configuracion").authenticated()
                        .requestMatchers("/cliente/**").hasRole("CLIENTE")
                        .requestMatchers("/cajero/**").hasRole("CAJERO")
                        .requestMatchers("/dependiente/**").hasRole("DEPENDIENTE")
                        .requestMatchers("/gerencia/sucursal/**").hasRole("GERENTE_SUCURSAL")
                        .requestMatchers("/gerencia/general/**").hasRole("GERENTE_GENERAL")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            final String targetUrl = resolvePostLoginRedirect(authentication);
                            response.sendRedirect(request.getContextPath() + targetUrl);
                        })
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/login?logout=true"));

        return http.build();
    }

    private String resolvePostLoginRedirect(final Authentication authentication) {
    final boolean isGerenteGeneral = authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_GERENTE_GENERAL"));

    if (isGerenteGeneral) {
        return "/gerencia/general/sucursales";
    }

    final boolean isGerenteSucursal = authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_GERENTE_SUCURSAL"));

    if (isGerenteSucursal) {
        return "/gerencia/clientes";
    }

    final boolean isCliente = authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_CLIENTE"));

    if (isCliente) {
        return "/cliente/inicio";
    }

    return "/dashboard";
}
}

package levelup42.trivia.infraestructure.security;

import levelup42.trivia.domain.port.out.PlayerRepositoryPort;
import levelup42.trivia.infraestructure.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final PlayerRepositoryPort playerRepositoryPort;

    public SecurityConfig(@Lazy JwtAuthenticationFilter jwtAuthFilter, PlayerRepositoryPort playerRepositoryPort) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.playerRepositoryPort = playerRepositoryPort;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public Endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/questions/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/players/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/players/**").permitAll() // Allow players to register
                        // Swagger/OpenAPI
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Protected Endpoints
                        .requestMatchers(HttpMethod.POST, "/api/v1/questions").hasRole("ADMIN")
                        // Any other request needs authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> playerRepositoryPort.findByMail(username)
                .map(player -> new CustomUserDetails(
                        new levelup42.trivia.infraestructure.adapter.out.persistence.entity.PlayerEntity(
                                player.getId() != null ? player.getId() : java.util.UUID.randomUUID(),
                                player.getName(),
                                player.getMail(),
                                player.getPassword(),
                                player.getRole(),
                                java.time.Instant.now() // This doesn't strictly matter for UserDetails but is required for the constructor
                        )
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

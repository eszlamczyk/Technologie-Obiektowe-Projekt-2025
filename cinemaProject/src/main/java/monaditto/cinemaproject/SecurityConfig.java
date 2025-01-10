package monaditto.cinemaproject;


import monaditto.cinemaproject.crypto.CustomPasswordEncoder;
import monaditto.cinemaproject.crypto.PasswordHasher;
import monaditto.cinemaproject.user.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;


import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true    // This enables @RolesAllowed
)
public class SecurityConfig {


    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordHasher passwordHasher;

    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService, PasswordHasher passwordHasher) {
        this.customUserDetailsService = customUserDetailsService;
        this.passwordHasher = passwordHasher;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/api/auth/login","/api/registration", "api/admin-panel/users").permitAll()
                .anyRequest().authenticated()
            )
            .csrf(AbstractHttpConfigurer::disable)
            .cors(withDefaults())
            .sessionManagement(sessionManagement -> sessionManagement
                    .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
            )
            .httpBasic(withDefaults());

        http.authenticationManager(authenticationManager(http));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new CustomPasswordEncoder(passwordHasher);
    }
}


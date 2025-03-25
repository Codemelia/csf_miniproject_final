package csf.finalmp.app.server.configs;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// PURPOSE OF THIS CONFIG
// SPRING SECURITY

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${frontend.app.url}")
    private String frontendBaseUrl;

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // permit frontend requests
        // enable CORS to allow frontend requests
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/artiste/**", "/api/artistes/**").authenticated()
                .requestMatchers("/api/spotify/gen-oauth/**", "/api/spotify/linked/**", "/api/spotify/search/**", 
                    "/api/spotify/save-playlist/**", "/api/spotify/save-playlist/**", 
                    "/api/spotify/get-playlist/**").authenticated()
                .requestMatchers("/api/stripe/gen-oauth/**", "/api/stripe/check-access/**").authenticated()
                .requestMatchers("/api/tips/get/**").authenticated()
                .anyRequest().permitAll()) // any other request permitted
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
        
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(frontendBaseUrl)); // frontend URL
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true); // Allow cookies to be sent with requests
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
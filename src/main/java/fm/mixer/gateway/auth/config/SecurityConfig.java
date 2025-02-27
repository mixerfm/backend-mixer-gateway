package fm.mixer.gateway.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "mixer-gateway.security", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SecurityConfig {

    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;
    private final BasicHttpConfig basicHttpConfig;
    private final CorsConfig corsConfig;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> {
            authorize
                .requestMatchers(antPathRequestMatchers("/update-subscription")).hasRole("PAYMENT_CLIENT")
                .requestMatchers(antPathRequestMatchers(
                    "/collections/*/reactions", "/comments/*/reactions", "/mixes/*/reactions", "/tracks/*/reactions",
                    "/users", "PUT /users/*", "DELETE /users/*",
                    "/users/*/follow", "/users/*/unfollow", "/users/*/remove-follower", "/users/*/reactions"
                )).authenticated();
            authorize.anyRequest().permitAll();
        });

        http.oauth2ResourceServer(oauth -> oauth
            .jwt(Customizer.withDefaults())
            .accessDeniedHandler(accessDeniedHandler)
            .authenticationEntryPoint(authenticationEntryPoint)
        );

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors((corsConfigurer) -> corsConfigurer.configurationSource((cs) -> {
            final var configuration = new CorsConfiguration();

            configuration.setAllowedOriginPatterns(corsConfig.getAllowedOrigins());
            configuration.setAllowedMethods(List.of(
                HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.OPTIONS.name(), HttpMethod.TRACE.name(),
                HttpMethod.POST.name(), HttpMethod.PATCH.name(), HttpMethod.PUT.name(), HttpMethod.DELETE.name()
            ));
            configuration.setAllowedHeaders(List.of(CorsConfiguration.ALL));
            configuration.setAllowCredentials(true);

            return configuration;
        }));
        http.httpBasic((httpBasicConfigurer) -> httpBasicConfigurer.authenticationEntryPoint(authenticationEntryPoint));

        return http.build();
    }

    @Bean
    public UserDetailsService basicHttpClients() {
        if (basicHttpConfig.isEnabled()) {
            return new InMemoryUserDetailsManager(basicHttpConfig.getClients().stream()
                .map(client -> User.builder()
                    .username(client.getUsername())
                    .password(passwordEncoder().encode(client.getPassword()))
                    .roles(client.getRoles().toArray(String[]::new))
                    .build()
                )
                .toList());
        }

        return new InMemoryUserDetailsManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static RequestMatcher[] antPathRequestMatchers(String... patterns) {
        return Stream.of(patterns).map(p -> {
            // Developer can define HTTP method in pattern, e.g.: "GET /endpoint"
            // => using hardcoded array positions so errors can easily be detected on first run
            final var pattern = p.split(" ");
            if (pattern.length == 1) {
                return new AntPathRequestMatcher(p);
            }
            return new AntPathRequestMatcher(pattern[1], pattern[0]);
        }).toArray(AntPathRequestMatcher[]::new);
    }
}

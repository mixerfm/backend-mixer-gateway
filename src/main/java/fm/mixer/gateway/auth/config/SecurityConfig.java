package fm.mixer.gateway.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.stream.Stream;

@EnableWebSecurity
public class SecurityConfig {

    @Configuration
    @ConditionalOnProperty(prefix = "mixer-gateway.security", name = "enabled", havingValue = "false")
    public static class DisabledSecurityConfig {

        @Bean
        public SecurityFilterChain configure(HttpSecurity http) throws Exception {
            // allow every requests
            http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());

            http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
            http.csrf(AbstractHttpConfigurer::disable);

            return http.build();
        }
    }

    @Configuration
    @RequiredArgsConstructor
    @ConditionalOnProperty(prefix = "mixer-gateway.security", name = "enabled", havingValue = "true", matchIfMissing = true)
    public static class JwtSecurityConfig {

        private final AuthenticationEntryPoint authenticationEntryPoint;
        private final AccessDeniedHandler accessDeniedHandler;

        @Bean
        public SecurityFilterChain configure(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(authorize -> {
                authorize.requestMatchers(antPathRequestMatchers(
                    "/collections/*/like", "/collections/*/dislike", "/collections/*/report",
                    "/comments/*/like", "/comments/*/dislike", "/comments/*/report",
                    "/mixes/*/like", "/mixes/*/dislike", "/mixes/*/report",
                    "/player/*/like", "/player/*/dislike", "/player/*/recommend",
                    "/player/*/do-not-recommend", "/player/*/report",
                    "/users", "PUT /users/*", "DELETE /users/*",
                    "/users/*/follow", "/users/*/unfollow", "/users/*/remove-follower", "/users/*/report"
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

            return http.build();
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
}

package com.assignment.ecommerce_rookie.security;

import com.assignment.ecommerce_rookie.model.AppRole;
import com.assignment.ecommerce_rookie.security.jwt.AuthEntryPointJwt;
import com.assignment.ecommerce_rookie.security.jwt.AuthTokenFilter;
import com.assignment.ecommerce_rookie.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private static final String[] CATEGORY_PATHS = {"/api/v1/categories/**"};
    private static final String[] PRODUCT_PATHS = {"/api/v1/products/**"};
    private static final String[] USER_PATHS = {"/api/v1/users/**"};
    private static final String[] AUTH_PATHS = {"/api/v1/auth/**"};

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                            configureAuthAccess(auth);
                            configureCategoryAccess(auth);
                            configureProductAccess(auth);
                            configureUserAccess(auth);
                            auth.requestMatchers("/api/v1/product-ratings").hasAuthority(AppRole.USER.name());
                            auth.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/error").permitAll()
                                    .anyRequest().authenticated();
                        }


                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    private void configureCategoryAccess(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, CATEGORY_PATHS).permitAll();
        auth.requestMatchers(HttpMethod.POST, CATEGORY_PATHS).hasAuthority(AppRole.ADMIN.name());
        auth.requestMatchers(HttpMethod.PUT, CATEGORY_PATHS).hasAuthority(AppRole.ADMIN.name());
        auth.requestMatchers(HttpMethod.DELETE, CATEGORY_PATHS).hasAuthority(AppRole.ADMIN.name());
    }

    private void configureProductAccess(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, PRODUCT_PATHS).permitAll();
        auth.requestMatchers(HttpMethod.POST, PRODUCT_PATHS).hasAuthority(AppRole.ADMIN.name());
        auth.requestMatchers(HttpMethod.PUT, PRODUCT_PATHS).hasAuthority(AppRole.ADMIN.name());
        auth.requestMatchers(HttpMethod.DELETE, PRODUCT_PATHS).hasAuthority(AppRole.ADMIN.name());
        auth.requestMatchers(HttpMethod.POST, "/api/v1/images").hasAuthority(AppRole.ADMIN.name());
    }

    private void configureUserAccess(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, USER_PATHS).hasAnyAuthority(AppRole.ADMIN.name(), AppRole.USER.name());
        auth.requestMatchers(HttpMethod.POST, USER_PATHS).hasAuthority(AppRole.ADMIN.name());
        auth.requestMatchers(HttpMethod.PUT, USER_PATHS).hasAnyAuthority(AppRole.ADMIN.name());
        auth.requestMatchers(HttpMethod.DELETE, USER_PATHS).hasAuthority(AppRole.ADMIN.name());
    }

    private void configureAuthAccess(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(AUTH_PATHS).permitAll();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers("/v2/api-docs",
                "/v3/api-docs/**",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"));
    }


}

package com.codestorykh.user.config;

import com.codestorykh.user.config.properties.JwtConfigProperties;
import com.codestorykh.user.filter.CustomAccessDeniedHandler;
import com.codestorykh.user.filter.CustomAuthenticationProvider;
import com.codestorykh.user.filter.JwtAuthenticationFilter;
import com.codestorykh.user.filter.JwtAuthenticationInternalFilter;
import com.codestorykh.user.service.JwtService;
import com.codestorykh.user.service.impl.CustomUserDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class CustomSecurityFilterChain extends JwtConfigProperties {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final CustomUserDetailService customUserDetailService;
    private final CustomAuthenticationProvider customAuthenticationProvider;


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void userAuthenticationGlobalConfig(AuthenticationManagerBuilder authenticationManagerBuilder) {
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        AuthenticationManagerBuilder managerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        managerBuilder.userDetailsService(customUserDetailService).passwordEncoder(passwordEncoder());
        AuthenticationManager authenticationManager = managerBuilder.build();

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "api/public/users/**")
                        .permitAll()
                        .requestMatchers("/swagger-ui/**",
                                "/swagger-resources/*",
                                "/v3/api-docs/**",
                                "loan-api-docs/**")
                        .permitAll()
                        .requestMatchers("/api/users/**")
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers("/api/roles/**")
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .authenticationManager(authenticationManager)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(
                        (exception) -> exception
                                .authenticationEntryPoint(
                                        (((request, response, authException)
                                                -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))))
                                .accessDeniedHandler(new CustomAccessDeniedHandler()))
                .addFilterBefore(
                        new JwtAuthenticationFilter(
                                jwtService, objectMapper, getUrl(), authenticationManager, customUserDetailService),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new JwtAuthenticationInternalFilter(jwtService, objectMapper, this),
                        UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

}
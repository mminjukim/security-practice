package example.security_practice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.security_practice.repository.MemberRepository;
import example.security_practice.repository.RefreshTokenRepository;
import example.security_practice.security.JwtUtil;
import example.security_practice.security.filter.JwtAuthenticationFilter;
import example.security_practice.security.filter.LoginAuthenticationFilter;
import example.security_practice.security.handler.LoginFailHandler;
import example.security_practice.security.handler.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterAfter(loginAuthenticationFilter(), LogoutFilter.class)
                .addFilterBefore(jwtAuthenticationFilter(), LoginAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/signup", "/", "/login").permitAll()
                        .anyRequest().authenticated())
                .logout((logout) -> logout
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, memberRepository, refreshTokenRepository);
    }

    @Bean
    public LoginAuthenticationFilter loginAuthenticationFilter() {
        return new LoginAuthenticationFilter(
                new ObjectMapper(),
                authenticationManager(),
                loginSuccessHandler(),
                loginFailHandler()
        );
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtUtil, refreshTokenRepository);
    }

    @Bean
    public LoginFailHandler loginFailHandler() {
        return new LoginFailHandler();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

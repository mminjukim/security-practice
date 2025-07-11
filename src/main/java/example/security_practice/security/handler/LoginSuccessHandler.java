package example.security_practice.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.security_practice.domain.Member;
import example.security_practice.domain.RefreshToken;
import example.security_practice.repository.RefreshTokenRepository;
import example.security_practice.security.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // 성공 Authentication 에서 이메일 추출해 토큰 생성
        String email = extractEmailFromAuthentication(authentication);
        String accessToken = jwtUtil.createAccessToken(email);
        String refreshToken = jwtUtil.createRefreshToken();

        // RefreshToken 저장
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .email(email)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        // 응답 작성
        jwtUtil.setJwtTokens(response, email, accessToken, refreshToken);
    }

    private String extractEmailFromAuthentication(Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        return member.getEmail();
    }

    private Cookie createCookie(String value) {
        Cookie cookie = new Cookie("RefreshToken", value);
        cookie.setMaxAge(12 * 60 * 60); // 12h
        cookie.setHttpOnly(true);   //JS로 접근 불가, 탈취 위험 감소
        return cookie;
    }
}

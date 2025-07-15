package example.security_practice.security.handler;

import example.security_practice.domain.Member;
import example.security_practice.domain.RefreshToken;
import example.security_practice.repository.RefreshTokenRepository;
import example.security_practice.security.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // 성공 Authentication 에서 이메일 추출
        String email = extractEmailFromAuthentication(authentication);
        // 기존 로그인으로 Refresh 토큰 이미 존재했다면 삭제
        Optional<RefreshToken> refreshEntity = refreshRepository.findByEmail(email);
        refreshEntity.ifPresent(refreshRepository::delete);
        // 응답 작성
        jwtUtil.respondJwtTokens(response, email);
    }

    private String extractEmailFromAuthentication(Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        return member.getEmail();
    }
}

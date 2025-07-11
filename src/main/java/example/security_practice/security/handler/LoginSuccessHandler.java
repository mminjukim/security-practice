package example.security_practice.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.security_practice.domain.Member;
import example.security_practice.domain.RefreshToken;
import example.security_practice.dto.response.LoginSuccessDTO;
import example.security_practice.repository.RefreshTokenRepository;
import example.security_practice.security.JwtUtil;
import jakarta.servlet.ServletException;
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

        // 성공 Authentication 에서 이메일 추출해 토큰 생성, 저장
        String email = extractEmailFromAuthentication(authentication);
        String accessToken = jwtUtil.createAccessToken(email);
        String refreshToken = jwtUtil.createRefreshToken();

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .email(email)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        // 응답 바디 생성
        LoginSuccessDTO dto = LoginSuccessDTO.builder()
                .message("로그인 성공 - 토큰이 성공적으로 발급되었습니다.")
                .email(email)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        String result = objectMapper.writeValueAsString(dto);

        // 응답 객체
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(result);
    }

    private String extractEmailFromAuthentication(Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        return member.getEmail();
    }
}

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

@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        String email = extractEmailFromAuthentication(authentication);
        String accessToken = jwtUtil.createAccessToken(email);
        String refreshToken = jwtUtil.createRefreshToken();

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .email(email)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        //TODO: JWT 전달하는 코드 작성
    }

    private String extractEmailFromAuthentication(Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        return member.getEmail();
    }
}

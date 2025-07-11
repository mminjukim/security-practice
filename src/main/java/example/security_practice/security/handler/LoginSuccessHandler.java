package example.security_practice.security.handler;

import example.security_practice.domain.Member;
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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // 성공 Authentication 에서 이메일 추출
        String email = extractEmailFromAuthentication(authentication);
        // 응답 작성
        jwtUtil.respondJwtTokens(response, email);
    }

    private String extractEmailFromAuthentication(Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        return member.getEmail();
    }
}

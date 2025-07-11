package example.security_practice.security.filter;

import example.security_practice.domain.Member;
import example.security_practice.domain.RefreshToken;
import example.security_practice.exception.CustomException;
import example.security_practice.exception.ErrorCode;
import example.security_practice.repository.MemberRepository;
import example.security_practice.repository.RefreshTokenRepository;
import example.security_practice.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final List<String> NO_CHECK_URLS = new ArrayList<>(Arrays.asList(
            "/login", "/signup"
    ));

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 로그인, 회원가입은 필터 예외
        if (NO_CHECK_URLS.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // AccessToken, RefreshToken이 전달됐는지 확인
        String refreshToken = jwtUtil.extractRefreshToken(request);
        String accessToken = jwtUtil.extractAccessToken(request.getHeader("Authorization"));

        if (!refreshToken.equals("NULL")) {
            // RefreshToken 존재하는 경우
            checkRefreshTokenAndReissue(response, refreshToken);
            return;
        } else {
            // AccessToken만 있는 경우
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authenticateToken(accessToken));
        }

        // 다음 필터로 전달
        filterChain.doFilter(request, response);
    }

    // RefreshToken서 email 가져와 새 AccessToken과 RefreshToken 발급
    private void checkRefreshTokenAndReissue(HttpServletResponse response,
                                             String refreshToken) throws IOException {

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_NOT_FOUND));
        String email = refreshTokenEntity.getEmail();

        // 기존 RefreshToken 삭제
        refreshTokenRepository.delete(refreshTokenEntity);

        // 새 AccessToken과 RefreshToken 발급
        String accessToken = jwtUtil.createAccessToken(email);
        String newRefreshToken = jwtUtil.createRefreshToken();

        // 새 RefreshToken 저장
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .email(email)
                .build();
        refreshTokenRepository.save(newRefreshTokenEntity);

        // 응답 작성
        jwtUtil.setJwtTokens(response, email, accessToken, newRefreshToken);
    }

    // AccessToken 검증 후 Authentication 객체 리턴
    private Authentication authenticateToken(String accessToken) {
        // 사용자 이메일 추출
        String email = jwtUtil.extractEmail(accessToken);
        // 사용자 정보 불러오기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        // Authentication 토큰 전달
        return new UsernamePasswordAuthenticationToken(member, accessToken, member.getAuthorities());
    }
}

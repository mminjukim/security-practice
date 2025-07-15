package example.security_practice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.security_practice.domain.RefreshToken;
import example.security_practice.dto.response.LoginSuccessDTO;
import example.security_practice.exception.CustomException;
import example.security_practice.exception.ErrorCode;
import example.security_practice.repository.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@Transactional
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.expiration}")
    private long accessExpiration;
    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    private Key key;
    private static final String BEARER = "Bearer ";
    private final ObjectMapper objectMapper;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Access Token 생성
    public String createAccessToken(String email) {
        return Jwts.builder()
                .subject("AccessToken")
                .claim("email", email)
                .expiration(new Date(System.currentTimeMillis() + accessExpiration * 1000))
                .signWith(key)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken() {
        return Jwts.builder()
                .subject("RefreshToken")
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration * 1000))
                .signWith(key)
                .compact();
    }

    // 토큰 검증
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // HTTP 요청에서 Access 토큰 추출
    public String extractAccessToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new CustomException(ErrorCode.TOKEN_NULL);
        } else if (!authorizationHeader.startsWith(BEARER)) {
            throw new CustomException(ErrorCode.TOKEN_NOT_BEARER);
        } else {
            return authorizationHeader.substring(BEARER.length());
        }
    }

    public String extractRefreshToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()
                || !authorizationHeader.startsWith(BEARER)) {
            return "NULL";
        } else {
            return authorizationHeader.substring(BEARER.length());
        }
    }

    // 토큰에서 사용자 이메일 추출
    public String extractEmail(String token) {
        if (isTokenValid(token)) {
            return Jwts.parser().verifyWith((SecretKey) key).build()
                    .parseSignedClaims(token).getPayload().get("email").toString();
        } else {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    // 토큰에서 만료시간 추출
    public LocalDateTime extractExpiration(String token) {
        if (isTokenValid(token)) {
            return Jwts.parser().verifyWith((SecretKey) key).build()
                    .parseSignedClaims(token).getPayload().getExpiration()
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    // HTTP 응답에 Refresh, Access 토큰 세팅
    public void respondJwtTokens(HttpServletResponse response, String email) throws IOException {
        // 새 토큰 생성
        String accessToken = createAccessToken(email);
        String refreshToken = createRefreshToken();

        // RefreshToken 저장
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .expiresAt(extractExpiration(refreshToken))
                .email(email)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        // 응답 바디 생성
        LoginSuccessDTO dto = LoginSuccessDTO.of(
                "토큰이 성공적으로 발급되었습니다.",
                email,
                accessToken,
                refreshToken
        );

        // 응답 객체
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(dto));
    }
}

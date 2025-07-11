package example.security_practice.security;

import example.security_practice.exception.CustomException;
import example.security_practice.exception.ErrorCode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Transactional
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.access.expiration}")
    private long accessExpiration;
    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    private static final String BEARER = "Bearer ";
    SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

    // Access Token 생성
    public String createAccessToken(String email) {
        return Jwts.builder()
                .subject("AccessToken")
                .claim("email", email)
                .expiration(new Date(System.currentTimeMillis() + accessExpiration * 1000))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(String email) {
        return Jwts.builder()
                .subject("RefreshToken")
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration * 1000))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    // 토큰 검증
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // HTTP 요청에서 토큰 추출
    public String extractToken(String authorizationHeader) {
        if (authorizationHeader == null) {
            throw new CustomException(ErrorCode.TOKEN_NULL);
        } else if (authorizationHeader.startsWith(BEARER)) {
            return authorizationHeader.substring(BEARER.length());
        } else {
            throw new CustomException(ErrorCode.TOKEN_NOT_BEARER);
        }
    }

    // 토큰에서 사용자 이메일 추출
    public String extractEmail(String token) {
        if (isTokenValid(token)) {
            return Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token).getPayload().get("email").toString();
        } else {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }
}

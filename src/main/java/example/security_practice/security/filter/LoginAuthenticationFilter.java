package example.security_practice.security.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import example.security_practice.exception.CustomException;
import example.security_practice.exception.ErrorCode;
import example.security_practice.security.handler.LoginSuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    // "/login" 경로의 POST 요청에 필터 적용하기 위해 PathPatternRequestMatcher 사용
    public LoginAuthenticationFilter(ObjectMapper objectMapper,
                                     AuthenticationManager authenticationManager,
                                     LoginSuccessHandler loginSuccessHandler) {
        super(PathPatternRequestMatcher
                .withDefaults().matcher(HttpMethod.POST, "/login"));
        this.objectMapper = objectMapper;
        this.setAuthenticationManager(authenticationManager);
        this.setAuthenticationSuccessHandler(loginSuccessHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        // json 로그인만 허용
        if (request.getContentType() == null || !request.getContentType().equals("application/json")) {
            throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);
        }

        // 요청 body 가져오기
        String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        Map<String, String> requestMap = objectMapper.readValue(
                messageBody, new TypeReference<>() {
                });

        // email, password 가져오기
        String email = requestMap.get("email");
        String password = requestMap.get("password");

        // Authentication 객체 세팅
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        try {
            return this.getAuthenticationManager().authenticate(token);
        } catch (AuthenticationException e) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }
    }
}

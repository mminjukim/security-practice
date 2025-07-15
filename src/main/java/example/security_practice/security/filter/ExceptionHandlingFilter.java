package example.security_practice.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.security_practice.exception.CustomException;
import example.security_practice.exception.ErrorResponseEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ExceptionHandlingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    public ExceptionHandlingFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);

        } catch (CustomException e) {
            ErrorResponseEntity error = ErrorResponseEntity.from(e.getErrorCode());
            response.setStatus(error.getHttpStatus());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(error));
        }
    }
}

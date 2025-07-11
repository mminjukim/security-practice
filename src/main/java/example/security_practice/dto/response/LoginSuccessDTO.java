package example.security_practice.dto.response;

import lombok.Builder;

@Builder
public class LoginSuccessDTO {
    private String message;
    private String email;
    private String accessToken;
    private String refreshToken;
}

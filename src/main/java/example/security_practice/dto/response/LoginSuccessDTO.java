package example.security_practice.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginSuccessDTO {
    private String message;
    private String email;
    private String accessToken;
    private String refreshToken;
}

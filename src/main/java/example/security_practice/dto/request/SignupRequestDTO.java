package example.security_practice.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SignupRequestDTO {
    private String email;
    private String password;
    private String confirmPassword;
    private String name;
}

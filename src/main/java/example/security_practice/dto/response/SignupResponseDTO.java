package example.security_practice.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SignupResponseDTO {
    private String message;
    private String email;
    private String name;
    private String role;
}

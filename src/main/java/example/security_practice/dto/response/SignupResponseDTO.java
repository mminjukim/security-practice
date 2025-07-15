package example.security_practice.dto.response;

import example.security_practice.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SignupResponseDTO {
    private String message;
    private String email;
    private String name;
    private String role;

    public static SignupResponseDTO from(Member member) {
        return SignupResponseDTO.builder()
                .message("정상적으로 회원가입되었습니다.")
                .email(member.getEmail())
                .name(member.getName())
                .role(member.getRole().name()).build();
    }
}

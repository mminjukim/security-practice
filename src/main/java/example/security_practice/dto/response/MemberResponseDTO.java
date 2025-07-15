package example.security_practice.dto.response;

import example.security_practice.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberResponseDTO {
    private String message;
    private String email;
    private String name;

    public static MemberResponseDTO from(Member member) {
        return MemberResponseDTO.builder()
                .message("회원이 정상적으로 조회되었습니다.")
                .email(member.getEmail())
                .name(member.getName())
                .build();
    }
}

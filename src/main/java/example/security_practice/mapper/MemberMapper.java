package example.security_practice.mapper;

import example.security_practice.domain.Member;
import example.security_practice.dto.response.SignupResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberMapper {

    public SignupResponseDTO toDto(Member member) {
        return SignupResponseDTO.builder()
                .message("정상적으로 회원가입되었습니다.")
                .email(member.getEmail())
                .name(member.getName())
                .role(member.getRole().name()).build();
    }
}

package example.security_practice.mapper;

import example.security_practice.domain.Member;
import example.security_practice.domain.Role;
import example.security_practice.dto.request.SignupRequestDTO;
import example.security_practice.dto.response.SignupResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberMapper {

    private final PasswordEncoder encoder;

    public Member toEntity(SignupRequestDTO dto) {
        return Member.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .name(dto.getName())
                .role(Role.USER).build();
    }

    public SignupResponseDTO toDto(Member member) {
        return SignupResponseDTO.builder()
                .message("정상적으로 회원가입되었습니다.")
                .email(member.getEmail())
                .name(member.getName())
                .role(member.getRole().name()).build();
    }
}

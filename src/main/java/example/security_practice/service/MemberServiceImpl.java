package example.security_practice.service;

import example.security_practice.domain.Member;
import example.security_practice.domain.Role;
import example.security_practice.dto.request.SignupRequestDTO;
import example.security_practice.dto.response.SignupResponseDTO;
import example.security_practice.exception.CustomException;
import example.security_practice.exception.ErrorCode;
import example.security_practice.mapper.MemberMapper;
import example.security_practice.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper mapper;
    private final PasswordEncoder encoder;


    @Override
    public SignupResponseDTO signup(SignupRequestDTO requestDTO) {
        // 이메일 중복 회원가입 예외
        String email = requestDTO.getEmail();
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new CustomException(ErrorCode.HAS_EMAIL);
        }
        // 비밀번호 재입력 필드 일치하지 않는 예외
        String password = requestDTO.getPassword();
        if (!password.equals(requestDTO.getConfirmPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        // Member 엔티티 생성
        Member member = Member.builder()
                .email(email)
                .password(encoder.encode(password))
                .name(requestDTO.getName())
                .role(Role.USER).build();
        memberRepository.save(member);

        return mapper.toDto(member);
    }
}

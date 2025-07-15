package example.security_practice.service;

import example.security_practice.dto.request.SignupRequestDTO;
import example.security_practice.dto.response.MemberResponseDTO;
import example.security_practice.dto.response.SignupResponseDTO;

public interface MemberService {

    SignupResponseDTO signup(SignupRequestDTO requestDTO);

    MemberResponseDTO getMemberById(Long memberId);
}

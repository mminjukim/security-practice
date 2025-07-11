package example.security_practice.service;

import example.security_practice.dto.request.SignupRequestDTO;
import example.security_practice.dto.response.SignupResponseDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface MemberService extends UserDetailsService {

    SignupResponseDTO signup(SignupRequestDTO requestDTO);
}

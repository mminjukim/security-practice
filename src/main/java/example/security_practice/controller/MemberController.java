package example.security_practice.controller;

import example.security_practice.dto.request.SignupRequestDTO;
import example.security_practice.dto.response.SignupResponseDTO;
import example.security_practice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody SignupRequestDTO dto) {
        SignupResponseDTO result = memberService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}

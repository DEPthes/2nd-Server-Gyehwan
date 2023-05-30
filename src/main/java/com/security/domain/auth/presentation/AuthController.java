package com.security.domain.auth.presentation;

import com.security.domain.auth.application.AuthService;
import com.security.domain.auth.dto.SignInReq;
import com.security.domain.auth.dto.SignUpReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    //회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(
            @Valid @RequestBody SignUpReq signUpReq) {

        return authService.signUp(signUpReq);
    }

    //로그인
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(
            @Valid @RequestBody SignInReq signInReq) {

        return authService.signIn(signInReq);
    }
}

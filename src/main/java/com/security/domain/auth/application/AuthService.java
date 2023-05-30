package com.security.domain.auth.application;

import com.security.domain.auth.domain.Token;
import com.security.domain.auth.domain.repository.TokenRepository;
import com.security.domain.auth.dto.AuthRes;
import com.security.domain.auth.dto.SignInReq;
import com.security.domain.auth.dto.SignUpReq;
import com.security.domain.auth.dto.TokenMapping;
import com.security.domain.user.domain.User;
import com.security.domain.user.domain.repository.UserRepository;
import com.security.global.DefaultAssert;
import com.security.global.payload.ApiResponse;
import com.security.global.payload.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    private final CustomTokenProviderService customTokenProviderService;

    public ResponseEntity<?> signUp(SignUpReq signInReq) {

        User user = User.builder()
                .email(signInReq.getEmail())
                .password(passwordEncoder.encode(signInReq.getPassword()))
                .build();

        userRepository.save(user);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("회원가입에 성공했습니다.").build())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    //로그인
    @Transactional
    public ResponseEntity<?> signIn(SignInReq signInReq){
        Optional<User> user = userRepository.findByEmail(signInReq.getEmail());
        DefaultAssert.isTrue(user.isPresent(), "유저가 올바르지 않습니다.");

        User findUser = user.get();  // get() => Optional로 받은 User객체 꺼내기
        boolean passwordCheck = passwordEncoder.matches(signInReq.getPassword(), findUser.getPassword());
        DefaultAssert.isTrue(passwordCheck, "비밀번호가 일치하지 않습니다.");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInReq.getEmail(),
                        signInReq.getPassword()
                )
        );

        TokenMapping tokenMapping = customTokenProviderService.createToken(authentication);
        Token token = Token.builder()
                .refreshToken(tokenMapping.getRefreshToken())
                .userEmail(tokenMapping.getUserEmail())
                .build();

        tokenRepository.save(token);
        AuthRes authResponse = AuthRes.builder()
                .accessToken(tokenMapping.getAccessToken())
                .refreshToken(tokenMapping.getRefreshToken())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(authResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}

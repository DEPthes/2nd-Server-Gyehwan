package com.security.domain.auth.application;

import com.security.domain.user.domain.User;
import com.security.domain.user.domain.repository.UserRepository;
import com.security.global.DefaultAssert;
import com.security.global.config.security.token.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("유저 정보를 찾을 수 없습니다."));

        return UserPrincipal.create(user);
    }

    public UserDetails loadUserById(Long id){
        Optional<User> user = userRepository.findById(id);
        DefaultAssert.isOptionalPresent(user);

        return  UserPrincipal.create(user.get());
    }

}

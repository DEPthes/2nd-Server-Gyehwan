package com.security.domain.auth.domain.repository;

import com.security.domain.auth.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
}

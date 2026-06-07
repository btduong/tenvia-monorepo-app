package com.tenvia.common.security;

import com.tenvia.common.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class JwtUtilTest {

    private static final String JWT_SECRET = "GUGQ6oZtM0cplvre0Kwhe4eqMKUwhHWXjP6LDw3fZ5u";
    private static final long JWT_EXPIRATION = 10_000L;
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(JWT_SECRET, JWT_EXPIRATION);
    }

    @Test
    void canGenerateToken_ROLE_USER() {
        String jwt = jwtUtil.generateToken(123L, UserRole.ROLE_USER);
        assertThat(jwtUtil.isValidToken(jwt)).isTrue();
        assertThat(jwtUtil.extractUsername(jwt)).isEqualTo("123");
        assertThat(jwtUtil.extractRole(jwt)).isEqualTo(UserRole.ROLE_USER);
    }

    @Test
    void canGenerateToken_ROLE_ADMIN() {
        String jwt = jwtUtil.generateToken(123L, UserRole.ROLE_ADMIN);
        assertThat(jwtUtil.isValidToken(jwt)).isTrue();
        assertThat(jwtUtil.extractUsername(jwt)).isEqualTo("123");
        assertThat(jwtUtil.extractRole(jwt)).isEqualTo(UserRole.ROLE_ADMIN);
    }

    @Test
    void expectInvalidToken() {
        String invalidJwtToken = "123.xyz";
        assertThat(jwtUtil.isValidToken(invalidJwtToken)).isFalse();
    }

}
package com.tenvia.user.controller;

import com.tenvia.security.JwtUtil;
import com.tenvia.user.dto.LoginDTO;
import com.tenvia.user.dto.UserDTO;
import com.tenvia.user.entities.UserEntity;
import com.tenvia.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginDTO> login(@RequestParam String username) {
        if (username == null || username.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        UserEntity userEntity = userService.login(username);
        String jwt = jwtUtil.generateToken(userEntity.getId(), userEntity.getRole());
        LoginDTO loginDTO = new LoginDTO(UserDTO.from(userEntity), jwt);
        return ResponseEntity.ok(loginDTO);
    }
}

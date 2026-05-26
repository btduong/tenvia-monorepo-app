package com.tenvia.user.controller;

import com.tenvia.user.dto.UserDTO;
import com.tenvia.user.entities.UserEntity;
import com.tenvia.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestParam String username) {
        if (username == null || username.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        UserEntity userEntity = userService.login(username);
        return ResponseEntity.ok(UserDTO.from(userEntity));
    }
}

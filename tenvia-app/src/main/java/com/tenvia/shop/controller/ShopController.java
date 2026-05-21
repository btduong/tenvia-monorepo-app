package com.tenvia.shop.controller;

import com.tenvia.shop.PowerUpType;
import com.tenvia.user.dto.UserDTO;
import com.tenvia.user.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private UserService userService;

    @PostMapping("/buy")
    public ResponseEntity<UserDTO> buyItem(@RequestParam Long userId, @RequestParam PowerUpType type) {

        log.debug("Buy {} request from:{}", type, userId);

        UserDTO userDTO = userService.addItem(userId, type, 1);

        return ResponseEntity.ok(userDTO);
    }
}

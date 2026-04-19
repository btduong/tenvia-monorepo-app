package com.tenvia.controller;

import com.tenvia.PowerUpType;
import com.tenvia.dto.UserDTO;
import com.tenvia.services.InventoryService;
import com.tenvia.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shop")
public class ShopController {

    private final Logger LOG = LoggerFactory.getLogger(ShopController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/buy")
    public ResponseEntity<UserDTO> buyItem(@RequestParam Long userId, @RequestParam PowerUpType type) {

        LOG.debug("Buy {} request from:{}", type, userId);

        int price = 0; // Make it free for now.
        userService.updateBalance(userId, -price);

        inventoryService.addItem(userId, type, 1);
        UserDTO userDTO = userService.getUserById(userId);

        return ResponseEntity.ok(userDTO);
    }
}

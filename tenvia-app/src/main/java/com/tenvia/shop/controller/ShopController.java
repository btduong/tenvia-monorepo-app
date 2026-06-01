package com.tenvia.shop.controller;

import com.tenvia.session.services.GameSessionService;
import com.tenvia.shop.PowerUpType;
import com.tenvia.shop.dto.CatalogItemDTO;
import com.tenvia.user.dto.UserDTO;
import com.tenvia.user.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {

    private final UserService userService;
    private final GameSessionService gameSessionService;

    @PostMapping("/buy")
    public ResponseEntity<UserDTO> buyItem(@AuthenticationPrincipal String userIdString, @RequestParam UUID sessionId, @RequestParam PowerUpType type) {

        Long userId = Long.valueOf(userIdString);
        gameSessionService.verifySessionIdOwner(sessionId, userId);

        log.debug("Buy {} request from:{}", type, userId);

        UserDTO userDTO = userService.addItem(userId, type, 1);

        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/catalog")
    public ResponseEntity<List<CatalogItemDTO>> getAllItemDescriptions() {
        List<CatalogItemDTO> catalogItemDTOList = Arrays.stream(PowerUpType.values())
                .map(type -> new CatalogItemDTO(type, type.getDisplayName(), type.getDescription()))
                .toList();
        return ResponseEntity.ok(catalogItemDTOList);
    }
}

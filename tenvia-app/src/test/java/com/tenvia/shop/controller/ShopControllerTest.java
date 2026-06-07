package com.tenvia.shop.controller;

import com.tenvia.common.UserRole;
import com.tenvia.common.security.JwtUtil;
import com.tenvia.session.services.GameSessionService;
import com.tenvia.shop.PowerUpType;
import com.tenvia.shop.dto.CatalogItemDTO;
import com.tenvia.user.dto.UserDTO;
import com.tenvia.user.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.tenvia.shop.PowerUpType.FIFTY_FIFTY;
import static com.tenvia.shop.PowerUpType.HAMMER;
import static com.tenvia.shop.PowerUpType.SWAP_QUESTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class ShopControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private GameSessionService gameSessionService;

    private String token;
    private UUID sessionId;

    @BeforeEach
    void setUp() {
        token = jwtUtil.generateToken(1L, UserRole.ROLE_USER);
        sessionId = UUID.randomUUID();
    }

    @Test
    void canBuyItem() throws Exception {
        UserDTO user = new UserDTO(1L, "alice", LocalDateTime.now(), 0, new HashMap<>());
        when(userService.addItem(1L, FIFTY_FIFTY, 1)).thenReturn(user);
        String responseContent = mockMvc.perform(post("/shop/buy")
                        .header("Authorization", "Bearer " + token)
                        .param("useIdString", String.valueOf(1L))
                        .param("sessionId", sessionId.toString())
                        .param("type", FIFTY_FIFTY.toString()))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDTO userDTO = objectMapper.readValue(responseContent, UserDTO.class);
        assertThat(userDTO.username()).isEqualTo("alice");
        assertThat(userDTO.id()).isEqualTo(1L);
    }

    @Test
    void canGetItemCatalog() throws Exception {

        String responseContent = mockMvc.perform(get("/shop/catalog")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<CatalogItemDTO> catalogItemDTOList = objectMapper.readValue(responseContent, new TypeReference<>() {
        });

        assertThat(catalogItemDTOList.size()).isEqualTo(3);
        assertThat(catalogItemDTOList.contains(new CatalogItemDTO(HAMMER, HAMMER.getDisplayName(), HAMMER.getDescription())));
        assertThat(catalogItemDTOList.contains(new CatalogItemDTO(FIFTY_FIFTY, FIFTY_FIFTY.getDisplayName(), FIFTY_FIFTY.getDescription())));
        assertThat(catalogItemDTOList.contains(new CatalogItemDTO(SWAP_QUESTION, SWAP_QUESTION.getDisplayName(), SWAP_QUESTION.getDescription())));
    }

}
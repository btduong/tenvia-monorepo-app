package com.tenvia.user;


import com.tenvia.common.UserRole;
import com.tenvia.security.JwtUtil;
import com.tenvia.user.dto.LoginDTO;
import com.tenvia.user.dto.UserDTO;
import com.tenvia.user.entities.UserEntity;
import com.tenvia.user.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    @Test
    void canLogin() throws Exception {
        String username = "alice";
        String token = jwtUtil.generateToken(1L, UserRole.ROLE_USER);
        UserEntity userEntity = new UserEntity(username);
        ReflectionTestUtils.setField(userEntity, "id", 1L);
        when(userService.login(username)).thenReturn(userEntity);

        String responseData = mockMvc.perform(post("/users/login")
                        .header("Authorization", "Bearer " + token)
                        .param("username", username))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        LoginDTO loginDTO = objectMapper.readValue(responseData, LoginDTO.class);
        assertThat(loginDTO.userDTO().username()).isEqualTo("alice");
        assertThat(loginDTO.userDTO().balance()).isEqualTo(0);
    }

    @Test
    void expect400_whenLogin_userNameIsNull() throws Exception {
        mockMvc.perform(post("/users/login"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void expect400_whenLogin_usernameIsBlank() throws Exception {
        String blankUsername = " ";
        mockMvc.perform(post("/users/login/")
                        .param("username", blankUsername))
                .andExpect(status().is4xxClientError());
    }
}
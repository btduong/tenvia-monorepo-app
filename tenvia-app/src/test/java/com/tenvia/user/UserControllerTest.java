package com.tenvia.user;


import com.tenvia.user.dto.UserDTO;
import com.tenvia.user.entities.UserEntity;
import com.tenvia.user.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    @Test
    void canLogin() throws Exception {
        String username = "alice";
        UserEntity userEntity = new UserEntity(username);
        when(userService.login(username)).thenReturn(userEntity);

        String responseData = mockMvc.perform(post("/users/login")
                        .param("username", username))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDTO userResponse = objectMapper.readValue(responseData, UserDTO.class);
        assertThat(userResponse.username()).isEqualTo("alice");
        assertThat(userResponse.balance()).isEqualTo(0);
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
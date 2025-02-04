package org.elis.progettoing.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elis.progettoing.dto.TokenDTO;
import org.elis.progettoing.dto.request.user.UserLoginRequest;
import org.elis.progettoing.dto.request.user.UserRequestDTO;
import org.elis.progettoing.security.JwtService;
import org.elis.progettoing.service.implementation.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthServiceImpl authService;

    @MockBean
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testRegisterAdmin() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setNickname("validNickname");
        userRequestDTO.setPassword("validPassword");
        userRequestDTO.setEmail("validEmail@example.com");
        userRequestDTO.setSurname("validSurname");
        userRequestDTO.setName("validName");
        TokenDTO tokenDTO = new TokenDTO("registerToken");

        when(authService.registerAdmin(any(UserRequestDTO.class))).thenReturn(tokenDTO);

        mockMvc.perform(post("/api/auth/registerAdmin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("registerToken"));
    }

    @Test
    void testRegisterBuyer() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setNickname("validNickname");
        userRequestDTO.setPassword("validPassword");
        userRequestDTO.setEmail("validEmail@example.com");
        userRequestDTO.setSurname("validSurname");
        userRequestDTO.setName("validName");
        TokenDTO tokenDTO = new TokenDTO("registerToken");

        when(authService.registerBuyer(any(UserRequestDTO.class))).thenReturn(tokenDTO);

        mockMvc.perform(post("/api/auth/registerBuyer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("registerToken"));
    }


    @Test
    void testRegisterModerator() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setNickname("validNickname");
        userRequestDTO.setPassword("validPassword");
        userRequestDTO.setEmail("validEmail@example.com");
        userRequestDTO.setSurname("validSurname");
        userRequestDTO.setName("validName");
        TokenDTO tokenDTO = new TokenDTO("registerToken");

        when(authService.registerModerator(any(UserRequestDTO.class))).thenReturn(tokenDTO);

        mockMvc.perform(post("/api/auth/registerModerator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.token").value("registerToken"));
        }

    @Test
    void testLogin() throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail("user@example.com");
        userLoginRequest.setPassword("password");
        TokenDTO tokenDTO = new TokenDTO("loginToken");

        when(authService.login(any(UserLoginRequest.class))).thenReturn(tokenDTO);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("loginToken"));
    }
}
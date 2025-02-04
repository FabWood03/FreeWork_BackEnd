package org.elis.progettoing.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.elis.progettoing.dto.request.user.UserEditRequest;
import org.elis.progettoing.dto.response.user.UserResponseDTO;
import org.elis.progettoing.service.definition.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testGetAllUsers() throws Exception {
        UserResponseDTO user1 = new UserResponseDTO(1L, "Fabio", "Bosco", "FabioBosco@example.com");
        UserResponseDTO user2 = new UserResponseDTO(2L, "Jane", "Smith", "janesmith@example.com");
        List<UserResponseDTO> users = List.of(user1, user2);

        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/user/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void testGetUserById() throws Exception {
        UserResponseDTO user = new UserResponseDTO(1L, "Fabio", "Bosco", "FabioBosco@example.com");

        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/user/getById")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Fabio"))
                .andExpect(jsonPath("$.email").value("FabioBosco@example.com"));
    }

    @Test
    void testGetUserByEmail() throws Exception {
        UserResponseDTO user = new UserResponseDTO(1L, "Fabio", "Bosco", "FabioBosco@example.com");

        when(userService.findByEmail("FabioBosco@example.com")).thenReturn(user);

        mockMvc.perform(get("/api/user/getByEmail")
                        .param("email", "FabioBosco@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("FabioBosco@example.com"));
    }

    @Test
    void testEnableUser() throws Exception {
        UserResponseDTO user = new UserResponseDTO(1L, "Fabio", "Bosco", "FabioBosco@example.com");
        user.setActive(true);

        when(userService.enableById(1L)).thenReturn(user);

        mockMvc.perform(patch("/api/user/enable")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testDisableUser() throws Exception {
        UserResponseDTO user = new UserResponseDTO(1L, "Fabio", "Bosco", "FabioBosco@example.com");
        user.setActive(false);

        when(userService.disableById(1L)).thenReturn(user);

        mockMvc.perform(patch("/api/user/disable")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void testRemoveUser() throws Exception {
        when(userService.remove(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/user/delete")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void testUpdateUser() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        UserEditRequest userRequestDTO = new UserEditRequest();
        userRequestDTO.setName("Fabio");
        userRequestDTO.setSurname("Bosco");
        userRequestDTO.setNickname("FabioBosco");
        userRequestDTO.setBirthDate(LocalDate.parse("1990-01-01"));
        userRequestDTO.setEducation("Bachelor's Degree");
        userRequestDTO.setSkills(List.of("Java", "Spring")); // Add valid skills
        userRequestDTO.setLanguages(List.of("English", "Italian")); // Add valid languages

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setName("Fabio");
        userResponseDTO.setSurname("Bosco");
        userResponseDTO.setNickname("FabioBosco");
        userResponseDTO.setEmail("FabioBosco@example.com");
        userResponseDTO.setRole("USER");
        userResponseDTO.setActive(true);
        userResponseDTO.setEducation("Bachelor's Degree");

        MockMultipartFile userPhoto = new MockMultipartFile("userPhoto", "userphoto.jpg", "image/jpeg", "photoContent".getBytes());
        MockMultipartFile portfolioFile = new MockMultipartFile("portfolio", "portfolio.pdf", "application/pdf", "portfolioContent".getBytes());

        when(userService.update(any(UserEditRequest.class), any())).thenReturn(userResponseDTO);

        String userRequestJson = objectMapper.writeValueAsString(userRequestDTO);

        MockMultipartFile userRequestDTOFile = new MockMultipartFile(
                "userData",
                "userData",
                "application/json",
                userRequestJson.getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/user/update")
                        .file(userPhoto)
                        .file(portfolioFile)
                        .file(userRequestDTOFile)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Fabio"))
                .andExpect(jsonPath("$.surname").value("Bosco"))
                .andExpect(jsonPath("$.education").value("Bachelor's Degree"));

        verify(userService).update(userRequestDTO, userPhoto);
    }

    @Test
    void updateUserRole_UserExists_ReturnsUpdatedUser() throws Exception {
        long userId = 1L;
        String newRole = "ADMIN";
        UserResponseDTO updatedUser = new UserResponseDTO();
        updatedUser.setId(userId);
        updatedUser.setRole(newRole);

        when(userService.updateUserRole(userId, newRole)).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/user/updateRole")
                        .param("id", String.valueOf(userId))
                        .param("role", newRole))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.role").value(newRole));
    }

    @Test
    void getUsersFiltered_ReturnsFilteredUsers_WhenUsersExist() throws Exception {
        UserResponseDTO user1 = new UserResponseDTO(1L, "Fabio", "Bosco", "FabioBosco@example.com");
        UserResponseDTO user2 = new UserResponseDTO(2L, "Jane", "Smith", "janesmith@example.com");
        List<UserResponseDTO> users = List.of(user1, user2);

        when(userService.getAllUserFiltered("Fabio")).thenReturn(users);

        mockMvc.perform(post("/api/user/getUsersFiltered")
                        .param("search", "Fabio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }
}


package org.elis.progettoing.mapper;

import org.elis.progettoing.dto.request.user.UserRequestDTO;
import org.elis.progettoing.dto.response.user.UserResponseDTO;
import org.elis.progettoing.enumeration.Role;
import org.elis.progettoing.mapper.implementation.UserMapperImpl;
import org.elis.progettoing.models.Cart;
import org.elis.progettoing.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperImplTest {

    private UserMapperImpl userMapperImpl;

    private UserRequestDTO userRequestDTO;
    private User user;

    @BeforeEach
    void setUp() {
        userMapperImpl = new UserMapperImpl();

        // Initialize UserRequestDTO
        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("Fabio");
        userRequestDTO.setSurname("Bosco");
        userRequestDTO.setNickname("FabioBosco");
        userRequestDTO.setEmail("FabioBosco@example.com");
        userRequestDTO.setPassword("12345678");
        userRequestDTO.setRole("BUYER");
        userRequestDTO.setBirthDate(LocalDate.of(2003, 12, 13));
        userRequestDTO.setEducation("This is my education.");
        userRequestDTO.setActive(true);
        userRequestDTO.setRanking(100);
        userRequestDTO.setFiscalCode("ABC123XYZ");
        userRequestDTO.setCartId(1L);

        // Initialize User entity
        user = new User();
        user.setName("Fabio");
        user.setSurname("Bosco");
        user.setNickname("FabioBosco");
        user.setEmail("FabioBosco@example.com");
        user.setPassword("12345678");
        user.setRole(Role.BUYER);
        user.setBirthDate(LocalDate.of(2003, 12, 13));
        user.setEducation("This is my education.");
        user.setActive(true);
        user.setRanking(100);
        user.setFiscalCode("ABC123XYZ");

        Cart cart = new Cart();
        cart.setId(1L);
        user.setCart(cart);
    }

    @Test
    void testUserRequestDTOToUser_withNonNullDTO() {
        // When
        User result = userMapperImpl.userRequestDTOToUser(userRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals("Fabio", result.getName());
        assertEquals("Bosco", result.getSurname());
        assertEquals("FabioBosco", result.getNickname());
        assertEquals("FabioBosco@example.com", result.getEmail());
        assertEquals("12345678", result.getPassword());
        assertEquals(Role.BUYER, result.getRole());
        assertEquals(LocalDate.of(2003, 12, 13), result.getBirthDate());
        assertEquals("This is my education.", result.getEducation());
        assertTrue(result.isActive());
        assertEquals(100, result.getRanking());
        assertEquals("ABC123XYZ", result.getFiscalCode());
        assertNotNull(result.getCart());
        assertEquals(1L, result.getCart().getId());
    }

    @Test
    void testUserRequestDTOToUser_withNullDTO() {
        // When
        User result = userMapperImpl.userRequestDTOToUser(null);

        // Then
        assertNull(result);
    }

    @Test
    void testUserToUserResponseDTO_withNonNullUser() {
        // When
        UserResponseDTO result = userMapperImpl.userToUserResponseDTO(user);

        // Then
        assertNotNull(result);
        assertEquals("Fabio", result.getName());
        assertEquals("Bosco", result.getSurname());
        assertEquals("FabioBosco", result.getNickname());
        assertEquals("FabioBosco@example.com", result.getEmail());
        assertEquals("BUYER", result.getRole());
        assertEquals(LocalDate.of(2003, 12, 13), result.getBirthDate());
        assertEquals("ABC123XYZ", result.getFiscalCode());
        assertTrue(result.isActive());
        assertEquals(100, result.getRating());
        assertEquals(1L, result.getCartId());
    }

    @Test
    void testUserToUserResponseDTO_withNullUser() {
        // When
        UserResponseDTO result = userMapperImpl.userToUserResponseDTO(null);

        // Then
        assertNull(result);
    }

    @Test
    void testUserToUserResponseDTO_withRoleNotNull() {
        // When
        UserResponseDTO result = userMapperImpl.userToUserResponseDTO(user);

        // Then
        assertNotNull(result);
        assertEquals("BUYER", result.getRole());  // Verify the role is correctly set in the DTO
    }


    @Test
    void testUsersToUserResponseDTOs_withNonNullUsers() {
        // Given
        User user2 = new User();
        user2.setName("Fabio");
        user2.setSurname("Bosco");
        user2.setNickname("FabioBosco");
        user2.setEmail("FabioBosco@example.com");
        user2.setRole(Role.BUYER);
        user2.setBirthDate(LocalDate.of(2003, 12, 13));
        user2.setEducation("Education of Fabio");
        user2.setActive(true);
        user2.setRanking(150);
        user2.setFiscalCode("XYZ123ABC");

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);

        // When
        List<UserResponseDTO> result = userMapperImpl.usersToUserDTOs(users);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Fabio", result.get(0).getName());
        assertEquals("Fabio", result.get(1).getName());
    }

    @Test
    void testUsersToUserResponseDTOs_withNullUsers() {
        // When
        List<UserResponseDTO> result = userMapperImpl.usersToUserDTOs(null);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testUserRequestDTOToCart_withNonNullDTO() {
        // When
        Cart result = userMapperImpl.userRequestDTOToCart(userRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testUserRequestDTOToCart_withNullDTO() {
        // When
        Cart result = userMapperImpl.userRequestDTOToCart(null);

        // Then
        assertNull(result);
    }

    @Test
    void testUserCartId_withNullUser() {
        // When
        long result = userMapperImpl.userCartId(null);

        // Then
        assertEquals(0L, result);
    }

}


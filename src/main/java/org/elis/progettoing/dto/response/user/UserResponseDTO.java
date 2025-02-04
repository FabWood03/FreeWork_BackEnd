package org.elis.progettoing.dto.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object (DTO) that represents a response containing the details of a user.
 * This class provides the user's information such as personal details, role, and rating.
 *
 * <p>This class is used to convey the details of a user in response to a request for user information.</p>
 */
@Data
public class UserResponseDTO {

    private long id;

    private String name;

    private String surname;

    private String nickname;

    private String email;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthDate;

    private String fiscalCode;

    private String role;

    private boolean active;

    private long cartId;

    private String imageFolderUrl;

    private List<String> portfolio;

    private String education;

    private List<String> skills;

    private String bio;

    private double rating;

    private String basedIn;

    private List<String> languages;

    /**
     * Constructs a new {@link UserResponseDTO} with the specified user ID, name, surname, and email.
     *
     * @param id    The unique identifier of the user.
     * @param name  The first name of the user.
     * @param surname The last name of the user.
     * @param email The email address of the user.
     */
    public UserResponseDTO(long id, String name, String surname, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    /**
     * Default constructor for creating an empty {@link UserResponseDTO}.
     */
    public UserResponseDTO() {}

    /**
     * Constructs a new {@link UserResponseDTO} with the specified user ID and name.
     *
     * @param id   The unique identifier of the user.
     * @param name The first name of the user.
     */
    public UserResponseDTO(long id, String name) {
        this.id = id;
        this.name = name;
    }
}

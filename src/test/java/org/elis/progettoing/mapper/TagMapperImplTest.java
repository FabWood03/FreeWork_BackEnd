package org.elis.progettoing.mapper;

import org.elis.progettoing.dto.request.product.TagDTO;
import org.elis.progettoing.dto.response.product.TagResponseDTO;
import org.elis.progettoing.mapper.implementation.TagMapperImpl;
import org.elis.progettoing.models.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TagMapperImplTest {

    private TagMapperImpl tagMapperImpl;

    @BeforeEach
    void setUp() {
        tagMapperImpl = new TagMapperImpl();
    }

    @Test
    void testTagDTOToTag_withNonNullDTO() {
        // Arrange
        TagDTO tagDTO = new TagDTO();
        tagDTO.setName("Test Tag");

        // Act
        Tag result = tagMapperImpl.tagDTOToTag(tagDTO);

        // Assert
        assertNotNull(result, "The result should not be null.");
        assertEquals("Test Tag", result.getName(), "The tag name should be 'Test Tag'.");
    }

    @Test
    void testTagDTOToTag_withNullDTO() {
        // Act
        Tag result = tagMapperImpl.tagDTOToTag(null);

        // Assert
        assertNull(result, "The result should be null when the input DTO is null.");
    }

    @Test
    void testTagToTagResponseDTO_withNonNullTag() {
        // Arrange
        Tag tag = new Tag("tag1");
        tag.setName("Test Tag");

        // Act
        TagResponseDTO result = tagMapperImpl.tagToTagResponseDTO(tag);

        // Assert
        assertNotNull(result, "The result should not be null.");
        assertEquals("Test Tag", result.getName(), "The response DTO name should be 'Test Tag'.");
    }

    @Test
    void testTagToTagResponseDTO_withNullTag() {
        // Act
        TagResponseDTO result = tagMapperImpl.tagToTagResponseDTO(null);

        // Assert
        assertNull(result, "The result should be null when the input tag is null.");
    }

    @Test
    void testTagToTagListResponseDTO_withNonNullList() {
        // Arrange
        Tag tag1 = new Tag("tag1");
        tag1.setName("Tag1");

        Tag tag2 = new Tag("tag1");
        tag2.setName("Tag2");

        List<Tag> tags = List.of(tag1, tag2);

        // Act
        List<TagResponseDTO> result = tagMapperImpl.tagToTagListResponseDTO(tags);

        // Assert
        assertNotNull(result, "The result should not be null.");
        assertEquals(2, result.size(), "The list size should be 2.");
        assertEquals("Tag1", result.get(0).getName(), "The first tag name should be 'Tag1'.");
        assertEquals("Tag2", result.get(1).getName(), "The second tag name should be 'Tag2'.");
    }

    @Test
    void testTagToTagListResponseDTO_withNullList() {
        // Act
        List<TagResponseDTO> result = tagMapperImpl.tagToTagListResponseDTO(null);

        // Assert
        assertNotNull(result, "The result should not be null.");
        assertEquals(0, result.size(), "The list size should be 0.");
    }

    @Test
    void testTagDTOListToTagList_withNonNullList() {
        // Arrange
        TagDTO tagDTO1 = new TagDTO();
        tagDTO1.setName("Tag1");

        TagDTO tagDTO2 = new TagDTO();
        tagDTO2.setName("Tag2");

        List<TagDTO> tagDTOs = List.of(tagDTO1, tagDTO2);

        // Act
        List<Tag> result = tagMapperImpl.tagDTOListToTagList(tagDTOs);

        // Assert
        assertNotNull(result, "The result should not be null.");
        assertEquals(2, result.size(), "The list size should be 2.");
        assertEquals("Tag1", result.get(0).getName(), "The first tag name should be 'Tag1'.");
        assertEquals("Tag2", result.get(1).getName(), "The second tag name should be 'Tag2'.");
    }

    @Test
    void testTagDTOListToTagList_withNullList() {
        // Act
        List<Tag> result = tagMapperImpl.tagDTOListToTagList(null);

        // Assert
        assertNotNull(result, "The result should not be null.");
        assertEquals(0, result.size(), "The list size should be 0.");
    }
}


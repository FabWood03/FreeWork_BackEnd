package org.elis.progettoing.mapper.definition;

import org.elis.progettoing.dto.response.product.TagResponseDTO;
import org.elis.progettoing.models.Tag;

import java.util.List;

/**
 * Interface for mapping between Tag entities and their respective DTOs.
 * This interface defines methods for converting a Tag entity to a TagResponseDTO,
 * and converting lists of Tag entities to lists of TagResponseDTOs.
 */
public interface TagMapper {

    /**
     * Converts a Tag entity to a TagResponseDTO.
     *
     * @param tag the Tag entity to be converted
     * @return the TagResponseDTO populated with data from the Tag entity
     */
    TagResponseDTO tagToTagResponseDTO(Tag tag);

    /**
     * Converts a list of Tag entities to a list of TagResponseDTOs.
     *
     * @param tag the list of Tag entities to be converted
     * @return the list of TagResponseDTOs populated with data from the Tag entities
     */
    List<TagResponseDTO> tagToTagListResponseDTO(List<Tag> tag);
}

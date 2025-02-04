package org.elis.progettoing.mapper.implementation;

import org.elis.progettoing.dto.request.product.TagDTO;
import org.elis.progettoing.dto.response.product.TagResponseDTO;
import org.elis.progettoing.mapper.definition.TagMapper;
import org.elis.progettoing.models.Tag;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the TagMapper interface. Provides methods for mapping between
 * Tag-related response DTOs and entity models.
 */
@Component
public class TagMapperImpl implements TagMapper {
    /**
     * Converts a TagDTO to a Tag entity.
     *
     * @param tagDTO the TagDTO to be converted
     * @return a Tag entity populated with data from the TagDTO, or null if the tagDTO is null
     */
    public Tag tagDTOToTag(TagDTO tagDTO) {
        if (tagDTO == null) {
            return null;
        }

        Tag tag = new Tag("tag1");

        tag.setName(tagDTO.getName());

        return tag;
    }

    /**
     * Converts a Tag entity to a TagResponseDTO.
     *
     * @param tag the Tag entity to be converted
     * @return a TagResponseDTO populated with data from the Tag entity, or null if the tag is null
     */
    @Override
    public TagResponseDTO tagToTagResponseDTO(Tag tag) {
        if (tag == null) {
            return null;
        }

        TagResponseDTO tagResponseDTO = new TagResponseDTO();

        tagResponseDTO.setName(tag.getName());

        return tagResponseDTO;
    }

    /**
     * Converts a list of Tag entities to a list of TagResponseDTOs.
     *
     * @param tag the list of Tag entities to be converted
     * @return a list of TagResponseDTOs, or an empty list if the input list is null
     */
    @Override
    public List<TagResponseDTO> tagToTagListResponseDTO(List<Tag> tag) {
        if (tag == null) {
            return Collections.emptyList();
        }

        return tag.stream().map(this::tagToTagResponseDTO).toList();
    }

    /**
     * Converts a list of TagDTOs to a list of Tag entities.
     *
     * @param list the list of TagDTOs to be converted
     * @return a list of Tag entities populated with data from the TagDTOs
     */
    public List<Tag> tagDTOListToTagList(List<TagDTO> list) {
        if (list == null) {
            return Collections.emptyList();
        }

        List<Tag> list1 = new ArrayList<>(list.size());
        for (TagDTO tagDTO : list) {
            list1.add(tagDTOToTag(tagDTO));
        }

        return list1;
    }
}

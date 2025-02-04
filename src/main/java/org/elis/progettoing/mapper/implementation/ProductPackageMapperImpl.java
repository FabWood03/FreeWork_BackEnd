package org.elis.progettoing.mapper.implementation;

import org.elis.progettoing.dto.request.product.DynamicAttributeDTO;
import org.elis.progettoing.dto.request.product.ProductPackageRequestDTO;
import org.elis.progettoing.dto.response.product.PackageAttributeResponseDTO;
import org.elis.progettoing.dto.response.product.ProductPackageResponseDTO;
import org.elis.progettoing.enumeration.PackageType;
import org.elis.progettoing.mapper.definition.ProductPackageMapper;
import org.elis.progettoing.models.product.PackageAttribute;
import org.elis.progettoing.models.product.ProductPackage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the ProductPackageMapper interface. Provides methods for mapping between
 * ProductPackage-related request and response DTOs and entity models.
 */
@Component
public class ProductPackageMapperImpl implements ProductPackageMapper {

    /**
     * Converts a ProductPackageRequestDTO to a ProductPackage entity.
     *
     * @param packageRequest the ProductPackageRequestDTO to be converted
     * @return a ProductPackage entity populated with data from the ProductPackageRequestDTO, or null if the packageRequest is null
     */
    @Override
    public ProductPackage packageRequestDTOToProductPackage(ProductPackageRequestDTO packageRequest) {
        if (packageRequest == null) {
            return null;
        }

        ProductPackage productPackage = new ProductPackage();

        // Maps attributes from DynamicAttributeDTO to PackageAttribute
        productPackage.setAttributes(dynamicAttributeDTOListToPackageAttributeList(packageRequest.getAttributes()));

        // Maps other fields
        if (packageRequest.getPrice() != null) {
            productPackage.setPrice(packageRequest.getPrice());
        }
        productPackage.setDescription(packageRequest.getDescription());
        if (packageRequest.getDeliveryTime() != null) {
            productPackage.setDeliveryTime(packageRequest.getDeliveryTime());
        }
        if (packageRequest.getRevisions() != null) {
            productPackage.setRevisions(packageRequest.getRevisions());
        }
        if (packageRequest.getEmailSupport() != null) {
            productPackage.setEmailSupport(packageRequest.getEmailSupport());
        }
        if (packageRequest.getChatSupport() != null) {
            productPackage.setChatSupport(packageRequest.getChatSupport());
        }

        // Maps the PackageType from string to enum
        productPackage.setType(org.elis.progettoing.enumeration.PackageType.valueOf(packageRequest.getType().toUpperCase()));

        return productPackage;
    }

    /**
     * Converts a DynamicAttributeDTO to a PackageAttribute entity.
     *
     * @param dynamicAttributeDTO the DynamicAttributeDTO to be converted
     * @return a PackageAttribute entity populated with data from the DynamicAttributeDTO, or null if the dynamicAttributeDTO is null
     */
    public PackageAttribute dynamicAttributeDTOToPackageAttribute(DynamicAttributeDTO dynamicAttributeDTO) {
        if (dynamicAttributeDTO == null) {
            return null;
        }

        PackageAttribute packageAttribute = new PackageAttribute();

        // Maps the dynamic attribute fields
        packageAttribute.setKey(dynamicAttributeDTO.getKey());
        packageAttribute.setValue(dynamicAttributeDTO.getValue());

        return packageAttribute;
    }

    /**
     * Converts a list of DynamicAttributeDTOs to a list of PackageAttributes.
     *
     * @param list the list of DynamicAttributeDTOs to be converted
     * @return a list of PackageAttributes populated with data from the DynamicAttributeDTOs
     */
    public List<PackageAttribute> dynamicAttributeDTOListToPackageAttributeList(List<DynamicAttributeDTO> list) {
        if (list == null) {
            return Collections.emptyList();
        }

        List<PackageAttribute> list1 = new ArrayList<>(list.size());
        for (DynamicAttributeDTO dynamicAttributeDTO : list) {
            list1.add(dynamicAttributeDTOToPackageAttribute(dynamicAttributeDTO));
        }

        return list1;
    }

    /**
     * Converts a list of ProductPackage entities to a list of ProductPackageResponseDTOs.
     *
     * @param list the list of ProductPackage entities to be converted
     * @return a list of ProductPackageResponseDTOs populated with data from the ProductPackage entities
     */
    public List<ProductPackageResponseDTO> productPackageListToProductPackageResponseDTOList(List<ProductPackage> list) {
        if (list == null) {
            return Collections.emptyList();
        }

        List<ProductPackageResponseDTO> list1 = new ArrayList<>(list.size());
        for (ProductPackage productPackage : list) {
            list1.add(productPackageToProductPackageResponseDTO(productPackage));
        }

        return list1;
    }

    /**
     * Converts a ProductPackage entity to a ProductPackageResponseDTO.
     *
     * @param productPackage the ProductPackage entity to be converted
     * @return a ProductPackageResponseDTO populated with data from the ProductPackage entity, or null if the productPackage is null
     */
    public ProductPackageResponseDTO productPackageToProductPackageResponseDTO(ProductPackage productPackage) {
        if (productPackage == null) {
            return null;
        }

        ProductPackageResponseDTO productPackageResponseDTO = new ProductPackageResponseDTO();

        productPackageResponseDTO.setId(productPackage.getId());
        productPackageResponseDTO.setType(productPackage.getType());
        productPackageResponseDTO.setPrice(productPackage.getPrice());
        productPackageResponseDTO.setDescription(productPackage.getDescription());
        productPackageResponseDTO.setDeliveryTime(productPackage.getDeliveryTime());
        productPackageResponseDTO.setRevisions(productPackage.getRevisions());
        productPackageResponseDTO.setEmailSupport(productPackage.isEmailSupport());
        productPackageResponseDTO.setChatSupport(productPackage.isChatSupport());
        productPackageResponseDTO.setAttributes(packageAttributeListToPackageAttributeResponseDTOList(productPackage.getAttributes()));

        return productPackageResponseDTO;
    }

    /**
     * Converts a list of PackageAttribute entities to a list of PackageAttributeResponseDTOs.
     *
     * @param list the list of PackageAttribute entities to be converted
     * @return a list of PackageAttributeResponseDTOs populated with data from the PackageAttribute entities
     */
    public List<PackageAttributeResponseDTO> packageAttributeListToPackageAttributeResponseDTOList(List<PackageAttribute> list) {
        if (list == null) {
            return Collections.emptyList();
        }

        List<PackageAttributeResponseDTO> list1 = new ArrayList<>(list.size());
        for (PackageAttribute packageAttribute : list) {
            list1.add(packageAttributeToPackageAttributeResponseDTO(packageAttribute));
        }

        return list1;
    }

    /**
     * Converts a PackageAttribute entity to a PackageAttributeResponseDTO.
     *
     * @param packageAttribute the PackageAttribute entity to be converted
     * @return a PackageAttributeResponseDTO populated with data from the PackageAttribute entity, or null if the packageAttribute is null
     */
    public PackageAttributeResponseDTO packageAttributeToPackageAttributeResponseDTO(PackageAttribute packageAttribute) {
        if (packageAttribute == null) {
            return null;
        }

        PackageAttributeResponseDTO packageAttributeResponseDTO = new PackageAttributeResponseDTO();

        packageAttributeResponseDTO.setKey(packageAttribute.getKey());
        packageAttributeResponseDTO.setValue(packageAttribute.getValue());

        return packageAttributeResponseDTO;
    }

    /**
     * Converts a list of ProductPackageRequestDTOs to a list of ProductPackage entities.
     *
     * @param list the list of ProductPackageRequestDTOs to be converted
     * @return a list of ProductPackage entities populated with data from the ProductPackageRequestDTOs
     */
    public List<ProductPackage> productPackageRequestDTOListToProductPackageList(List<ProductPackageRequestDTO> list) {
        if (list == null) {
            return Collections.emptyList();
        }

        List<ProductPackage> list1 = new ArrayList<>(list.size());
        for (ProductPackageRequestDTO productPackageRequestDTO : list) {
            list1.add(productPackageRequestDTOToProductPackage(productPackageRequestDTO));
        }

        return list1;
    }

    /**
     * Converts a ProductPackageRequestDTO to a ProductPackage entity.
     *
     * @param productPackageRequestDTO the ProductPackageRequestDTO to be converted
     * @return a ProductPackage entity populated with data from the ProductPackageRequestDTO, or null if the requestDTO is null
     */
    public ProductPackage productPackageRequestDTOToProductPackage(ProductPackageRequestDTO productPackageRequestDTO) {
        if (productPackageRequestDTO == null) {
            return null;
        }

        ProductPackage productPackage = new ProductPackage();

        if (productPackageRequestDTO.getType() != null) {
            productPackage.setType(Enum.valueOf(PackageType.class, productPackageRequestDTO.getType()));
        }
        if (productPackageRequestDTO.getPrice() != null) {
            productPackage.setPrice(productPackageRequestDTO.getPrice());
        }
        productPackage.setDescription(productPackageRequestDTO.getDescription());
        if (productPackageRequestDTO.getDeliveryTime() != null) {
            productPackage.setDeliveryTime(productPackageRequestDTO.getDeliveryTime());
        }
        if (productPackageRequestDTO.getRevisions() != null) {
            productPackage.setRevisions(productPackageRequestDTO.getRevisions());
        }
        if (productPackageRequestDTO.getEmailSupport() != null) {
            productPackage.setEmailSupport(productPackageRequestDTO.getEmailSupport());
        }
        if (productPackageRequestDTO.getChatSupport() != null) {
            productPackage.setChatSupport(productPackageRequestDTO.getChatSupport());
        }

        return productPackage;
    }
}

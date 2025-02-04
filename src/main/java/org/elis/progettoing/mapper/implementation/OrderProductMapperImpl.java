package org.elis.progettoing.mapper.implementation;

import org.elis.progettoing.dto.response.order.OrderProductResponseDTO;
import org.elis.progettoing.mapper.definition.OrderProductMapper;
import org.elis.progettoing.models.OrderProduct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class implements the OrderProductMapper interface and provides the mapping functionality
 * between DTOs and entities related to OrderProduct.
 * It converts OrderProduct to OrderProductResponseDTO and also maps various other related DTOs.
 */
@Component
public class OrderProductMapperImpl implements OrderProductMapper {

    /**
     * Converts a list of OrderProduct entities to a list of OrderProductResponseDTO.
     *
     * @param orderProducts the list of OrderProduct entities containing the order product data
     * @return the list of OrderProductResponseDTO populated with data from the entities, or an empty list if the entities are null
     */
    @Override
    public List<OrderProductResponseDTO> orderProductListToOrderProductResponseList(List<OrderProduct> orderProducts) {
        if (orderProducts == null) {
            return Collections.emptyList();
        }

        List<OrderProductResponseDTO> list = new ArrayList<>(orderProducts.size());
        for (OrderProduct orderProduct : orderProducts) {
            list.add(orderProductToOrderProductResponseDTO(orderProduct));
        }

        return list;
    }

    /**
     * Converts an OrderProduct entity to an OrderProductResponseDTO.
     *
     * @param orderProduct the OrderProduct entity containing the order product data
     * @return the OrderProductResponseDTO populated with data from the entity, or null if the entity is null
     */
    @Override
    public OrderProductResponseDTO orderProductToOrderProductResponseDTO(OrderProduct orderProduct) {
        if (orderProduct == null) {
            return null;
        }

        OrderProductResponseDTO responseDTO = new OrderProductResponseDTO();
        responseDTO.setId(orderProduct.getId());
        responseDTO.setProductId(orderProduct.getProduct().getId());
        responseDTO.setProductName(orderProduct.getProduct().getTitle());
        responseDTO.setPackageId(orderProduct.getSelectedPackage().getId());
        responseDTO.setPackageName(String.valueOf(orderProduct.getSelectedPackage().getType()));
        responseDTO.setPrice((long) orderProduct.getSelectedPackage().getPrice());
        responseDTO.setStatus(orderProduct.getStatus().toString());
        responseDTO.setEstimatedDeliveryDate(orderProduct.getEstimatedDeliveryDate());
        responseDTO.setProductImagePhoto(orderProduct.getProduct().getUrlProductPhotos().getFirst());
        responseDTO.setDescriptionForSeller(orderProduct.getDescription());

        return responseDTO;
    }
}

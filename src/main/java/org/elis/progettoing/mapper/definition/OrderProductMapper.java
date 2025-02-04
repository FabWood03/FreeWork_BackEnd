package org.elis.progettoing.mapper.definition;

import org.elis.progettoing.dto.response.order.OrderProductResponseDTO;
import org.elis.progettoing.models.OrderProduct;

import java.util.List;

/**
 * Interface for mapping between OrderProduct entities and their respective DTOs.
 * This interface defines methods for converting an OrderProduct entity to an OrderProductResponseDTO,
 * and converting a list of OrderProduct entities to a list of OrderProductResponseDTOs.
 */
public interface OrderProductMapper {

    /**
     * Converts a list of OrderProduct entities to a list of OrderProductResponseDTOs.
     *
     * @param orderProducts the list of OrderProduct entities to be converted
     * @return the list of OrderProductResponseDTOs populated with data from the list of OrderProduct entities
     */
    List<OrderProductResponseDTO> orderProductListToOrderProductResponseList(List<OrderProduct> orderProducts);

    /**
     * Converts an OrderProduct entity to an OrderProductResponseDTO.
     *
     * @param orderProduct the OrderProduct entity to be converted
     * @return the OrderProductResponseDTO populated with data from the OrderProduct entity
     */
    OrderProductResponseDTO orderProductToOrderProductResponseDTO(OrderProduct orderProduct);
}

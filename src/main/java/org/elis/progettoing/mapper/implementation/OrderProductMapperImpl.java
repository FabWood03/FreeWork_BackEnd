package org.elis.progettoing.mapper.implementation;

import org.elis.progettoing.dto.response.order.OrderProductResponseDTO;
import org.elis.progettoing.mapper.definition.OrderProductMapper;
import org.elis.progettoing.models.OrderProduct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class OrderProductMapperImpl implements OrderProductMapper {

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

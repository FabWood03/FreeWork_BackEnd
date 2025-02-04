package org.elis.progettoing.mapper;

import org.elis.progettoing.dto.response.order.OrderProductResponseDTO;
import org.elis.progettoing.enumeration.OrderProductStatus;
import org.elis.progettoing.enumeration.PackageType;
import org.elis.progettoing.mapper.implementation.OrderProductMapperImpl;
import org.elis.progettoing.models.OrderProduct;
import org.elis.progettoing.models.product.Product;
import org.elis.progettoing.models.product.ProductPackage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderProductMapperImplTest {

    @InjectMocks
    private OrderProductMapperImpl orderProductMapper;

    @Test
    void orderProductListToOrderProductResponseList_ReturnsEmptyList_WhenOrderProductsIsNull() {
        List<OrderProductResponseDTO> responseDTOList = orderProductMapper.orderProductListToOrderProductResponseList(null);

        assertNotNull(responseDTOList);
        assertTrue(responseDTOList.isEmpty());
    }

    @Test
    void orderProductListToOrderProductResponseList_ReturnsResponseDTOList_WhenOrderProductsIsValid() {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(1L);
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Product Title");
        product.setUrlProductPhotos(List.of("photo_url"));
        orderProduct.setProduct(product);
        ProductPackage selectedPackage = new ProductPackage();
        selectedPackage.setId(1L);
        selectedPackage.setType(PackageType.BASIC);
        selectedPackage.setPrice(100.0);
        orderProduct.setSelectedPackage(selectedPackage);
        orderProduct.setStatus(OrderProductStatus.PENDING);
        orderProduct.setEstimatedDeliveryDate(LocalDateTime.now());
        orderProduct.setDescription("Description");

        List<OrderProductResponseDTO> responseDTOList = orderProductMapper.orderProductListToOrderProductResponseList(List.of(orderProduct));

        assertNotNull(responseDTOList);
        assertEquals(1, responseDTOList.size());
        assertEquals(1L, responseDTOList.getFirst().getId());
    }

    @Test
    void orderProductToOrderProductResponseDTO_ReturnsResponseDTO_WhenOrderProductIsValid() {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(1L);
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Product Title");
        product.setUrlProductPhotos(List.of("photo_url"));
        orderProduct.setProduct(product);
        ProductPackage selectedPackage = new ProductPackage();
        selectedPackage.setId(1L);
        selectedPackage.setType(PackageType.BASIC);
        selectedPackage.setPrice(100.0);
        orderProduct.setSelectedPackage(selectedPackage);
        orderProduct.setStatus(OrderProductStatus.PENDING);
        orderProduct.setEstimatedDeliveryDate(LocalDateTime.now());
        orderProduct.setDescription("Description");

        OrderProductResponseDTO responseDTO = orderProductMapper.orderProductToOrderProductResponseDTO(orderProduct);

        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals(1L, responseDTO.getProductId());
        assertEquals("Product Title", responseDTO.getProductName());
        assertEquals(1L, responseDTO.getPackageId());
        assertEquals("BASIC", responseDTO.getPackageName());
        assertEquals(100L, responseDTO.getPrice());
        assertEquals("PENDING", responseDTO.getStatus());
        assertEquals(orderProduct.getEstimatedDeliveryDate().withNano(0), responseDTO.getEstimatedDeliveryDate().withNano(0));
        assertEquals("photo_url", responseDTO.getProductImagePhoto());
        assertEquals("Description", responseDTO.getDescriptionForSeller());
    }

    @Test
    void orderProductToOrderProductResponseDTO_ReturnsNull_WhenOrderProductIsNull() {
        OrderProductResponseDTO responseDTO = orderProductMapper.orderProductToOrderProductResponseDTO(null);

        assertNull(responseDTO);
    }
}

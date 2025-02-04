package org.elis.progettoing.dto.response.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing a response containing an order.
 * This class contains essential details of an order such as its ID, purchase date,
 * order products, total price, and the buyer's name, surname, and photo.
 *
 * <p>This DTO is used when fetching detailed information about an order, typically for viewing an order's details.</p>
 */
@Data
public class OrderResponseDTO {
    private long id;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime purchaseDate;
    private List<OrderProductResponseDTO> orderProducts;
    private long totalPrice;
    private String buyerName;
    private String buyerSurname;
    private String buyerPhoto;
}

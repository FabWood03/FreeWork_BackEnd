package org.elis.progettoing.dto.response.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * This class represents the response DTO for an order product.
 * It contains the data that will be sent to the client.
 */
@Data
public class OrderProductResponseDTO {
    private long id;

    private long productId;

    private long packageId;

    private long buyerId;

    private long price;

    private String productImagePhoto;

    private String productName;

    private String packageName;

    private String status;

    private String buyerName;

    private String buyerSurname;

    private String descriptionForSeller;

    private boolean hasReview;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime estimatedDeliveryDate;
}

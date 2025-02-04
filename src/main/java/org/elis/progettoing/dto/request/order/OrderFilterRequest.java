package org.elis.progettoing.dto.request.order;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing the request to filter orders.
 * This DTO contains the necessary details for filtering orders, such as search text and date range type.
 */
@Data
public class OrderFilterRequest {
    private String searchText;
    private String dateRangeType;
}

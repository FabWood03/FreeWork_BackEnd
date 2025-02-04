package org.elis.progettoing.dto.request;

import lombok.Data;

@Data
public class FilterRequest {
    private Integer subCategory;
    private Double maxBudget;
    private Double minBudget;
    private Integer deliveryTime;
    private String searchText;
}
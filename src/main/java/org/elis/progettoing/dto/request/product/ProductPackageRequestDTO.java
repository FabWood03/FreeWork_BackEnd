package org.elis.progettoing.dto.request.product;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a product package request.
 * This DTO is used to create or update a product package, including attributes such as price, description,
 * support options, and dynamic attributes.
 * The class allows the submission of detailed information about a product package, which includes various
 * support types, delivery times, and dynamic attributes, to be used for further processing in the system.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPackageRequestDTO {
    @NotBlank(message = "Il tipo è richiesto")
    private String type;

    @NotNull(message = "Il prezzo è richiesto")
    private Double price;

    @NotNull(message = "La descrizione è richiesta")
    @Max(value = 200, message = "La descrizione deve essere lunga al massimo 200 caratteri")
    private String description;

    @NotNull(message = "Il tempo di consegna è richiesto")
    private Integer deliveryTime;

    @NotNull(message = "Il numero di revisioni è richiesto")
    private Integer revisions;

    @NotNull(message = "Il supporto via email è richiesto")
    private Boolean emailSupport;

    @NotNull(message = "Il supporto via chat è richiesto")
    private Boolean chatSupport;

    private List<DynamicAttributeDTO> attributes;

}

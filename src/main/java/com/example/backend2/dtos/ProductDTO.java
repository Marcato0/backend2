package com.example.backend2.dtos;

import com.example.backend2.models.ProductModel.Category;
import com.example.backend2.models.ProductModel.ProductStatus;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;



import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Objeto ProductDTO que representa os dados de um produto")
public class ProductDTO {


    @NotBlank
    @Size(max = 100)
    @Schema(description = "Nome do produto", example = "Condorito 400")
    private String name;


    @NotNull
    @Schema(description = "Preço unitário do produto", example = "100000.99")
    private BigDecimal unitPrice;


    @NotNull
    @Schema(description = "Categoria do produto", example = "MANUAL")
    private Category category;


    @NotBlank
    @Size(max = 255)
    @Schema(description = "Descrição do produto", example = "Desenvolvida para uma pulverização uniforme, " +
            "custo reduzido de manutenção e alto rendimento operacional diário comparado com aplicações manuais.")
    private String description;


    @NotNull
    @Schema(description = "Status do produto", example = "AVAILABLE")
    private ProductStatus status;
}

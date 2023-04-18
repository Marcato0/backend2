package com.example.backend2.models;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "products")
@Schema(description = "Objeto ProductModel que representa os dados de um produto no banco de dados")
public class ProductModel implements Serializable {

    private static final long serialVersionUID = 1l;

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    @Schema(description = "ID único do produto")
    private UUID id;


    @Column(nullable = false, unique = true)
    @Schema(description = "Nome do produto", example = "Condorito 400")
    private String name;


    @Column(name = "unit_price", nullable = false)
    @Schema(description = "Preço unitário do produto", example = "100000.99")
    private BigDecimal unitPrice;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Categoria do produto", example = "MANUAL")
    private Category category;


    @Column(nullable = false)
    @Schema(description = "Descrição do produto", example = "Desenvolvida para uma pulverização uniforme, " +
            "custo reduzido de manutenção e alto rendimento operacional diário comparado com aplicações manuais.")
    private String description;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Status do produto", example = "AVAILABLE")
    private ProductStatus status;

    public enum Category {
        MANUAL,
        ELECTRIC,
        FUEL_POWERED
    }

    public enum ProductStatus {
        AVAILABLE,
        UNAVAILABLE,
        DISCONTINUED
    }
}

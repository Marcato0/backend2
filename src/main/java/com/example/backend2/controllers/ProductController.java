package com.example.backend2.controllers;


import com.example.backend2.dtos.ProductDTO;
import com.example.backend2.exceptions.ResourceNotFoundException;

import com.example.backend2.models.ProductModel;
import com.example.backend2.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Controller",
        description = "Controlador responsável por gerenciar operações relacionadas a produtos")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    @Operation(summary = "Cria um novo produto",
            description = "Cria um novo produto usando os dados fornecidos no ProductDTO e salva no banco de dados.\n" +
                    "\n" +
                    "@param productDto objeto ProductDTO enviado pelo cliente via HTTP request body.\n" +
                    "@return objeto ProductModel criado.\n")
    public ProductModel createProduct(@RequestBody ProductDTO productDto) {
        return productService.createProduct(productDto);
    }

    @GetMapping
    @Operation(summary = "Obter todos os produtos",
            description = "Retorna uma lista de todos os produtos registrados no banco de dados.\n" +
                    "\n" +
                    "@return objeto ResponseEntity com status HTTP OK e corpo da resposta contendo a lista de objetos ProductModel.\n")
    public ResponseEntity<List<ProductModel>> getAllProducts() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.getAllProducts());
    }


    @GetMapping("/search/{name}")
    @Operation(summary = "Buscar produtos por nome",
            description = "Retorna uma lista de produtos cujo nome contém o valor fornecido.\n" +
                    "\n" +
                    "@param name valor a ser pesquisado no campo 'name' dos objetos ProductModel.\n" +
                    "@return objeto ResponseEntity com status HTTP OK e corpo da resposta contendo a lista de objetos ProductModel.\n" +
                    "@throws ResourceNotFoundException se nenhum produto for encontrado com o nome fornecido.\n")
    public ResponseEntity<List<ProductModel>> findByNameContaining(@PathVariable String name) throws ResourceNotFoundException {
        List<ProductModel> products = productService.findByNameContaining(name);
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }



    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir produto por ID",
            description = "Exclui um produto com base no ID fornecido.\n" +
                    "\n" +
                    "@param id ID do objeto ProductModel a ser removido.\n" +
                    "@return objeto ResponseEntity com status HTTP OK e corpo da resposta contendo a mensagem 'Product deleted successfully.'.\n" +
                    "@throws ResourceNotFoundException se nenhum produto for encontrado com o ID fornecido.\n")
    public ResponseEntity<Object> deleteProduct(@PathVariable UUID id) throws ResourceNotFoundException {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully.");
    }


    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto por ID",
            description = "Atualiza os dados de um produto com base no ID fornecido e nas informações fornecidas no ProductDTO.\n" +
                    "\n" +
                    "@param id ID do objeto ProductModel a ser atualizado.\n" +
                    "@param productDto objeto ProductDTO enviado pelo cliente via HTTP request body.\n" +
                    "@return objeto ResponseEntity com status HTTP OK e corpo da resposta contendo o objeto ProductModel atualizado.\n" +
                    "@throws ResourceNotFoundException se nenhum produto for encontrado com o ID fornecido.\n")
    public ResponseEntity<ProductModel> updateProduct(@PathVariable UUID id,
                                                      @RequestBody ProductDTO productDto)throws ResourceNotFoundException {

        ProductModel product = productService.updateProduct(id, productDto);
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

}

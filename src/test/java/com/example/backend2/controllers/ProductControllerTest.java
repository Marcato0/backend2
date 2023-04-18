package com.example.backend2.controllers;

import com.example.backend2.dtos.ProductDTO;
import com.example.backend2.exceptions.DuplicateObjException;
import com.example.backend2.exceptions.ResourceNotFoundException;
import com.example.backend2.models.ProductModel;
import com.example.backend2.repositories.ProductRepository;
import com.example.backend2.services.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    @DisplayName("Teste para cadastrar produto com todos os campos")
    void deveCadastrarProdutoComTodosOsCampos() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("condorito 400");
        productDTO.setUnitPrice(new BigDecimal("100000.99"));
        productDTO.setCategory(ProductModel.Category.MANUAL);
        productDTO.setDescription("Desenvolvida para uma pulverização uniforme, " +
                "custo reduzido de manutenção e alto rendimento operacional diário comparado com aplicações manuais.");
        productDTO.setStatus(ProductModel.ProductStatus.AVAILABLE);

        ProductModel mockProduct = new ProductModel();
        mockProduct.setId(UUID.randomUUID());
        mockProduct.setName(productDTO.getName());
        mockProduct.setUnitPrice(productDTO.getUnitPrice());
        mockProduct.setCategory(productDTO.getCategory());
        mockProduct.setDescription(productDTO.getDescription());
        mockProduct.setStatus(productDTO.getStatus());

        when(productService.createProduct(any(ProductDTO.class))).thenReturn(mockProduct);

        ProductModel savedProduct = productController.createProduct(productDTO);

        assertNotNull(savedProduct.getId());
        assertEquals(productDTO.getName(), savedProduct.getName());
        assertEquals(productDTO.getUnitPrice(), savedProduct.getUnitPrice());
        assertEquals(productDTO.getCategory(), savedProduct.getCategory());
        assertEquals(productDTO.getDescription(), savedProduct.getDescription());
        assertEquals(productDTO.getStatus(), savedProduct.getStatus());
    }


    @Test
    @DisplayName("Teste para cadastrar produto que ja existe e reornar um erro")
    void deveLancarExcecaoAoTentarCadastrarProdutoExistente() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("condorito 400");
        productDTO.setUnitPrice(new BigDecimal("100000.99"));
        productDTO.setCategory(ProductModel.Category.MANUAL);
        productDTO.setDescription("...");
        productDTO.setStatus(ProductModel.ProductStatus.AVAILABLE);

        when(productService.createProduct(any(ProductDTO.class))).thenThrow(new DuplicateObjException("Produto já existe."));

        assertThrows(DuplicateObjException.class, () -> productController.createProduct(productDTO));
    }

    @Test
    @DisplayName("Buscar todos os produtos")
    void getAllProducts() {
        // Cria uma lista de objetos ProductModel
        List<ProductModel> products = new ArrayList<>();
        ProductModel product1 = new ProductModel();
        product1.setName("Produto 1");
        products.add(product1);

        ProductModel product2 = new ProductModel();
        product2.setName("Produto 2");
        products.add(product2);

        // Configura o mock do serviço para retornar a lista de produtos
        when(productService.getAllProducts()).thenReturn(products);

        // Chama o método getAllProducts() do controller e verifica o resultado
        ResponseEntity<List<ProductModel>> response = productController.getAllProducts();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("Produto 1", response.getBody().get(0).getName());
        assertEquals("Produto 2", response.getBody().get(1).getName());
    }



    @Test
    @DisplayName("Teste para buscar produtos por nome")
    void deveBuscarProdutosPorNome() {
        String name = "Condorito";
        ProductModel product1 = new ProductModel();
        product1.setName("Condorito 400");
        product1.setUnitPrice(new BigDecimal("100000.99"));
        product1.setCategory(ProductModel.Category.MANUAL);
        product1.setDescription("Desenvolvida para uma pulverização uniforme, " +
                "custo reduzido de manutenção e alto rendimento operacional diário comparado com aplicações manuais.");
        product1.setStatus(ProductModel.ProductStatus.AVAILABLE);

        List<ProductModel> mockProducts = Collections.singletonList(product1);

        when(productService.findByNameContaining(name)).thenReturn(mockProducts);

        ResponseEntity<List<ProductModel>> responseEntity = productController.findByNameContaining(name);
        List<ProductModel> foundProducts = responseEntity.getBody();

        assertNotNull(foundProducts);
        assertEquals(1, foundProducts.size());
        assertEquals(product1.getName(), foundProducts.get(0).getName());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }


    @Test
    @DisplayName("Teste para buscar produtos por nome que não existe")
    void deveLancarExcecaoAoTentarBuscarProdutoInexistente() {
        String name = "produto inexistente";
        when(productService.findByNameContaining(name)).thenThrow(new ResourceNotFoundException("Produto não encontrado."));

        assertThrows(ResourceNotFoundException.class, () -> productController.findByNameContaining(name));
    }

    @Test
    @DisplayName("Teste para excluir produto pelo id")
    void deveExcluirProdutoPorId() throws ResourceNotFoundException {
        UUID id = UUID.randomUUID();
        doNothing().when(productService).deleteProduct(id);
        ResponseEntity<Object> response = productController.deleteProduct(id);
        verify(productService, times(1)).deleteProduct(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product deleted successfully.", response.getBody());
    }

    @Test
    @DisplayName("Teste para excluir produto que não existe pelo id")
    void deveLancarExcecaoAoTentarExcluirProdutoInexistente() {
        UUID id = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Produto não encontrado.")).when(productService).deleteProduct(id);

        assertThrows(ResourceNotFoundException.class, () -> productController.deleteProduct(id));
    }

    @Test
    @DisplayName("Teste para atualizar produto pelo id")
    void deveAtualizarProdutoPorId() {
        // Cria um produto para ser atualizado
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("condorito 400");
        productDTO.setUnitPrice(new BigDecimal("100000.99"));
        productDTO.setCategory(ProductModel.Category.MANUAL);
        productDTO.setDescription("Desenvolvida para uma pulverização uniforme, " +
                "custo reduzido de manutenção e alto rendimento operacional diário comparado com aplicações manuais.");
        productDTO.setStatus(ProductModel.ProductStatus.AVAILABLE);

        ProductModel mockProduct = new ProductModel();
        UUID productId = UUID.randomUUID();
        mockProduct.setId(productId);
        mockProduct.setName(productDTO.getName());
        mockProduct.setUnitPrice(productDTO.getUnitPrice());
        mockProduct.setCategory(productDTO.getCategory());
        mockProduct.setDescription(productDTO.getDescription());
        mockProduct.setStatus(productDTO.getStatus());

        // Quando productService.updateProduct for chamado com o ID correto, retorne o mockProduct
        when(productService.updateProduct(eq(productId), any(ProductDTO.class))).thenReturn(mockProduct);

        // Chama o método do controlador para atualizar o produto
        ProductModel updatedProduct = productController.updateProduct(productId, productDTO).getBody();

        // Verifica se o produto foi atualizado corretamente
        assertNotNull(updatedProduct);
        assertEquals(productId, updatedProduct.getId());
        assertEquals(productDTO.getName(), updatedProduct.getName());
        assertEquals(productDTO.getUnitPrice(), updatedProduct.getUnitPrice());
        assertEquals(productDTO.getCategory(), updatedProduct.getCategory());
        assertEquals(productDTO.getDescription(), updatedProduct.getDescription());
        assertEquals(productDTO.getStatus(), updatedProduct.getStatus());
    }

    @Test
    @DisplayName("Teste para ediatr produto e colocar um nome de umproduto que ja existe")
    void deveLancarExcecaoAoTentarEditarProdutoComNomeExistente() {
        UUID id = UUID.randomUUID();
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("condorito 400");
        productDTO.setUnitPrice(new BigDecimal("100000.99"));
        productDTO.setCategory(ProductModel.Category.MANUAL);
        productDTO.setDescription("...");
        productDTO.setStatus(ProductModel.ProductStatus.AVAILABLE);

        when(productService.updateProduct(eq(id), any(ProductDTO.class)))
                .thenThrow(new DuplicateObjException("Já existe um produto com esse nome."));

        assertThrows(DuplicateObjException.class, () -> productController.updateProduct(id, productDTO));
    }



}
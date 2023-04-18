package com.example.backend2.services;

import com.example.backend2.dtos.ProductDTO;
import com.example.backend2.exceptions.DuplicateObjException;
import com.example.backend2.exceptions.ResourceNotFoundException;
import com.example.backend2.models.ProductModel;
import com.example.backend2.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    /**
     * Cria um produto com base no DTO fornecido e o salva no banco de dados.
     *
     * @param productDto o DTO que contém as informações do usuário a ser criado
     * @return o produto criado e salvo no banco de dados
     * @throws DuplicateObjException se já existir um produto com o mesmo nome
     */
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ProductModel createProduct(ProductDTO productDto) {

        // Remove espaços em branco no início e no fim do nome
        productDto.setName(productDto.getName().trim());

        // Converte o nome para letras minúsculas
        productDto.setName(productDto.getName().toLowerCase());

        // Verifica se já existe um produto com o mesmo nome
        if (productRepository.findByNameIgnoreCase(productDto.getName()).isPresent()) {
            throw new DuplicateObjException("Product is already in use!");
        }



        // Cria um produto com base no DTO
        var product = new ProductModel();
        BeanUtils.copyProperties(productDto, product);



        // Salva o usuário no banco de dados
        return productRepository.save(product);
    }


    /**
     * Retorna uma lista de todos os produtos salvos no banco de dados.
     *
     * @return uma lista de todos os produtos salvos no banco de dados
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_COLLABORATOR')")
    public List<ProductModel> getAllProducts() {
        return productRepository.findAll();
    }


    /**
     * Retorna uma lista de produtos com nome correspondente à pesquisa.
     *
     * @param name o nome a ser pesquisado
     * @return uma lista de produtos com nome correspondente à pesquisa
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_COLLABORATOR')")
    public List<ProductModel> findByNameContaining(String name) throws ResourceNotFoundException {

        // Remove os espaços em branco no início e no final da string
        String trimmedName = name.trim();

        //busca o produto pelo nome
        List<ProductModel> optionalProduct = productRepository.findByNameContainingIgnoreCase(trimmedName);


        // verifica se produto existe
        if (optionalProduct.isEmpty()) {
            throw new ResourceNotFoundException("Product not found with name containing: " + name);
        }

        return optionalProduct;
    }


    /**
     * Exclui o produto com o id fornecido.
     * @param id o id do produto a ser excluído.
     * @throws ResourceNotFoundException se não houver produto com o id fornecido.
     */
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public void deleteProduct(UUID id) throws ResourceNotFoundException {

        //busca o produto pelo id
        Optional<ProductModel> optionalProduct = productRepository.findById(id);

        // verifica se produto existe
        if (optionalProduct.isEmpty()) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }

        // exclui o produto encontrado
        ProductModel product = optionalProduct.get();
        productRepository.deleteById(product.getId());
    }


    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    /**
     * Atualiza os dados do produto com o id fornecido com base nos dados fornecidos.
     * @param id o id do produto a ser atualizado.
     * @param productDTO um objeto ProductDTO contendo os novos dados de produto.
     * @return um objeto ProductModel representando o produto atualizado.
     * @throws ResourceNotFoundException se não houver produto com o id fornecido.
     */
    public ProductModel updateProduct(UUID id, ProductDTO productDTO) throws ResourceNotFoundException {

        Optional<ProductModel> optionalProduct = productRepository.findById(id);

        // verifica se o produto existe
        if (optionalProduct.isEmpty()) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }

        // Verifica se já existe um produto com o mesmo nome
        if (productRepository.findByNameIgnoreCase(productDTO.getName()).isPresent()) {
            throw new DuplicateObjException("Product is already in use!");
        }

        // Criar um produto com base no DTO
        var product = new ProductModel();
        BeanUtils.copyProperties(productDTO, product);


        //Buscar id
        product.setId(optionalProduct.get().getId());

        // salva no banco de dados
        return productRepository.save(product);
    }
}

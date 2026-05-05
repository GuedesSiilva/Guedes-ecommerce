package br.com.senai.api_ecommerce.controllers;

import br.com.senai.api_ecommerce.categoria.Categoria;
import br.com.senai.api_ecommerce.categoria.CategoriaRepository;
import br.com.senai.api_ecommerce.produto.*;
import br.com.senai.api_ecommerce.exceptions.ErroResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("produtos")
@Tag(name="Produtos",description="Gerenciamento dos produtos do ecommerce")
public class ProdutoController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @PostMapping
    @Transactional
    @Operation(summary = "Criar um novo produto")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DadosDetalhamentoProduto.class))
                    }),
            @ApiResponse(responseCode = "409", description = "SKU já cadastrado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Categoria inválida", content = @Content)
    })
    public ResponseEntity<DadosDetalhamentoProduto> cadastrarProduto(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DadosCadastroProduto.class),
                            examples = @ExampleObject(
                                    value = "{ \"nome\": \"Nome Produto\",\t\n" +
                                            "\t\"preco\": 21.00,\n" +
                                            "\t\"sku\":\"999999999\",\n" +
                                            "\t\"descricao\": \"Descrição do produto\",\n" +
                                            "\t\"estoque\": 1,\n" +
                                            "\t\"categoriaId\": 6}"
                            )
                    )
            )
            @RequestBody @Valid DadosCadastroProduto dados){
        //1. Verificar se a categoria existe
        var categoria = categoriaRepository.findByIdAndAtivoTrue(dados.categoriaId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Categoria não encontrada"));
        //2. Verificar se SKU é único
        if(produtoRepository.existsBySkuAndAtivoTrue(dados.sku()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU já cadastrado no sistema");
        //3. Criar o produto
        Produto produto = new Produto(dados, categoria);
        produtoRepository.save(produto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new DadosDetalhamentoProduto(produto));
    }

    @GetMapping
    @Operation(summary = "Listar todos os produtos")
    public ResponseEntity<Page<DadosListagemProduto>> listarProdutos(@PageableDefault(size=10, sort={"nome"}) Pageable paginacao){
        var page = produtoRepository.findAllByAtivoTrue(paginacao)
                .map(DadosListagemProduto::new);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Listar os produtos por ID")
    public ResponseEntity<DadosDetalhamentoProduto> buscarProdutoPorId(@PathVariable Long id){
        var produto = produtoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
        return ResponseEntity.ok(new DadosDetalhamentoProduto(produto));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Excluir produtos")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity excluirProduto(@PathVariable Long id){
        var produto = produtoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
        produto.excluirProduto();

        return ResponseEntity.noContent().build();
    }

    @PutMapping
    @Transactional
    @Operation(summary = "Atualizar produtos")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DadosDetalhamentoProduto.class))
                    }),
            @ApiResponse(responseCode = "409", description = "SKU já cadastrado", content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Recurso não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Categoria inválida",
                                            value = """
                    {"codigo": "CATEGORIA_NAO_ENCONTRADA", "mensagem": "Categoria inválida"}
                    """
                                    ),
                                    @ExampleObject(
                                            name = "Produto inválido",
                                            value = """
                    {"codigo": "PRODUTO_NAO_ENCONTRADO", "mensagem": "Produto inválido"}
                    """
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<DadosDetalhamentoProduto> atualizarProduto(
            @RequestBody @Valid DadosAtualizarProduto dados
    ){
        //1. Verificar se o produto existe
        var produto = produtoRepository.findByIdAndAtivoTrue(dados.id())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        //2. Verificar se a categoria existe
        Categoria categoria = null;
        if(dados.categoriaId() != null) {
            categoria = categoriaRepository.findByIdAndAtivoTrue(dados.categoriaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
        }
        //3. Verificar se SKU é único
        if(dados.sku()!=null && !dados.sku().isBlank()) {
            if (produtoRepository.existsBySkuAndAtivoTrue(dados.sku()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU já cadastrado no sistema");
        }

        produto.atualizarProduto(dados, categoria);

        return ResponseEntity.ok(new DadosDetalhamentoProduto(produto));
    }
}
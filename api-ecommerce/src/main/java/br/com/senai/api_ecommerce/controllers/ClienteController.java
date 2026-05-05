package br.com.senai.api_ecommerce.controllers;

import br.com.senai.api_ecommerce.cliente.*;
import br.com.senai.api_ecommerce.exceptions.ErroResponse;
import br.com.senai.api_ecommerce.produto.DadosCadastroProduto;
import br.com.senai.api_ecommerce.produto.DadosDetalhamentoProduto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("clientes")
@Tag(name = "Clientes", description = "Gerenciamento dos clientes do ecommerce")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping
    @Transactional
    @Operation(summary = "Cadastrar um novo cliente")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DadosDetalhamentoCliente.class))
                    }),
            @ApiResponse(responseCode = "409", description = "CPF ou Email já cadastrado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado", content = @Content)
    })
    public void cadastrarCliente(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DadosCadastroProduto.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "\t\"nome\": \"Nome do cliente\",\n" +
                                            "\t\"email\": \"aleatorio@email.com\",\n" +
                                            "\t\"telefone\": \"11999999999\",\n" +
                                            "\t\"cpf\": \"12345678901\",\n" +
                                            "\t\"endereco\": {\n" +
                                            "\t\t\"logradouro\": \"Rua das Flores\",\n" +
                                            "\t\t\"bairro\": \"Centro\",\n" +
                                            "\t\t\"cidade\": \"São Paulo\",\n" +
                                            "\t\t\"cep\": \"12345678\",\n" +
                                            "\t\t\"uf\": \"SP\",\n" +
                                            "\t\t\"numero\": \"100\",\n" +
                                            "\t\t\"complemento\": \"Apto 12\"\n" +
                                            "\t}\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody @Valid DadosCadastroCliente dados){
    clienteRepository.save(new Cliente(dados));
    }

    @GetMapping
    @Operation(summary = "Listar todos os clientes")
    public Page<DadosListagemCliente> listarClientes(@PageableDefault(size = 10) Pageable paginacao)
    {
        return clienteRepository.findAllByAtivoTrue(paginacao)
                .map(DadosListagemCliente::new);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Listar os clientes por ID")
    public DadosDetalhamentoCliente detalharCliente (@PathVariable Long id)
    {
        Cliente cliente = clienteRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(()-> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente não existe, SEU BURRO"
                ));
        return new DadosDetalhamentoCliente(cliente);
    }

    @PutMapping
    @Transactional
    @Operation(summary = "Atualizar dados dos Clientes")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DadosDetalhamentoCliente.class))
                    }),

            @ApiResponse(responseCode = "409", description = "CPF ou e-mail já cadastrado", content = @Content),

            @ApiResponse(
                    responseCode = "404",
                    description = "Recurso não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class),
                            examples = {

                                    @ExampleObject(
                                            name = "Cliente não encontrado",
                                            value = """
                    {"codigo": "CLIENTE_NAO_ENCONTRADO", "mensagem": "Cliente não encontrado"}
                    """
                                    ),

                                    @ExampleObject(
                                            name = "Endereço inválido",
                                            value = """
                    {"codigo": "ENDERECO_INVALIDO", "mensagem": "Dados de endereço inválidos"}
                    """
                                    )
                            }
                    )
            )
    })
    public void  atualizarCliente(@RequestBody @Valid DadosAtualizarCliente dados){
        var cliente = clienteRepository.getReferenceById(dados.id());
        cliente.atualizarCliente(dados);
    }

    @DeleteMapping("{id}")
    @Transactional
    @Operation(summary = "Remover clientes do sistema")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void  excluirCliente(@PathVariable Long id){
        var cliente = clienteRepository.getReferenceById(id);
        cliente.excluirCliente();
    }
}

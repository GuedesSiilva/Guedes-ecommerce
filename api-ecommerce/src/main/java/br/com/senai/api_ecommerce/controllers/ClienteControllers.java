package br.com.senai.api_ecommerce.controllers;

import br.com.senai.api_ecommerce.cliente.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("clientes")
public class ClienteControllers {

    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping
    @Transactional
    public void  cadastrarCliente(@RequestBody @Valid DadosCadastroCliente dados){
    clienteRepository.save(new Cliente(dados));
    }

    @GetMapping
    public Page<DadosListagemCliente> listarClientes(@PageableDefault(size = 10) Pageable paginacao)
    {
        return clienteRepository.findAllByAtivoTrue(paginacao)
                .map(DadosListagemCliente::new);
    }

    @GetMapping("/{id}")
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
    public void  atualizarCliente(@RequestBody @Valid DadosAtualizarCliente dados){
        var cliente = clienteRepository.getReferenceById(dados.id());
        cliente.atualizarCliente(dados);
    }

    @DeleteMapping("{id}")
    @Transactional
    public void  excluirCliente(@PathVariable Long id){
        var cliente = clienteRepository.getReferenceById(id);
        cliente.excluirCliente();
    }
}

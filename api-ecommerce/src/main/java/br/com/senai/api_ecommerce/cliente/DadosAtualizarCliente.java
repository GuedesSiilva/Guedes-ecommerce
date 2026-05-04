package br.com.senai.api_ecommerce.cliente;

import br.com.senai.api_ecommerce.endereco.DadosAtualizarEndereco;
import br.com.senai.api_ecommerce.endereco.DadosCadastroEndereco;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record DadosAtualizarCliente(
        Long id,

        @Size(min = 3, max = 100)
        @Column(unique = true)
        String nome,

        @Email
        String email,

        @Size(max = 20)
        String telefone,

        @Valid
        DadosAtualizarEndereco endereco
) {}

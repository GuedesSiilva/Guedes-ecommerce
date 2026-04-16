package br.com.senai.api_ecommerce.cliente;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record DadosAtualizarCliente(
        Long id,

        @Size(min = 3, max = 100)
        @Column(unique = true)
        String nome,

        @Email
        String email,

        @Size(max = 20)
        String telefone
) {}

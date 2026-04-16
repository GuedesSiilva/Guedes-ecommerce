package br.com.senai.api_ecommerce.categoria;

public record DadosListagemCategoria(
        Long id,
        String Nome
) {
    public  DadosListagemCategoria(Categoria categoria) {
        this(categoria.getId(),  categoria.getNome());
    }
}

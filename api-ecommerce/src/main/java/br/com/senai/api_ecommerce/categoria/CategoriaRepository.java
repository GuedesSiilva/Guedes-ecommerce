package br.com.senai.api_ecommerce.categoria;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Long id(Long id);

    Page<Categoria> findAllByAtivoTrue(Pageable paginacao);
}

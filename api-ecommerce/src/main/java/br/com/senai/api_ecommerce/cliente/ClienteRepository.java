package br.com.senai.api_ecommerce.cliente;

import io.micrometer.observation.ObservationFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Long id(long id);

    Optional<Cliente> findByIdAndAtivoTrue(Long id);

    Page<Cliente> findAllByAtivoTrue(Pageable paginacao);
}

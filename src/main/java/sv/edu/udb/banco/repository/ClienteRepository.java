package sv.edu.udb.banco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.banco.entity.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByDui(String dui);

    List<Cliente> findAllByEstado(String estado);
}

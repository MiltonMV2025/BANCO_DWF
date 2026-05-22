package sv.edu.udb.banco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import sv.edu.udb.banco.entity.Cuenta;

import java.util.List;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {

    List<Cuenta> findAllByCliente_Id(Integer clienteId);

    List<Cuenta> findAllByTipo(String tipo);

    @EntityGraph(attributePaths = {"cliente"})
    List<Cuenta> findAllByOrderByIdCuentaAsc();

    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
}

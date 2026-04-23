package sv.edu.udb.banco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.banco.entity.Cuenta;

import java.util.List;

public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {

    List<Cuenta> findAllByCliente_Id(Integer clienteId);

    List<Cuenta> findAllByTipo(String tipo);
}

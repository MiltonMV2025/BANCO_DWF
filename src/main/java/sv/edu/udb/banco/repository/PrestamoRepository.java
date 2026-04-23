package sv.edu.udb.banco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.banco.entity.Prestamo;

import java.util.List;

public interface PrestamoRepository extends JpaRepository<Prestamo, Integer> {

    List<Prestamo> findAllByCliente_Id(Integer clienteId);

    List<Prestamo> findAllByEmpleado_IdEmpleado(Integer idEmpleado);

    List<Prestamo> findAllByEstado(String estado);
}

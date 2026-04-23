package sv.edu.udb.banco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.banco.entity.Empleado;

import java.util.List;

public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {

    List<Empleado> findAllByEstado(String estado);

    List<Empleado> findAllByRol(String rol);
}

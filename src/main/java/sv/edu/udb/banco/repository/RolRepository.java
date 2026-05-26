package sv.edu.udb.banco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.banco.entity.Rol;

import java.util.List;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    List<Rol> findAllByEstadoOrderByNombreAsc(String estado);

    Optional<Rol> findByCodigoAndEstado(String codigo, String estado);
}

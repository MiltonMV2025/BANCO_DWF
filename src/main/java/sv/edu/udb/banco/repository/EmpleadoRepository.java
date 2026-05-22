package sv.edu.udb.banco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sv.edu.udb.banco.entity.Empleado;

import java.util.List;

public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {

    interface EmpleadoGerenciaRow {
        Integer getIdEmpleado();
        String getNombre();
        String getRol();
        String getEstado();
        Long getPrestamosAsignados();
    }

    List<Empleado> findAllByEstado(String estado);

    List<Empleado> findAllByRol(String rol);

    @Query("""
            select
                e.idEmpleado as idEmpleado,
                e.nombre as nombre,
                e.rol as rol,
                e.estado as estado,
                count(p.idPrestamo) as prestamosAsignados
            from Empleado e
            left join e.prestamos p
            where (:q is null or trim(:q) = '' or lower(e.nombre) like lower(concat('%', :q, '%')) or lower(e.rol) like lower(concat('%', :q, '%')))
              and (:estado is null or trim(:estado) = '' or lower(e.estado) = lower(:estado))
            group by e.idEmpleado, e.nombre, e.rol, e.estado
            order by e.nombre asc
            """)
    List<EmpleadoGerenciaRow> findResumenGerencia(
            @Param("q") String q,
            @Param("estado") String estado
    );
}

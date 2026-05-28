package sv.edu.udb.banco.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sv.edu.udb.banco.entity.Sucursal;

public interface SucursalRepository extends JpaRepository<Sucursal, Integer> {

    List<Sucursal> findAllByEstado(String estado);

    @Query("""
            select s from Sucursal s
            where (:q is null or trim(:q) = ''
                   or lower(s.nombre) like lower(concat('%', :q, '%'))
                   or lower(s.direccion) like lower(concat('%', :q, '%')))
              and (:estado is null or trim(:estado) = '' or lower(s.estado) = lower(:estado))
            order by s.nombre asc
            """)
    List<Sucursal> buscarSucursales(
            @Param("q") String q,
            @Param("estado") String estado
    );

    @Query("""
            select s from Sucursal s
            left join fetch s.empleados e
            where s.idSucursal = :id
            """)
    java.util.Optional<Sucursal> findByIdConEmpleados(@Param("id") Integer id);
}
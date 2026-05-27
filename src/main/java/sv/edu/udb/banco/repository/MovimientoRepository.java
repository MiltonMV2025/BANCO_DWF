package sv.edu.udb.banco.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sv.edu.udb.banco.entity.Movimiento;

public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {

    List<Movimiento> findAllByCliente_Id(Integer clienteId);

    List<Movimiento> findAllByCuenta_IdCuenta(Integer idCuenta);

    List<Movimiento> findAllByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    List<Movimiento> findTop5ByCuenta_IdCuentaOrderByFechaDescIdMovimientoDesc(Integer idCuenta);

    List<Movimiento> findAllByCuenta_IdCuentaAndFechaBetweenOrderByFechaDescIdMovimientoDesc(
            Integer idCuenta,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    // --- NUEVO: para el gerente general ---

    interface MovimientoGlobalRow {
        Integer getIdMovimiento();
        String getTipo();
        java.math.BigDecimal getMonto();
        LocalDate getFecha();
        String getNumeroCuenta();
        String getNombreCliente();
        String getDuiCliente();
    }

    @Query("""
            select
                m.idMovimiento   as idMovimiento,
                m.tipo           as tipo,
                m.monto          as monto,
                m.fecha          as fecha,
                c.numeroCuenta   as numeroCuenta,
                cl.nombre        as nombreCliente,
                cl.dui           as duiCliente
            from Movimiento m
            join m.cuenta c
            join m.cliente cl
            where (:fechaInicio is null or m.fecha >= :fechaInicio)
              and (:fechaFin    is null or m.fecha <= :fechaFin)
              and (:q is null or trim(:q) = ''
                   or lower(cl.nombre)      like lower(concat('%', :q, '%'))
                   or lower(c.numeroCuenta) like lower(concat('%', :q, '%'))
                   or lower(cl.dui)         like lower(concat('%', :q, '%')))
            order by m.fecha desc, m.idMovimiento desc
            """)
    List<MovimientoGlobalRow> findMovimientosGlobal(
            @Param("q") String q,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );
}
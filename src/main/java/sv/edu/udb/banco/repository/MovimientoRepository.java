package sv.edu.udb.banco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.banco.entity.Movimiento;

import java.time.LocalDate;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {

    List<Movimiento> findAllByCliente_Id(Integer clienteId);

    List<Movimiento> findAllByCuenta_IdCuenta(Integer idCuenta);

    List<Movimiento> findAllByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
}

package sv.edu.udb.banco.service;

import sv.edu.udb.banco.entity.Prestamo;

import java.math.BigDecimal;
import java.util.List;

public interface PrestamoService {

    Prestamo solicitarPrestamo(
            Integer clienteId,
            BigDecimal monto,
            Integer plazoMeses);

    List<Prestamo> obtenerPrestamosCliente(Integer clienteId);

    List<Prestamo> obtenerPrestamosPendientes();

    Prestamo aprobarPrestamo(Integer idPrestamo);

    Prestamo rechazarPrestamo(Integer idPrestamo);
}
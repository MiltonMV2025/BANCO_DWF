package sv.edu.udb.banco.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.banco.entity.Cuenta;
import sv.edu.udb.banco.entity.Movimiento;
import sv.edu.udb.banco.repository.CuentaRepository;
import sv.edu.udb.banco.repository.MovimientoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class TransferenciaService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;

    public TransferenciaService(
            final CuentaRepository cuentaRepository,
            final MovimientoRepository movimientoRepository
    ) {
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional
    public void procesarMovimiento(
            final Integer cuentaId,
            final Integer cuentaDestinoId,
            final String tipoOperacion,
            final BigDecimal montoOperacion
    ) {
        if (cuentaId == null) {
            throw new IllegalArgumentException("Debes seleccionar una cuenta.");
        }

        if (montoOperacion == null || montoOperacion.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }

        final Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("La cuenta seleccionada no existe."));

        final boolean isRetiro = "RETIRO".equalsIgnoreCase(tipoOperacion);
        final boolean isTransferencia = "TRANSFERENCIA".equalsIgnoreCase(tipoOperacion);
        final BigDecimal saldoActual = cuenta.getSaldo() == null ? BigDecimal.ZERO : cuenta.getSaldo();

        if ((isRetiro || isTransferencia) && saldoActual.compareTo(montoOperacion) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar el retiro.");
        }

        if (isTransferencia) {
            if (cuentaDestinoId == null) {
                throw new IllegalArgumentException("Debes seleccionar la cuenta destino.");
            }

            if (cuentaDestinoId.equals(cuentaId)) {
                throw new IllegalArgumentException("La cuenta origen y destino no pueden ser la misma.");
            }

            final Cuenta cuentaDestino = cuentaRepository.findById(cuentaDestinoId)
                    .orElseThrow(() -> new IllegalArgumentException("La cuenta destino no existe."));

            cuenta.setSaldo(saldoActual.subtract(montoOperacion));
            final BigDecimal saldoDestino = cuentaDestino.getSaldo() == null ? BigDecimal.ZERO : cuentaDestino.getSaldo();
            cuentaDestino.setSaldo(saldoDestino.add(montoOperacion));

            cuentaRepository.save(cuenta);
            cuentaRepository.save(cuentaDestino);

            final Movimiento retiro = new Movimiento();
            retiro.setCuenta(cuenta);
            retiro.setCliente(cuenta.getCliente());
            retiro.setTipo("RETIRO");
            retiro.setMonto(montoOperacion);
            retiro.setFecha(LocalDate.now());

            final Movimiento deposito = new Movimiento();
            deposito.setCuenta(cuentaDestino);
            deposito.setCliente(cuentaDestino.getCliente());
            deposito.setTipo("DEPOSITO");
            deposito.setMonto(montoOperacion);
            deposito.setFecha(LocalDate.now());

            movimientoRepository.save(retiro);
            movimientoRepository.save(deposito);
            return;
        }

        final BigDecimal saldoNuevo = isRetiro ? saldoActual.subtract(montoOperacion) : saldoActual.add(montoOperacion);

        cuenta.setSaldo(saldoNuevo);
        cuentaRepository.save(cuenta);

        final Movimiento movimiento = new Movimiento();
        movimiento.setCuenta(cuenta);
        movimiento.setCliente(cuenta.getCliente());
        movimiento.setTipo(isRetiro ? "RETIRO" : "DEPOSITO");
        movimiento.setMonto(montoOperacion);
        movimiento.setFecha(LocalDate.now());
        movimientoRepository.save(movimiento);
    }
}

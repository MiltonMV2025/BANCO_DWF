package sv.edu.udb.banco.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.edu.udb.banco.entity.Cliente;
import sv.edu.udb.banco.entity.Empleado;
import sv.edu.udb.banco.entity.Prestamo;
import sv.edu.udb.banco.repository.ClienteRepository;
import sv.edu.udb.banco.repository.EmpleadoRepository;
import sv.edu.udb.banco.repository.PrestamoRepository;
import sv.edu.udb.banco.service.PrestamoService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PrestamoServiceImpl implements PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Override
    public Prestamo solicitarPrestamo(Integer clienteId, BigDecimal monto, Integer plazoMeses) {

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        BigDecimal salario = cliente.getSalario();

        BigDecimal interes;
        BigDecimal montoMaximo;

        if (salario.compareTo(new BigDecimal("365")) < 0) {
            montoMaximo = new BigDecimal("10000");
            interes = new BigDecimal("3");
        } else if (salario.compareTo(new BigDecimal("600")) < 0) {
            montoMaximo = new BigDecimal("25000");
            interes = new BigDecimal("3");
        } else if (salario.compareTo(new BigDecimal("900")) < 0) {
            montoMaximo = new BigDecimal("35000");
            interes = new BigDecimal("4");
        } else {
            montoMaximo = new BigDecimal("50000");
            interes = new BigDecimal("5");
        }

        if (monto.compareTo(montoMaximo) > 0) {
            throw new RuntimeException("El monto solicitado supera el máximo permitido");
        }

        BigDecimal cuotaMensual = monto
                .divide(new BigDecimal(plazoMeses), 2, RoundingMode.HALF_UP);

        Prestamo prestamo = new Prestamo();

        prestamo.setCliente(cliente);

        Empleado empleado = empleadoRepository.findAll().get(0);
        prestamo.setEmpleado(empleado);

        prestamo.setMonto(monto);
        prestamo.setInteres(interes);
        prestamo.setPlazoMeses(plazoMeses);
        prestamo.setEstado("EN ESPERA");
        prestamo.setCuotaMensual(cuotaMensual);

        return prestamoRepository.save(prestamo);
    }

    @Override
    public List<Prestamo> obtenerPrestamosCliente(Integer clienteId) {
        return prestamoRepository.findAllByCliente_Id(clienteId);
    }

    @Override
    public List<Prestamo> obtenerPrestamosPendientes() {
        return prestamoRepository.findAllByEstado("EN ESPERA");
    }

    @Override
    public Prestamo aprobarPrestamo(Integer idPrestamo) {

        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        prestamo.setEstado("APROBADO");

        return prestamoRepository.save(prestamo);
    }

    @Override
    public Prestamo rechazarPrestamo(Integer idPrestamo) {

        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        prestamo.setEstado("RECHAZADO");

        return prestamoRepository.save(prestamo);
    }
}
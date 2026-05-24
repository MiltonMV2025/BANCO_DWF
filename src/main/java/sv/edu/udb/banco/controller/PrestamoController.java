package sv.edu.udb.banco.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.banco.entity.Prestamo;
import sv.edu.udb.banco.service.PrestamoService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @PostMapping("/solicitar")
    public ResponseEntity<?> solicitarPrestamo(
            @RequestParam Integer clienteId,
            @RequestParam BigDecimal monto,
            @RequestParam Integer plazoMeses) {

        try {

            prestamoService.solicitarPrestamo(
                    clienteId,
                    monto,
                    plazoMeses);

            return ResponseEntity.ok("Préstamo registrado correctamente");

        } catch (Exception e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Prestamo>> obtenerPrestamosCliente(
            @PathVariable Integer clienteId) {

        return ResponseEntity.ok(
                prestamoService.obtenerPrestamosCliente(clienteId));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<Prestamo>> obtenerPendientes() {

        return ResponseEntity.ok(
                prestamoService.obtenerPrestamosPendientes());
    }

    @PutMapping("/aprobar/{idPrestamo}")
    public ResponseEntity<?> aprobarPrestamo(
            @PathVariable Integer idPrestamo) {

        return ResponseEntity.ok(
                prestamoService.aprobarPrestamo(idPrestamo));
    }

    @PutMapping("/rechazar/{idPrestamo}")
    public ResponseEntity<?> rechazarPrestamo(
            @PathVariable Integer idPrestamo) {

        return ResponseEntity.ok(
                prestamoService.rechazarPrestamo(idPrestamo));
    }
}
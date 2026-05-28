package sv.edu.udb.banco.controller;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sv.edu.udb.banco.entity.Empleado;
import sv.edu.udb.banco.entity.Sucursal;
import sv.edu.udb.banco.repository.EmpleadoRepository;
import sv.edu.udb.banco.repository.MovimientoRepository;
import sv.edu.udb.banco.repository.SucursalRepository;

@Controller
@RequestMapping("/gerencia/general")
@Secured("ROLE_GERENTE_GENERAL")
public class GerenciaGeneralController {

    private final SucursalRepository sucursalRepository;
    private final EmpleadoRepository empleadoRepository;
    private final MovimientoRepository movimientoRepository;

    public GerenciaGeneralController(
            final SucursalRepository sucursalRepository,
            final EmpleadoRepository empleadoRepository,
            final MovimientoRepository movimientoRepository
    ) {
        this.sucursalRepository = sucursalRepository;
        this.empleadoRepository = empleadoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    // ─────────────────────────────────────────────
    // SUCURSALES
    // ─────────────────────────────────────────────

    @GetMapping("/sucursales")
    public String listarSucursales(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String estado,
            final Model model
    ) {
        final List<Sucursal> sucursales = sucursalRepository.buscarSucursales(q, estado);
        model.addAttribute("sucursales", sucursales);
        model.addAttribute("q", q);
        model.addAttribute("estado", estado);
        return "pages/General/sucursales";
    }

    @PostMapping("/sucursales")
    @Transactional
    public String crearSucursal(
            @RequestParam final String nombre,
            @RequestParam final String direccion,
            final RedirectAttributes redirectAttributes
    ) {
        try {
            final Sucursal sucursal = new Sucursal();
            sucursal.setNombre(nombre.trim());
            sucursal.setDireccion(direccion.trim());
            sucursal.setEstado("ACTIVO");
            sucursalRepository.save(sucursal);
            redirectAttributes.addFlashAttribute("toastMessage", "Sucursal creada correctamente.");
        } catch (RuntimeException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            redirectAttributes.addFlashAttribute("toastMessage", "No se pudo crear la sucursal.");
        }
        return "redirect:/gerencia/general/sucursales";
    }

    @PostMapping("/sucursales/{id}/inactivar")
    public String inactivarSucursal(
            @PathVariable final Integer id,
            final RedirectAttributes redirectAttributes
    ) {
        try {
            final Sucursal sucursal = sucursalRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada."));
            sucursal.setEstado("INACTIVO");
            sucursalRepository.save(sucursal);
            redirectAttributes.addFlashAttribute("toastMessage", "Sucursal inactivada.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("toastMessage", "No se pudo inactivar la sucursal.");
        }
        return "redirect:/gerencia/general/sucursales";
    }

    // ─────────────────────────────────────────────
    // ASIGNAR GERENTE DE SUCURSAL
    // ─────────────────────────────────────────────

    @GetMapping("/sucursales/{id}/asignar-gerente")
    public String vistaAsignarGerente(
            @PathVariable final Integer id,
            final Model model
    ) {
        final Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada."));

        // Solo empleados con rol GERENTE_SUCURSAL y estado ACTIVO pueden ser asignados
        final List<Empleado> candidatos = empleadoRepository.findAllByRol("GERENTE_SUCURSAL")
                .stream()
                .filter(e -> "ACTIVO".equals(e.getEstado()))
                .toList();

        model.addAttribute("sucursal", sucursal);
        model.addAttribute("candidatos", candidatos);
        return "pages/General/asignar-gerente";
    }

    @PostMapping("/sucursales/{id}/asignar-gerente")
    @Transactional
    public String asignarGerente(
            @PathVariable final Integer id,
            @RequestParam final Integer idEmpleado,
            final RedirectAttributes redirectAttributes
    ) {
        try {
            final Sucursal sucursal = sucursalRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada."));

            final Empleado gerente = empleadoRepository.findById(idEmpleado)
                    .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado."));

            if (!"GERENTE_SUCURSAL".equals(gerente.getRol())) {
                throw new IllegalArgumentException("El empleado seleccionado no tiene rol de Gerente de Sucursal.");
            }

            if (!"ACTIVO".equals(gerente.getEstado())) {
                throw new IllegalArgumentException("El empleado seleccionado no está activo.");
            }

            gerente.setSucursal(sucursal);
            empleadoRepository.save(gerente);

            redirectAttributes.addFlashAttribute("toastMessage",
                    "Gerente \"" + gerente.getNombre() + "\" asignado a sucursal \"" + sucursal.getNombre() + "\".");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("toastMessage", e.getMessage());
        } catch (RuntimeException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            redirectAttributes.addFlashAttribute("toastMessage", "No se pudo asignar el gerente.");
        }
        return "redirect:/gerencia/general/sucursales";
    }

    // ─────────────────────────────────────────────
    // ACCIONES DE PERSONAL (EN ESPERA)
    // ─────────────────────────────────────────────

    @GetMapping("/acciones-personal")
    public String listarAccionesPersonal(
            @RequestParam(required = false) String q,
            final Model model
    ) {
        // Trae todos los empleados en estado EN ESPERA
        final List<EmpleadoRepository.EmpleadoGerenciaRow> pendientes =
                empleadoRepository.findResumenGerencia(q, "EN ESPERA");

        model.addAttribute("pendientes", pendientes);
        model.addAttribute("q", q);
        return "pages/General/acciones-personal";
    }

    @PostMapping("/acciones-personal/{id}/aprobar")
    public String aprobarEmpleado(
            @PathVariable final Integer id,
            final RedirectAttributes redirectAttributes
    ) {
        try {
            final Empleado empleado = empleadoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado."));

            if (!"EN ESPERA".equals(empleado.getEstado())) {
                throw new IllegalArgumentException("El empleado no está en estado EN ESPERA.");
            }

            empleado.setEstado("ACTIVO");
            empleadoRepository.save(empleado);
            redirectAttributes.addFlashAttribute("toastMessage",
                    "Empleado \"" + empleado.getNombre() + "\" aprobado y activado.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("toastMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("toastMessage", "No se pudo aprobar el empleado.");
        }
        return "redirect:/gerencia/general/acciones-personal";
    }

    @PostMapping("/acciones-personal/{id}/rechazar")
    public String rechazarEmpleado(
            @PathVariable final Integer id,
            final RedirectAttributes redirectAttributes
    ) {
        try {
            final Empleado empleado = empleadoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado."));

            if (!"EN ESPERA".equals(empleado.getEstado())) {
                throw new IllegalArgumentException("El empleado no está en estado EN ESPERA.");
            }

            empleado.setEstado("INACTIVO");
            empleadoRepository.save(empleado);
            redirectAttributes.addFlashAttribute("toastMessage",
                    "Empleado \"" + empleado.getNombre() + "\" rechazado.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("toastMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("toastMessage", "No se pudo rechazar el empleado.");
        }
        return "redirect:/gerencia/general/acciones-personal";
    }

    // ─────────────────────────────────────────────
    // MOVIMIENTOS GLOBALES
    // ─────────────────────────────────────────────

    @GetMapping("/movimientos")
    public String listarMovimientosGlobal(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin,
            final Model model
    ) {
        final List<MovimientoRepository.MovimientoGlobalRow> movimientos =
                movimientoRepository.findMovimientosGlobal(q, fechaInicio, fechaFin);

        BigDecimal totalDepositos = BigDecimal.ZERO;
        BigDecimal totalRetiros = BigDecimal.ZERO;
        for (MovimientoRepository.MovimientoGlobalRow mov : movimientos) {
            if (mov == null || mov.getMonto() == null || mov.getTipo() == null) {
                continue;
            }
            if ("DEPOSITO".equalsIgnoreCase(mov.getTipo())) {
                totalDepositos = totalDepositos.add(mov.getMonto());
            } else if ("RETIRO".equalsIgnoreCase(mov.getTipo())) {
                totalRetiros = totalRetiros.add(mov.getMonto());
            }
        }

        model.addAttribute("movimientos", movimientos);
        model.addAttribute("totalDepositos", totalDepositos);
        model.addAttribute("totalRetiros", totalRetiros);
        model.addAttribute("q", q);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        return "pages/General/movimientos";
    }
}

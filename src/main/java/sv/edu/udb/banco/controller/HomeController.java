package sv.edu.udb.banco.controller;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sv.edu.udb.banco.entity.Cliente;
import sv.edu.udb.banco.entity.Cuenta;
import sv.edu.udb.banco.entity.Movimiento;
import sv.edu.udb.banco.entity.Prestamo;
import sv.edu.udb.banco.repository.ClienteRepository;
import sv.edu.udb.banco.repository.CuentaRepository;
import sv.edu.udb.banco.repository.EmpleadoRepository;
import sv.edu.udb.banco.repository.MovimientoRepository;
import sv.edu.udb.banco.repository.PrestamoRepository;
import sv.edu.udb.banco.service.TransferenciaService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private final ClienteRepository clienteRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PrestamoRepository prestamoRepository;
    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;
    private final TransferenciaService transferenciaService;

    public HomeController(
            final ClienteRepository clienteRepository,
            final EmpleadoRepository empleadoRepository,
            final PrestamoRepository prestamoRepository,
            final CuentaRepository cuentaRepository,
            final MovimientoRepository movimientoRepository,
            final TransferenciaService transferenciaService
    ) {
        this.clienteRepository = clienteRepository;
        this.empleadoRepository = empleadoRepository;
        this.prestamoRepository = prestamoRepository;
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
        this.transferenciaService = transferenciaService;
    }

    @GetMapping("/home")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "pages/dashboard";
    }

    @GetMapping("/cliente/inicio")
    public String clienteInicio() {
        return "pages/dashboard";
    }

    @GetMapping("/movimientos")
    public String movimientos(
            @RequestParam(required = false) final Integer cuentaId,
            @RequestParam(required = false) final LocalDate desde,
            @RequestParam(required = false) final LocalDate hasta,
            final Model model
    ) {
        final List<Cuenta> cuentas = cuentaRepository.findAll(Sort.by(Sort.Direction.ASC, "idCuenta"));
        model.addAttribute("cuentasMovimiento", cuentas);

        final LocalDate fechaDesde = desde == null ? LocalDate.now().minusMonths(1) : desde;
        final LocalDate fechaHasta = hasta == null ? LocalDate.now() : hasta;
        model.addAttribute("desdeMovimiento", fechaDesde);
        model.addAttribute("hastaMovimiento", fechaHasta);

        if (cuentas.isEmpty()) {
            model.addAttribute("movimientos", List.of());
            return "pages/movimientos";
        }

        final Integer cuentaSeleccionadaId = cuentaId == null ? cuentas.get(0).getIdCuenta() : cuentaId;
        model.addAttribute("cuentaSeleccionadaId", cuentaSeleccionadaId);

        final List<Movimiento> movimientos = movimientoRepository
                .findAllByCuenta_IdCuentaAndFechaBetweenOrderByFechaDescIdMovimientoDesc(cuentaSeleccionadaId, fechaDesde, fechaHasta);
        model.addAttribute("movimientos", movimientos);
        return "pages/movimientos";
    }

    @GetMapping("/transferencias")
    public String transferencias(
            @RequestParam(required = false) final String dui,
            @RequestParam(required = false) final Integer cuentaId,
            @RequestParam(required = false, defaultValue = "DEPOSITO") final String tipo,
            @RequestParam(required = false, defaultValue = "0") final BigDecimal monto,
            final Model model
    ) {
        final String duiBusqueda = dui == null ? "" : dui.trim();
        model.addAttribute("duiBusqueda", duiBusqueda);

        final boolean isDeposito = !"RETIRO".equalsIgnoreCase(tipo);
        model.addAttribute("tipoOperacion", isDeposito ? "DEPOSITO" : "RETIRO");
        model.addAttribute("montoOperacion", monto.max(BigDecimal.ZERO));

        if (duiBusqueda.isEmpty()) {
            model.addAttribute("clienteEncontrado", false);
            model.addAttribute("cuentasCliente", List.of());
            model.addAttribute("movimientosRecientes", List.of());
            model.addAttribute("fechaOperacion", LocalDate.now());
            return "pages/transferencias";
        }

        final Optional<Cliente> clienteOpt = clienteRepository.findByDui(duiBusqueda);
        if (clienteOpt.isEmpty()) {
            model.addAttribute("clienteEncontrado", false);
            model.addAttribute("cuentasCliente", List.of());
            model.addAttribute("movimientosRecientes", List.of());
            model.addAttribute("fechaOperacion", LocalDate.now());
            return "pages/transferencias";
        }

        final Cliente cliente = clienteOpt.get();
        final List<Cuenta> cuentas = cuentaRepository.findAllByCliente_Id(cliente.getId());
        model.addAttribute("clienteEncontrado", true);
        model.addAttribute("cliente", cliente);
        model.addAttribute("cuentasCliente", cuentas);
        model.addAttribute("fechaOperacion", LocalDate.now());

        Cuenta cuentaSeleccionada = null;
        if (cuentaId != null) {
            cuentaSeleccionada = cuentas.stream()
                    .filter(c -> c.getIdCuenta().equals(cuentaId))
                    .findFirst()
                    .orElse(null);
        }

        if (cuentaSeleccionada == null && !cuentas.isEmpty()) {
            cuentaSeleccionada = cuentas.get(0);
        }

        if (cuentaSeleccionada != null) {
            model.addAttribute("cuentaSeleccionada", cuentaSeleccionada);
            final List<Movimiento> recientes = movimientoRepository
                    .findTop5ByCuenta_IdCuentaOrderByFechaDescIdMovimientoDesc(cuentaSeleccionada.getIdCuenta());
            model.addAttribute("movimientosRecientes", recientes);
        } else {
            model.addAttribute("cuentaSeleccionada", null);
            model.addAttribute("movimientosRecientes", List.of());
        }

        return "pages/transferencias";
    }

    @PostMapping("/transferencias/procesar")
    public String procesarTransferencia(
            @RequestParam final Integer cuentaId,
            @RequestParam final String tipo,
            @RequestParam final BigDecimal monto,
            @RequestParam final String dui,
            final RedirectAttributes redirectAttributes
    ) {
        try {
            transferenciaService.procesarMovimiento(cuentaId, tipo, monto);
            redirectAttributes.addFlashAttribute("toastMessage", "Movimiento procesado correctamente.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("toastMessage", exception.getMessage());
        }

        return "redirect:/transferencias?dui=" + dui + "&cuentaId=" + cuentaId + "&tipo=" + tipo + "&monto=" + monto;
    }

    @GetMapping("/prestamos")
    public String prestamos(final Model model) {
        final List<Prestamo> prestamos = prestamoRepository.findAllByOrderByIdPrestamoDesc();
        model.addAttribute("prestamos", prestamos);
        return "pages/prestamos";
    }

    @GetMapping("/pagos")
    public String pagosLegacy() {
        return "redirect:/prestamos";
    }

    @GetMapping("/gerencia/clientes")
    public String clientesGerencia(
            @RequestParam(required = false) final String q,
            @RequestParam(required = false) final String estado,
            final Model model
    ) {
        final String estadoNormalizado = normalizarFiltroEstado(estado);
        final List<ClienteRepository.ClienteGerenciaRow> clientes = clienteRepository.findResumenGerencia(q, estadoNormalizado);
        model.addAttribute("clientes", clientes);
        model.addAttribute("qClientes", q == null ? "" : q);
        model.addAttribute("estadoClientes", estado == null ? "" : estado);
        return "pages/clientes";
    }

    @GetMapping("/gerencia/empleados")
    public String empleadosGerencia(
            @RequestParam(required = false) final String q,
            @RequestParam(required = false) final String estado,
            final Model model
    ) {
        final String estadoNormalizado = normalizarFiltroEstado(estado);
        model.addAttribute("empleados", empleadoRepository.findResumenGerencia(q, estadoNormalizado));
        model.addAttribute("qEmpleados", q == null ? "" : q);
        model.addAttribute("estadoEmpleados", estado == null ? "" : estado);
        return "pages/empleados";
    }

    @GetMapping("/gerencia/aprobacion-creditos")
    public String aprobacionCreditosGerencia(
            @RequestParam(required = false) final Integer prestamoId,
            final Model model
    ) {
        final List<Prestamo> prestamos = prestamoRepository.findAllByOrderByIdPrestamoDesc();
        model.addAttribute("prestamosGerencia", prestamos);

        final Optional<Prestamo> seleccionadoPorId = prestamos.stream()
                .filter(prestamo -> prestamoId != null && prestamoId.equals(prestamo.getIdPrestamo()))
                .findFirst();

        final Optional<Prestamo> pendiente = prestamos.stream()
                .filter(p -> esPendiente(p.getEstado()))
                .findFirst();

        model.addAttribute("prestamoSeleccionado",
                seleccionadoPorId.orElseGet(() -> pendiente.orElse(prestamos.isEmpty() ? null : prestamos.get(0))));
        return "pages/aprobacion-creditos";
    }

    @GetMapping("/configuracion")
    public String configuracionLegacy() {
        return "redirect:/gerencia/clientes";
    }

    private boolean esPendiente(final String estado) {
        return estado == null
                || "EN ESPERA".equalsIgnoreCase(estado)
                || "PENDIENTE".equalsIgnoreCase(estado);
    }

    private String normalizarFiltroEstado(final String estado) {
        if (estado == null || estado.isBlank()) {
            return null;
        }

        return switch (estado.trim().toUpperCase()) {
            case "ACTIVOS" -> "ACTIVO";
            case "INACTIVOS" -> "INACTIVO";
            default -> estado.trim();
        };
    }
}

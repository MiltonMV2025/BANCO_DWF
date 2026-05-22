package sv.edu.udb.banco.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sv.edu.udb.banco.entity.Cliente;
import sv.edu.udb.banco.entity.Empleado;
import sv.edu.udb.banco.entity.Prestamo;
import sv.edu.udb.banco.repository.ClienteRepository;
import sv.edu.udb.banco.repository.EmpleadoRepository;
import sv.edu.udb.banco.repository.PrestamoRepository;

import java.math.BigDecimal;

@Controller
public class GerenciaController {

    private final ClienteRepository clienteRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PrestamoRepository prestamoRepository;

    public GerenciaController(
            final ClienteRepository clienteRepository,
            final EmpleadoRepository empleadoRepository,
            final PrestamoRepository prestamoRepository
    ) {
        this.clienteRepository = clienteRepository;
        this.empleadoRepository = empleadoRepository;
        this.prestamoRepository = prestamoRepository;
    }

    @PostMapping("/gerencia/clientes")
    public String crearCliente(
            @RequestParam final String nombre,
            @RequestParam final String dui,
            @RequestParam final BigDecimal salario,
            @RequestParam final String estado,
            final RedirectAttributes redirectAttributes
    ) {
        try {
            final Cliente cliente = new Cliente();
            cliente.setNombre(nombre.trim());
            cliente.setDui(dui.trim());
            cliente.setSalario(salario.max(BigDecimal.ZERO));
            cliente.setEstado(normalizarEstado(estado, "ACTIVO"));
            clienteRepository.save(cliente);
            redirectAttributes.addFlashAttribute("toastMessage", "Cliente creado correctamente.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("toastMessage", "No se pudo crear el cliente: revisa DUI y datos.");
        }
        return "redirect:/gerencia/clientes";
    }

    @PostMapping("/gerencia/clientes/{id}")
    public String editarCliente(
            @PathVariable final Integer id,
            @RequestParam final String nombre,
            @RequestParam final String dui,
            @RequestParam final BigDecimal salario,
            @RequestParam final String estado,
            final RedirectAttributes redirectAttributes
    ) {
        try {
            final Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));

            cliente.setNombre(nombre.trim());
            cliente.setDui(dui.trim());
            cliente.setSalario(salario.max(BigDecimal.ZERO));
            cliente.setEstado(normalizarEstado(estado, cliente.getEstado()));
            clienteRepository.save(cliente);
            redirectAttributes.addFlashAttribute("toastMessage", "Cliente actualizado.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("toastMessage", "No se pudo actualizar el cliente.");
        }
        return "redirect:/gerencia/clientes";
    }

    @PostMapping("/gerencia/clientes/{id}/inactivar")
    public String inactivarCliente(
            @PathVariable final Integer id,
            final RedirectAttributes redirectAttributes
    ) {
        try {
            final Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));
            cliente.setEstado("INACTIVO");
            clienteRepository.save(cliente);
            redirectAttributes.addFlashAttribute("toastMessage", "Cliente inactivado.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("toastMessage", "No se pudo inactivar el cliente.");
        }
        return "redirect:/gerencia/clientes";
    }

    @PostMapping("/gerencia/empleados")
    public String crearEmpleado(
            @RequestParam final String nombre,
            @RequestParam final String rol,
            @RequestParam final String estado,
            final RedirectAttributes redirectAttributes
    ) {
        try {
            final Empleado empleado = new Empleado();
            empleado.setNombre(nombre.trim());
            empleado.setRol(rol.trim());
            empleado.setEstado(normalizarEstado(estado, "ACTIVO"));
            empleadoRepository.save(empleado);
            redirectAttributes.addFlashAttribute("toastMessage", "Empleado creado correctamente.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("toastMessage", "No se pudo crear el empleado.");
        }
        return "redirect:/gerencia/empleados";
    }

    @PostMapping("/gerencia/empleados/{id}")
    public String editarEmpleado(
            @PathVariable final Integer id,
            @RequestParam final String nombre,
            @RequestParam final String rol,
            @RequestParam final String estado,
            final RedirectAttributes redirectAttributes
    ) {
        try {
            final Empleado empleado = empleadoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado."));

            empleado.setNombre(nombre.trim());
            empleado.setRol(rol.trim());
            empleado.setEstado(normalizarEstado(estado, empleado.getEstado()));
            empleadoRepository.save(empleado);
            redirectAttributes.addFlashAttribute("toastMessage", "Empleado actualizado.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("toastMessage", "No se pudo actualizar el empleado.");
        }
        return "redirect:/gerencia/empleados";
    }

    @PostMapping("/gerencia/empleados/{id}/inactivar")
    public String inactivarEmpleado(
            @PathVariable final Integer id,
            final RedirectAttributes redirectAttributes
    ) {
        try {
            final Empleado empleado = empleadoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado."));
            empleado.setEstado("INACTIVO");
            empleadoRepository.save(empleado);
            redirectAttributes.addFlashAttribute("toastMessage", "Empleado inactivado.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("toastMessage", "No se pudo inactivar el empleado.");
        }
        return "redirect:/gerencia/empleados";
    }

    @PostMapping("/gerencia/aprobacion-creditos/{id}/estado")
    @Transactional
    public String actualizarEstadoPrestamo(
            @PathVariable final Integer id,
            @RequestParam final String estado,
            final RedirectAttributes redirectAttributes
    ) {
        try {
            final Prestamo prestamo = prestamoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado."));

            final String estadoNormalizado = normalizarEstado(estado, prestamo.getEstado());
            prestamo.setEstado(estadoNormalizado);
            prestamoRepository.save(prestamo);
            redirectAttributes.addFlashAttribute("toastMessage", "Estado actualizado a " + estadoNormalizado + ".");
            return "redirect:/gerencia/aprobacion-creditos?prestamoId=" + prestamo.getIdPrestamo();
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("toastMessage", "No se pudo actualizar el estado del préstamo.");
            return "redirect:/gerencia/aprobacion-creditos";
        }
    }

    private String normalizarEstado(final String estado, final String porDefecto) {
        if (estado == null || estado.isBlank()) {
            return porDefecto;
        }

        return switch (estado.trim().toUpperCase()) {
            case "A", "ACTIVO", "ACTIVOS" -> "ACTIVO";
            case "I", "INACTIVO", "INACTIVOS" -> "INACTIVO";
            case "MONITOREO" -> "MONITOREO";
            case "EN ESPERA", "PENDIENTE" -> "EN ESPERA";
            case "APROBADO" -> "APROBADO";
            case "RECHAZADO" -> "RECHAZADO";
            case "EN CAPACITACION", "EN CAPACITACIÓN" -> "EN CAPACITACION";
            default -> estado.trim().toUpperCase();
        };
    }
}

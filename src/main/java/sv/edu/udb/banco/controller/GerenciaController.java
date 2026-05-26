package sv.edu.udb.banco.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sv.edu.udb.banco.entity.Cliente;
import sv.edu.udb.banco.entity.Cuenta;
import sv.edu.udb.banco.entity.Empleado;
import sv.edu.udb.banco.entity.Prestamo;
import sv.edu.udb.banco.entity.Rol;
import sv.edu.udb.banco.entity.Usuario;
import sv.edu.udb.banco.repository.ClienteRepository;
import sv.edu.udb.banco.repository.CuentaRepository;
import sv.edu.udb.banco.repository.EmpleadoRepository;
import sv.edu.udb.banco.repository.PrestamoRepository;
import sv.edu.udb.banco.repository.RolRepository;
import sv.edu.udb.banco.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Controller
public class GerenciaController {

    private final ClienteRepository clienteRepository;
    private final CuentaRepository cuentaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PrestamoRepository prestamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;

    public GerenciaController(
            final ClienteRepository clienteRepository,
            final CuentaRepository cuentaRepository,
            final EmpleadoRepository empleadoRepository,
            final PrestamoRepository prestamoRepository,
            final UsuarioRepository usuarioRepository,
            final RolRepository rolRepository,
            final PasswordEncoder passwordEncoder,
            final TransactionTemplate transactionTemplate
    ) {
        this.clienteRepository = clienteRepository;
        this.cuentaRepository = cuentaRepository;
        this.empleadoRepository = empleadoRepository;
        this.prestamoRepository = prestamoRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.transactionTemplate = transactionTemplate;
    }

    @PostMapping("/gerencia/clientes")
    @Transactional
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
            final Cliente clienteGuardado = clienteRepository.save(cliente);

            final Cuenta cuentaBase = new Cuenta();
            cuentaBase.setCliente(clienteGuardado);
            cuentaBase.setTipo("AHORROS");
            cuentaBase.setSaldo(BigDecimal.ZERO);
            cuentaBase.setFechaCreacion(LocalDate.now());
            cuentaBase.setNumeroCuenta(generarNumeroCuenta(clienteGuardado.getId()));
            cuentaRepository.save(cuentaBase);

            redirectAttributes.addFlashAttribute("toastMessage", "Cliente creado correctamente.");
        } catch (RuntimeException exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
    @Transactional
    public String crearEmpleado(
            @RequestParam final String nombre,
            @RequestParam final String dui,
            @RequestParam final String rol,
            @RequestParam final String password,
            @RequestParam final String confirmarPassword,
            @RequestParam final String estado,
            final RedirectAttributes redirectAttributes
    ) {
        try {
            final String duiNormalizado = normalizarDui(dui);
            if (duiNormalizado.isBlank()) {
                redirectAttributes.addFlashAttribute("toastMessage", "DUI invÃ¡lido. Formato esperado: 12345678-9.");
                return "redirect:/gerencia/empleados";
            }

            if (empleadoRepository.existsByDuiIgnoreCase(duiNormalizado)
                    || usuarioRepository.existsByUsernameIgnoreCase(duiNormalizado)) {
                redirectAttributes.addFlashAttribute("toastMessage", "Ya existe un empleado con ese DUI.");
                return "redirect:/gerencia/empleados";
            }

            if (password == null || password.trim().length() < 6) {
                redirectAttributes.addFlashAttribute("toastMessage", "La contraseÃ±a debe tener al menos 6 caracteres.");
                return "redirect:/gerencia/empleados";
            }

            if (!password.trim().equals(confirmarPassword == null ? "" : confirmarPassword.trim())) {
                redirectAttributes.addFlashAttribute("toastMessage", "La confirmaciÃ³n de contraseÃ±a no coincide.");
                return "redirect:/gerencia/empleados";
            }

            final Rol rolEmpleado = resolverRolActivo(rol);
            final Empleado empleado = new Empleado();
            empleado.setNombre(nombre.trim());
            empleado.setDui(duiNormalizado);
            empleado.setRol(rolEmpleado.getCodigo());
            empleado.setEstado(normalizarEstado(estado, "ACTIVO"));
            empleadoRepository.save(empleado);

            final Usuario usuario = new Usuario();
            usuario.setUsername(duiNormalizado);
            usuario.setPasswordHash(passwordEncoder.encode(password.trim()));
            usuario.setEstado(toEstadoUsuario(empleado.getEstado()));
            usuario.setRoles(Set.of(rolEmpleado));
            usuarioRepository.save(usuario);

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
            @RequestParam final String dui,
            @RequestParam final String rol,
            @RequestParam(required = false) final String password,
            @RequestParam(required = false) final String confirmarPassword,
            @RequestParam final String estado,
            final RedirectAttributes redirectAttributes
    ) {
        try {
            transactionTemplate.executeWithoutResult(status -> {
                final Empleado empleado = empleadoRepository.findById(id)
                        .orElseThrow(() -> new UserFacingException("Empleado no encontrado."));

                final String duiAnterior = empleado.getDui();
                final String duiNormalizado = normalizarDui(dui);
                if (duiNormalizado.isBlank()) {
                    throw new UserFacingException("DUI inválido. Formato esperado: 12345678-9.");
                }

                final boolean existeEnOtroEmpleado = empleadoRepository.findByDuiIgnoreCase(duiNormalizado)
                        .map(e -> !e.getIdEmpleado().equals(id))
                        .orElse(false);
                if (existeEnOtroEmpleado) {
                    throw new UserFacingException("Ya existe otro empleado con ese DUI.");
                }

                final String passwordLimpio = password == null ? "" : password.trim();
                final String confirmarLimpio = confirmarPassword == null ? "" : confirmarPassword.trim();
                if (!passwordLimpio.isBlank() && passwordLimpio.length() < 6) {
                    throw new UserFacingException("La contraseña debe tener al menos 6 caracteres.");
                }
                if ((!passwordLimpio.isBlank() || !confirmarLimpio.isBlank()) && !passwordLimpio.equals(confirmarLimpio)) {
                    throw new UserFacingException("La confirmación de contraseña no coincide.");
                }

                final Rol rolEmpleado = resolverRolActivo(rol);
                empleado.setNombre(nombre.trim());
                empleado.setDui(duiNormalizado);
                empleado.setRol(rolEmpleado.getCodigo());
                empleado.setEstado(normalizarEstado(estado, empleado.getEstado()));
                empleadoRepository.save(empleado);

                final Usuario usuario = buscarUsuarioEmpleado(duiAnterior, id)
                        .orElseThrow(() -> new UserFacingException("No se encontró usuario de acceso para el empleado."));

                if (!usuario.getUsername().equalsIgnoreCase(duiNormalizado)
                        && usuarioRepository.existsByUsernameIgnoreCase(duiNormalizado)) {
                    throw new UserFacingException("El DUI ya está en uso por otro usuario.");
                }

                usuario.setUsername(duiNormalizado);
                if (!passwordLimpio.isBlank()) {
                    usuario.setPasswordHash(passwordEncoder.encode(passwordLimpio));
                }
                usuario.setEstado(toEstadoUsuario(empleado.getEstado()));
                usuario.setRoles(Set.of(rolEmpleado));
                usuarioRepository.save(usuario);
            });

            redirectAttributes.addFlashAttribute("toastMessage", "Empleado actualizado.");
        } catch (UserFacingException exception) {
            redirectAttributes.addFlashAttribute("toastMessage", exception.getMessage());
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("toastMessage", "No se pudo actualizar el empleado.");
        }
        return "redirect:/gerencia/empleados";
    }
    @PostMapping("/gerencia/empleados/{id}/inactivar")
    @Transactional
    public String inactivarEmpleado(
            @PathVariable final Integer id,
            final RedirectAttributes redirectAttributes
    ) {
        try {
            final Empleado empleado = empleadoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado."));
            empleado.setEstado("INACTIVO");
            empleadoRepository.save(empleado);

            buscarUsuarioEmpleado(empleado.getDui(), id).ifPresent(usuario -> {
                usuario.setEstado("I");
                usuarioRepository.save(usuario);
            });

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
                    .orElseThrow(() -> new IllegalArgumentException("PrÃ©stamo no encontrado."));

            final String estadoNormalizado = normalizarEstado(estado, prestamo.getEstado());
            prestamo.setEstado(estadoNormalizado);
            prestamoRepository.save(prestamo);
            redirectAttributes.addFlashAttribute("toastMessage", "Estado actualizado a " + estadoNormalizado + ".");
            return "redirect:/gerencia/aprobacion-creditos?prestamoId=" + prestamo.getIdPrestamo();
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("toastMessage", "No se pudo actualizar el estado del prÃ©stamo.");
            return "redirect:/gerencia/aprobacion-creditos";
        }
    }

    private java.util.Optional<Usuario> buscarUsuarioEmpleado(final String duiEmpleado, final Integer idEmpleado) {
        final String duiNormalizado = normalizarDui(duiEmpleado);
        if (!duiNormalizado.isBlank()) {
            final java.util.Optional<Usuario> usuarioPorDui = usuarioRepository.findByUsername(duiNormalizado);
            if (usuarioPorDui.isPresent()) {
                return usuarioPorDui;
            }
        }
        return usuarioRepository.findByUsername(generarUsernameEmpleado(idEmpleado));
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
            case "EN CAPACITACION", "EN CAPACITACIÃ“N" -> "EN CAPACITACION";
            default -> estado.trim().toUpperCase();
        };
    }

    private String normalizarDui(final String dui) {
        if (dui == null) {
            return "";
        }
        final String soloDigitos = dui.replaceAll("[^0-9]", "");
        if (soloDigitos.length() != 9) {
            return "";
        }
        return soloDigitos.substring(0, 8) + "-" + soloDigitos.substring(8);
    }

    private String generarNumeroCuenta(final Integer idCliente) {
        final String correlativo = String.format("%08d", idCliente == null ? 0 : idCliente);
        return "AHO-" + correlativo;
    }

    private Rol resolverRolActivo(final String valorRol) {
        if (valorRol == null || valorRol.isBlank()) {
            throw new IllegalArgumentException("Debes seleccionar un rol.");
        }

        return rolRepository.findAllByEstadoOrderByNombreAsc("A").stream()
                .filter(rol -> rol.getCodigo().equalsIgnoreCase(valorRol.trim())
                        || rol.getNombre().equalsIgnoreCase(valorRol.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Rol no vÃ¡lido."));
    }

    private String generarUsernameEmpleado(final Integer idEmpleado) {
        return "emp-" + (idEmpleado == null ? 0 : idEmpleado);
    }

    private String toEstadoUsuario(final String estadoEmpleado) {
        return "ACTIVO".equalsIgnoreCase(estadoEmpleado) ? "A" : "I";
    }

    private static class UserFacingException extends RuntimeException {
        UserFacingException(final String message) {
            super(message);
        }
    }
}


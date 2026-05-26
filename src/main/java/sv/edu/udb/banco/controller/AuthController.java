package sv.edu.udb.banco.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sv.edu.udb.banco.entity.Cliente;
import sv.edu.udb.banco.entity.Cuenta;
import sv.edu.udb.banco.entity.Rol;
import sv.edu.udb.banco.entity.Usuario;
import sv.edu.udb.banco.repository.ClienteRepository;
import sv.edu.udb.banco.repository.CuentaRepository;
import sv.edu.udb.banco.repository.RolRepository;
import sv.edu.udb.banco.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Controller
public class AuthController {

    private final ClienteRepository clienteRepository;
    private final CuentaRepository cuentaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            final ClienteRepository clienteRepository,
            final CuentaRepository cuentaRepository,
            final UsuarioRepository usuarioRepository,
            final RolRepository rolRepository,
            final PasswordEncoder passwordEncoder
    ) {
        this.clienteRepository = clienteRepository;
        this.cuentaRepository = cuentaRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "pages/login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "pages/registro";
    }

    @PostMapping("/registro")
    @Transactional
    public String registrarCliente(
            @RequestParam final String nombre,
            @RequestParam final String dui,
            @RequestParam final BigDecimal salario,
            @RequestParam final String password,
            @RequestParam final String confirmarPassword,
            final RedirectAttributes redirectAttributes
    ) {
        try {
            if (nombre == null || nombre.isBlank()) {
                redirectAttributes.addFlashAttribute("registroError", "Debes ingresar tu nombre.");
                return "redirect:/registro";
            }

            final String duiNormalizado = normalizarDui(dui);
            if (duiNormalizado.isBlank()) {
                redirectAttributes.addFlashAttribute("registroError", "DUI inválido. Formato esperado: 12345678-9.");
                return "redirect:/registro";
            }

            if (password == null || password.trim().length() < 6) {
                redirectAttributes.addFlashAttribute("registroError", "La contraseña debe tener al menos 6 caracteres.");
                return "redirect:/registro";
            }

            if (!password.trim().equals(confirmarPassword == null ? "" : confirmarPassword.trim())) {
                redirectAttributes.addFlashAttribute("registroError", "La confirmación de contraseña no coincide.");
                return "redirect:/registro";
            }

            if (clienteRepository.findByDui(duiNormalizado).isPresent()
                    || usuarioRepository.existsByUsernameIgnoreCase(duiNormalizado)) {
                redirectAttributes.addFlashAttribute("registroError", "Ya existe un cliente registrado con ese DUI.");
                return "redirect:/registro";
            }

            final Rol rolCliente = rolRepository.findByCodigoAndEstado("ROLE_CLIENTE", "A")
                    .orElseThrow(() -> new IllegalStateException("No se encontró ROLE_CLIENTE activo."));

            final Cliente cliente = new Cliente();
            cliente.setNombre(nombre.trim());
            cliente.setDui(duiNormalizado);
            cliente.setSalario(salario == null ? BigDecimal.ZERO : salario.max(BigDecimal.ZERO));
            cliente.setEstado("ACTIVO");
            final Cliente clienteGuardado = clienteRepository.save(cliente);

            final Cuenta cuentaBase = new Cuenta();
            cuentaBase.setCliente(clienteGuardado);
            cuentaBase.setTipo("AHORROS");
            cuentaBase.setSaldo(BigDecimal.ZERO);
            cuentaBase.setFechaCreacion(LocalDate.now());
            cuentaBase.setNumeroCuenta("AHO-" + String.format("%08d", clienteGuardado.getId()));
            cuentaRepository.save(cuentaBase);

            final Usuario usuario = new Usuario();
            usuario.setUsername(duiNormalizado);
            usuario.setPasswordHash(passwordEncoder.encode(password.trim()));
            usuario.setEstado("A");
            usuario.setRoles(Set.of(rolCliente));
            usuarioRepository.save(usuario);

            redirectAttributes.addFlashAttribute("registroSuccess", "Cuenta creada. Ahora iniciá sesión.");
            return "redirect:/login";
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("registroError", "No se pudo completar el registro.");
            return "redirect:/registro";
        }
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
}

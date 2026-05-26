package sv.edu.udb.banco.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import sv.edu.udb.banco.repository.ClienteRepository;
import sv.edu.udb.banco.repository.EmpleadoRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
@Component
public class ViewRoleModelAdvice {

    private final ClienteRepository clienteRepository;
    private final EmpleadoRepository empleadoRepository;

    public ViewRoleModelAdvice(
            final ClienteRepository clienteRepository,
            final EmpleadoRepository empleadoRepository
    ) {
        this.clienteRepository = clienteRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @ModelAttribute
    public void addRoleFlags(final Model model, final Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            model.addAttribute("showUsuarioMenu", false);
            model.addAttribute("showGerenciaMenu", false);
            model.addAttribute("displayUserName", "Invitado");
            model.addAttribute("displayUserInitials", "IN");
            return;
        }

        final Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        final boolean isGerente = roles.contains("ROLE_GERENTE_SUCURSAL")
                || roles.contains("ROLE_GERENTE_GENERAL");

        boolean isUsuario = roles.contains("ROLE_CLIENTE")
                || roles.contains("ROLE_CAJERO")
                || roles.contains("ROLE_DEPENDIENTE");

        if (!isGerente && !isUsuario) {
            isUsuario = true;
        }

        final String loginDui = normalizarDui(authentication.getName());
        final String nombreMostrado = resolverNombreMostrado(loginDui).orElse(authentication.getName());

        model.addAttribute("showUsuarioMenu", isUsuario);
        model.addAttribute("showGerenciaMenu", isGerente);
        model.addAttribute("displayUserName", nombreMostrado);
        model.addAttribute("displayUserInitials", construirIniciales(nombreMostrado));
    }

    private Optional<String> resolverNombreMostrado(final String duiNormalizado) {
        if (duiNormalizado.isBlank()) {
            return Optional.empty();
        }

        final Optional<String> nombreCliente = clienteRepository.findByDui(duiNormalizado)
                .map(cliente -> cliente.getNombre() == null ? "" : cliente.getNombre().trim())
                .filter(nombre -> !nombre.isBlank());
        if (nombreCliente.isPresent()) {
            return nombreCliente;
        }

        return empleadoRepository.findByDuiIgnoreCase(duiNormalizado)
                .map(empleado -> empleado.getNombre() == null ? "" : empleado.getNombre().trim())
                .filter(nombre -> !nombre.isBlank());
    }

    private String normalizarDui(final String valor) {
        if (valor == null) {
            return "";
        }

        final String soloDigitos = valor.replaceAll("[^0-9]", "");
        if (soloDigitos.length() != 9) {
            return valor.trim();
        }

        return soloDigitos.substring(0, 8) + "-" + soloDigitos.substring(8);
    }

    private String construirIniciales(final String username) {
        if (username == null || username.isBlank()) {
            return "US";
        }

        final String normalizado = username.trim().replaceAll("[^A-Za-z0-9 ]", " ");
        final String[] partes = normalizado.split("\\s+");
        if (partes.length == 1) {
            return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
        }

        final String primera = partes[0].isEmpty() ? "" : partes[0].substring(0, 1);
        final String segunda = partes[1].isEmpty() ? "" : partes[1].substring(0, 1);
        return (primera + segunda).toUpperCase();
    }
}

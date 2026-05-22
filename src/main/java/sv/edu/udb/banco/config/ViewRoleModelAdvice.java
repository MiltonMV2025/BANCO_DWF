package sv.edu.udb.banco.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class ViewRoleModelAdvice {

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

        model.addAttribute("showUsuarioMenu", isUsuario);
        model.addAttribute("showGerenciaMenu", isGerente);
        model.addAttribute("displayUserName", authentication.getName());
        model.addAttribute("displayUserInitials", construirIniciales(authentication.getName()));
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

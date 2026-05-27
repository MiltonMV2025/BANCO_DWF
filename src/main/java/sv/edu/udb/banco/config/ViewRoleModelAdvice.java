package sv.edu.udb.banco.config;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ViewRoleModelAdvice {

    @ModelAttribute
    public void addRoleFlags(final Model model, final Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            model.addAttribute("showUsuarioMenu", false);
            model.addAttribute("showGerenciaMenu", false);
            model.addAttribute("showGerenteGeneralMenu", false);
            model.addAttribute("displayUserName", "Invitado");
            model.addAttribute("displayUserInitials", "IN");
            return;
        }

        final Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        final boolean isGerenteSucursal = roles.contains("ROLE_GERENTE_SUCURSAL");
        final boolean isGerenteGeneral  = roles.contains("ROLE_GERENTE_GENERAL");

        boolean isUsuario = roles.contains("ROLE_CLIENTE")
                || roles.contains("ROLE_CAJERO")
                || roles.contains("ROLE_DEPENDIENTE");

        if (!isGerenteSucursal && !isGerenteGeneral && !isUsuario) {
            isUsuario = true;
        }

        model.addAttribute("showUsuarioMenu", isUsuario);
        model.addAttribute("showGerenciaMenu", isGerenteSucursal);
        model.addAttribute("showGerenteGeneralMenu", isGerenteGeneral);
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
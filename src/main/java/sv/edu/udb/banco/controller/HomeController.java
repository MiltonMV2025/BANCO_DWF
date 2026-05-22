package sv.edu.udb.banco.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "pages/dashboard";
    }

    @GetMapping("/movimientos")
    public String movimientos() {
        return "pages/movimientos";
    }

    @GetMapping("/transferencias")
    public String transferencias() {
        return "pages/transferencias";
    }

    @GetMapping("/prestamos")
    public String prestamos() {
        return "pages/prestamos";
    }

    @GetMapping("/pagos")
    public String pagosLegacy() {
        return "redirect:/prestamos";
    }

    @GetMapping("/gerencia/clientes")
    public String clientesGerencia() {
        return "pages/clientes";
    }

    @GetMapping("/gerencia/empleados")
    public String empleadosGerencia() {
        return "pages/empleados";
    }

    @GetMapping("/gerencia/aprobacion-creditos")
    public String aprobacionCreditosGerencia() {
        return "pages/aprobacion-creditos";
    }

    @GetMapping("/configuracion")
    public String configuracionLegacy() {
        return "redirect:/gerencia/clientes";
    }
}

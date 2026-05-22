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

    @GetMapping("/pagos")
    public String pagos() {
        return "pages/pagos";
    }

    @GetMapping("/configuracion")
    public String configuracion() {
        return "pages/configuracion";
    }
}

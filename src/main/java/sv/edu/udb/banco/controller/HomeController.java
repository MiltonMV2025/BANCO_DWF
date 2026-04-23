package sv.edu.udb.banco.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/home")
    @ResponseBody
    public String home(final Authentication authentication) {
        return "Login exitoso. Usuario autenticado: " + authentication.getName();
    }
}

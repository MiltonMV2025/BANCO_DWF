package sv.edu.udb.banco.service.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sv.edu.udb.banco.entity.Usuario;
import sv.edu.udb.banco.repository.UsuarioRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(final UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final String usernameNormalizado = username == null ? "" : username.trim();

        final Usuario usuario = buscarUsuarioActivo(usernameNormalizado)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado o inactivo: " + usernameNormalizado));

        final Set<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getCodigo()))
                .collect(Collectors.toSet());

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPasswordHash())
                .authorities(authorities)
                .build();
    }

    private java.util.Optional<Usuario> buscarUsuarioActivo(final String username) {
        final java.util.Optional<Usuario> exacto = usuarioRepository.findByUsernameIgnoreCaseAndEstado(username, "A");
        if (exacto.isPresent()) {
            return exacto;
        }

        final String soloDigitos = username.replaceAll("[^0-9]", "");
        if (soloDigitos.length() == 9) {
            final String duiConGuion = soloDigitos.substring(0, 8) + "-" + soloDigitos.substring(8);
            return usuarioRepository.findByUsernameIgnoreCaseAndEstado(duiConGuion, "A");
        }

        return java.util.Optional.empty();
    }
}

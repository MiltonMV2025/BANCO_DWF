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
        final Usuario usuario = usuarioRepository.findByUsernameAndEstado(username, "A")
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado o inactivo: " + username));

        final Set<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getCodigo()))
                .collect(Collectors.toSet());

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPasswordHash())
                .authorities(authorities)
                .build();
    }
}

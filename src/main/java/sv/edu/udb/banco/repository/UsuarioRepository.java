package sv.edu.udb.banco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.banco.entity.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsernameAndEstado(String username, String estado);
}

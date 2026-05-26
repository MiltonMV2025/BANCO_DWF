package sv.edu.udb.banco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sv.edu.udb.banco.entity.Cliente;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    interface ClienteGerenciaRow {
        Integer getId();
        String getNombre();
        String getDui();
        BigDecimal getSalario();
        String getEstado();
        Long getTotalCuentas();
        BigDecimal getSaldoTotal();
    }

    Optional<Cliente> findByDui(String dui);

    List<Cliente> findAllByEstado(String estado);

    @Query("""
            select
                c.id as id,
                c.nombre as nombre,
                c.dui as dui,
                c.salario as salario,
                c.estado as estado,
                count(ct.idCuenta) as totalCuentas,
                coalesce(sum(ct.saldo), 0) as saldoTotal
            from Cliente c
            left join c.cuentas ct
            where (:q is null or trim(:q) = '' or lower(c.nombre) like lower(concat('%', :q, '%')) or c.dui like concat('%', :q, '%'))
              and (:estado is null or trim(:estado) = '' or lower(c.estado) = lower(:estado))
            group by c.id, c.nombre, c.dui, c.salario, c.estado
            order by c.nombre asc
            """)
    List<ClienteGerenciaRow> findResumenGerencia(
            @Param("q") String q,
            @Param("estado") String estado
    );
}

package sv.edu.udb.banco.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(
        name = "cliente",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_CLIENTE_DUI", columnNames = "dui")
        },
        indexes = {
                @Index(name = "IX_CLIENTE_ESTADO", columnList = "estado")
        }
)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "dui", nullable = false, length = 10)
    private String dui;

    @Column(name = "salario", nullable = false, precision = 18, scale = 2)
    private BigDecimal salario;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private Set<Cuenta> cuentas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private Set<Prestamo> prestamos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private Set<Movimiento> movimientos = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(final String nombre) {
        this.nombre = nombre;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(final String dui) {
        this.dui = dui;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(final BigDecimal salario) {
        this.salario = salario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(final String estado) {
        this.estado = estado;
    }

    public Set<Cuenta> getCuentas() {
        return cuentas;
    }

    public void setCuentas(final Set<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }

    public Set<Prestamo> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(final Set<Prestamo> prestamos) {
        this.prestamos = prestamos;
    }

    public Set<Movimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(final Set<Movimiento> movimientos) {
        this.movimientos = movimientos;
    }
}

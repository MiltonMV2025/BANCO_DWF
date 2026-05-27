package sv.edu.udb.banco.entity;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "empleado",
        indexes = {
                @Index(name = "IX_EMPLEADO_ESTADO", columnList = "estado"),
                @Index(name = "IX_EMPLEADO_ID_SUCURSAL", columnList = "id_sucursal")
        }
)
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado", nullable = false)
    private Integer idEmpleado;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "rol", nullable = false, length = 50)
    private String rol;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal", nullable = true)
    private Sucursal sucursal;

    @OneToMany(mappedBy = "empleado", fetch = FetchType.LAZY)
    private Set<Prestamo> prestamos = new LinkedHashSet<>();

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(final Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(final String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(final String rol) {
        this.rol = rol;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(final String estado) {
        this.estado = estado;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(final Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public Set<Prestamo> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(final Set<Prestamo> prestamos) {
        this.prestamos = prestamos;
    }
}
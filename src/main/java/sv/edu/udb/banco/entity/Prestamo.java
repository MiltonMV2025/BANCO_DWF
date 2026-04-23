package sv.edu.udb.banco.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(
        name = "prestamo",
        indexes = {
                @Index(name = "IX_PRESTAMO_ID_CLIENTE", columnList = "id_cliente"),
                @Index(name = "IX_PRESTAMO_ID_EMPLEADO", columnList = "id_empleado"),
                @Index(name = "IX_PRESTAMO_ESTADO", columnList = "estado")
        }
)
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prestamo", nullable = false)
    private Integer idPrestamo;

    @Column(name = "monto", nullable = false, precision = 18, scale = 2)
    private BigDecimal monto;

    @Column(name = "interes", nullable = false, precision = 9, scale = 4)
    private BigDecimal interes;

    @Column(name = "plazo_meses", nullable = false)
    private Integer plazoMeses;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    public Integer getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(final Integer idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(final BigDecimal monto) {
        this.monto = monto;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public void setInteres(final BigDecimal interes) {
        this.interes = interes;
    }

    public Integer getPlazoMeses() {
        return plazoMeses;
    }

    public void setPlazoMeses(final Integer plazoMeses) {
        this.plazoMeses = plazoMeses;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(final String estado) {
        this.estado = estado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(final Cliente cliente) {
        this.cliente = cliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(final Empleado empleado) {
        this.empleado = empleado;
    }
}

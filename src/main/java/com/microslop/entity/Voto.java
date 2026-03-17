package com.microslop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "voto",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_usuario_proyecto",
        columnNames = {"usuario_id", "proyecto_id"}  // un usuario, un voto por proyecto
    )
)
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_voto", nullable = false)
    private LocalDateTime fechaVoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    // ── Constructores ────────────────────────────────────────────────────────

    public Voto() {
        this.fechaVoto = LocalDateTime.now();
    }

    public Voto(Usuario usuario, Proyecto proyecto) {
        this.usuario   = usuario;
        this.proyecto  = proyecto;
        this.fechaVoto = LocalDateTime.now();
    }

    // ── Getters y Setters ────────────────────────────────────────────────────

    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }

    public LocalDateTime getFechaVoto()        { return fechaVoto; }
    public void setFechaVoto(LocalDateTime f)  { this.fechaVoto = f; }

    public Usuario getUsuario()                { return usuario; }
    public void setUsuario(Usuario u)          { this.usuario = u; }

    public Proyecto getProyecto()              { return proyecto; }
    public void setProyecto(Proyecto p)        { this.proyecto = p; }

    @Override
    public String toString() {
        return "Voto{" +
                "id=" + id +
                ", usuario=" + usuario +
                ", proyecto=" + proyecto +
                ", fechaVoto=" + fechaVoto +
                '}';
    }
}
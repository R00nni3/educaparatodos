package com.educaparatodos.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(
        name = "inscripciones",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "curso_id"})
)
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_inscripcion", nullable = false)
    private Date fechaInscripcion;

    public Inscripcion() {}

    public Inscripcion(Usuario usuario, Curso curso) {
        this.usuario = usuario;
        this.curso = curso;
    }

    @PrePersist
    public void prePersist() {
        if (this.fechaInscripcion == null) {
            this.fechaInscripcion = new Date();
        }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }

    public Date getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(Date fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
}
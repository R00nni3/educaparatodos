package com.educaparatodos.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "cursos")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, length = 50)
    private String tema;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelDificultad nivel;

    private int popularidad;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Leccion> lecciones;

    public Curso() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }

    public NivelDificultad getNivel() { return nivel; }
    public void setNivel(NivelDificultad nivel) { this.nivel = nivel; }

    public int getPopularidad() { return popularidad; }
    public void setPopularidad(int popularidad) { this.popularidad = popularidad; }

    public List<Leccion> getLecciones() { return lecciones; }
    public void setLecciones(List<Leccion> lecciones) { this.lecciones = lecciones; }
}
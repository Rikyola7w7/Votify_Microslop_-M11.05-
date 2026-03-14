package com.microslop.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios") //Da error pero funciona correctamente(creo)
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private String contraseña;

    public Usuario() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Usuario(String nombre, String email, String contraseña) {
        this.nombre = nombre;
        this.email = email;
        this.contraseña = contraseña;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getContraseña() { return contraseña; }
    public void  setContraseña(String contraseña) { this.contraseña = contraseña; }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + id +
                ", nombre=" + nombre +
                ", email=" + email +
                ", contraseña:" + contraseña +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}







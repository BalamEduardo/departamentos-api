package com.blue.apartamentos.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "habitaciones")
public class HabitacionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habitacion")
    private Long id_habitacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_propiedad", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "habitaciones"})
    private PropiedadModel propiedad;

    @Column(name = "numero", nullable = false, length = 20)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_habitacion", nullable = false, length = 20)
    private TipoHabitacion tipo_habitacion = TipoHabitacion.INDIVIDUAL;

    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;

    @Column(name = "capacidad", nullable = false)
    private int capacidad;

    @Column(name = "num_camas", nullable = false)
    private int num_camas;

    @Column(name = "bano_privado", nullable = false)
    private boolean bano_privado;

    @Column(name = "amueblada", nullable = false)
    private boolean amueblada;

    @Column(name = "metro_cuadrados", nullable = false)
    private double metro_cuadrados;

    @Column(name = "nivel", nullable = false)
    private int nivel;

    @Column(name = "precio_noche", nullable = false)
    private double precio_noche;

    @Column(name = "comodidades", length = 300)
    private String comodidades;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoHabitacion estado = EstadoHabitacion.DISPONIBLE;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fecha_registro;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fecha_actualizacion;

    public HabitacionModel() {
    }

    @PrePersist
    public void onPrePersist() {
        if (this.fecha_registro == null) {
            this.fecha_registro = LocalDateTime.now();
        }

        this.fecha_actualizacion = LocalDateTime.now();
    }

    @PreUpdate
    public void onPreUpdate() {
        this.fecha_actualizacion = LocalDateTime.now();
    }

    public Long getId_habitacion() {
        return id_habitacion;
    }

    public void setId_habitacion(Long id_habitacion) {
        this.id_habitacion = id_habitacion;
    }

    public PropiedadModel getPropiedad() {
        return propiedad;
    }

    public void setPropiedad(PropiedadModel propiedad) {
        this.propiedad = propiedad;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoHabitacion getTipo_habitacion() {
        return tipo_habitacion;
    }

    public void setTipo_habitacion(TipoHabitacion tipo_habitacion) {
        this.tipo_habitacion = tipo_habitacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public int getNum_camas() {
        return num_camas;
    }

    public void setNum_camas(int num_camas) {
        this.num_camas = num_camas;
    }

    public boolean isBano_privado() {
        return bano_privado;
    }

    public void setBano_privado(boolean bano_privado) {
        this.bano_privado = bano_privado;
    }

    public boolean isAmueblada() {
        return amueblada;
    }

    public void setAmueblada(boolean amueblada) {
        this.amueblada = amueblada;
    }

    public double getMetro_cuadrados() {
        return metro_cuadrados;
    }

    public void setMetro_cuadrados(double metro_cuadrados) {
        this.metro_cuadrados = metro_cuadrados;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public double getPrecio_noche() {
        return precio_noche;
    }

    public void setPrecio_noche(double precio_noche) {
        this.precio_noche = precio_noche;
    }

    public String getComodidades() {
        return comodidades;
    }

    public void setComodidades(String comodidades) {
        this.comodidades = comodidades;
    }

    public EstadoHabitacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoHabitacion estado) {
        this.estado = estado;
    }

    public LocalDateTime getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(LocalDateTime fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    public LocalDateTime getFecha_actualizacion() {
        return fecha_actualizacion;
    }

    public void setFecha_actualizacion(LocalDateTime fecha_actualizacion) {
        this.fecha_actualizacion = fecha_actualizacion;
    }

    public enum TipoHabitacion {
        INDIVIDUAL,
        DOBLE,
        SUITE
    }

    public enum EstadoHabitacion {
        DISPONIBLE,
        NO_DISPONIBLE,
        MANTENIMIENTO
    }
}

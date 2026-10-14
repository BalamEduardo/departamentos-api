package com.blue.apartamentos.models;

import java.time.LocalDate;
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
import jakarta.persistence.Table;

@Entity
@Table(name = "reservas")
public class ReservaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_reserva;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_arrendatario", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "password" })
    private ClienteModel arrendatario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_propiedad", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "habitaciones" })
    private PropiedadModel propiedad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_habitacion")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "propiedad" })
    private HabitacionModel habitacion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fecha_inicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fecha_fin;

    @Column(name = "num_personas", nullable = false)
    private int num_personas;

    @Column(name = "monto_total", nullable = false)
    private double monto_total;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fecha_registro;

    public ReservaModel() {
    }

    @PrePersist
    public void prepararRegistro() {
        if (fecha_registro == null) {
            fecha_registro = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoReserva.PENDIENTE;
        }
    }

    public Long getId_reserva() { return id_reserva; }
    public void setId_reserva(Long id_reserva) { this.id_reserva = id_reserva; }
    public ClienteModel getArrendatario() { return arrendatario; }
    public void setArrendatario(ClienteModel arrendatario) { this.arrendatario = arrendatario; }
    public PropiedadModel getPropiedad() { return propiedad; }
    public void setPropiedad(PropiedadModel propiedad) { this.propiedad = propiedad; }
    public HabitacionModel getHabitacion() { return habitacion; }
    public void setHabitacion(HabitacionModel habitacion) { this.habitacion = habitacion; }
    public LocalDate getFecha_inicio() { return fecha_inicio; }
    public void setFecha_inicio(LocalDate fecha_inicio) { this.fecha_inicio = fecha_inicio; }
    public LocalDate getFecha_fin() { return fecha_fin; }
    public void setFecha_fin(LocalDate fecha_fin) { this.fecha_fin = fecha_fin; }
    public int getNum_personas() { return num_personas; }
    public void setNum_personas(int num_personas) { this.num_personas = num_personas; }
    public double getMonto_total() { return monto_total; }
    public void setMonto_total(double monto_total) { this.monto_total = monto_total; }
    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }
    public LocalDateTime getFecha_registro() { return fecha_registro; }
    public void setFecha_registro(LocalDateTime fecha_registro) { this.fecha_registro = fecha_registro; }

    public void confirmar() {
        if (estado == EstadoReserva.CANCELADA) {
            throw new IllegalStateException("Una reserva cancelada no se puede confirmar.");
        }
        estado = EstadoReserva.CONFIRMADA;
    }

    public void cancelar() {
        estado = EstadoReserva.CANCELADA;
    }

    public enum EstadoReserva {
        PENDIENTE,
        CONFIRMADA,
        CANCELADA
    }
}

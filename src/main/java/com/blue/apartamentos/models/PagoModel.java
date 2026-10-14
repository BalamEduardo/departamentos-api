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
@Table(name = "pagos")
public class PagoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_pago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_reserva", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private ReservaModel reserva;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo", length = 30)
    private MetodoPago metodo = MetodoPago.EFECTIVO;

    public enum MetodoPago { EFECTIVO, TRANSFERENCIA, TARJETA }
    public MetodoPago getMetodo() { return metodo; }
    public void setMetodo(MetodoPago metodo) { this.metodo = metodo; }

    @Column(name = "monto", nullable = false)
    private double monto;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fecha_vencimiento;

    @Column(name = "fecha_pago")
    private LocalDate fecha_pago;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fecha_registro;

    public PagoModel() {
    }

    @PrePersist
    public void prepararRegistro() {
        if (fecha_registro == null) {
            fecha_registro = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoPago.PENDIENTE;
        }
    }

    public Long getId_pago() { return id_pago; }
    public void setId_pago(Long id_pago) { this.id_pago = id_pago; }
    public ReservaModel getReserva() { return reserva; }
    public void setReserva(ReservaModel reserva) { this.reserva = reserva; }
    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
    public LocalDate getFecha_vencimiento() { return fecha_vencimiento; }
    public void setFecha_vencimiento(LocalDate fecha_vencimiento) { this.fecha_vencimiento = fecha_vencimiento; }
    public LocalDate getFecha_pago() { return fecha_pago; }
    public void setFecha_pago(LocalDate fecha_pago) { this.fecha_pago = fecha_pago; }
    public EstadoPago getEstado() { return estado; }
    public void setEstado(EstadoPago estado) { this.estado = estado; }
    public LocalDateTime getFecha_registro() { return fecha_registro; }
    public void setFecha_registro(LocalDateTime fecha_registro) { this.fecha_registro = fecha_registro; }

    public boolean estaVencido() {
        return estado == EstadoPago.PENDIENTE && fecha_vencimiento != null
                && fecha_vencimiento.isBefore(LocalDate.now());
    }

    public enum EstadoPago {
        PENDIENTE,
        PAGADO
    }
}

package com.blue.apartamentos.models;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "disponibilidades")
public class DisponibilidadModel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_disponibilidad;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_propiedad")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "habitaciones"})
    private PropiedadModel propiedad;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_habitacion")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "propiedad"})
    private HabitacionModel habitacion;
    @Column(nullable = false) private LocalDateTime fecha_inicio;
    @Column(nullable = false) private LocalDateTime fecha_fin;
    @Column(nullable = false) private Boolean disponible;
    private Double precio_especial;
    public Long getId_disponibilidad() { return id_disponibilidad; }
    public void setId_disponibilidad(Long valor) { id_disponibilidad = valor; }
    public PropiedadModel getPropiedad() { return propiedad; }
    public void setPropiedad(PropiedadModel valor) { propiedad = valor; }
    public HabitacionModel getHabitacion() { return habitacion; }
    public void setHabitacion(HabitacionModel valor) { habitacion = valor; }
    public LocalDateTime getFecha_inicio() { return fecha_inicio; }
    public void setFecha_inicio(LocalDateTime valor) { fecha_inicio = valor; }
    public LocalDateTime getFecha_fin() { return fecha_fin; }
    public void setFecha_fin(LocalDateTime valor) { fecha_fin = valor; }
    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean valor) { disponible = valor; }
    public Double getPrecio_especial() { return precio_especial; }
    public void setPrecio_especial(Double valor) { precio_especial = valor; }
}

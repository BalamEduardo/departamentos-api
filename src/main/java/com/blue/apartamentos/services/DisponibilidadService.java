package com.blue.apartamentos.services;

import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.blue.apartamentos.models.*;
import com.blue.apartamentos.repositories.*;

@Service
public class DisponibilidadService {
    @Autowired
    private IDisponibilidadRepository repository;
    @Autowired
    private IPropiedadRepository propiedades;
    @Autowired
    private IHabitacionRepository habitaciones;
    @Autowired
    private IReservaRepository reservas;
    public List<DisponibilidadModel> listar(Long propiedadId) {
        return repository.findAll().stream()
                .filter(disponibilidad -> propiedadId == null || disponibilidad.getPropiedad() != null
                        && disponibilidad.getPropiedad().getId_propiedad().equals(propiedadId))
                .toList();
    }

    @Transactional
    public DisponibilidadModel guardar(Long id, DisponibilidadModel datos) {
        DisponibilidadModel existente = id == null ? null : repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (datos.getPropiedad() == null || datos.getPropiedad().getId_propiedad() == null)
            throw new IllegalArgumentException("Selecciona una propiedad.");
        if (existente != null && existente.getPropiedad() != null
                && !existente.getPropiedad().getId_propiedad().equals(datos.getPropiedad().getId_propiedad()))
            throw new IllegalArgumentException("Conserva la propiedad del intervalo; elimina y crea otro para cambiarla.");
        PropiedadModel propiedad = propiedades.findById(datos.getPropiedad().getId_propiedad())
                .orElseThrow(() -> new IllegalArgumentException("No existe la propiedad."));
        HabitacionModel habitacion = null;
        if (datos.getHabitacion() != null && datos.getHabitacion().getId_habitacion() != null) {
            habitacion = habitaciones.findById(datos.getHabitacion().getId_habitacion())
                    .orElseThrow(() -> new IllegalArgumentException("No existe la habitación."));
            if (!habitacion.getPropiedad().getId_propiedad().equals(propiedad.getId_propiedad()))
                throw new IllegalArgumentException("La habitación no pertenece a la propiedad.");
        }
        if (datos.getFecha_inicio() == null || datos.getFecha_fin() == null || datos.getDisponible() == null
                || !datos.getFecha_inicio().isBefore(datos.getFecha_fin())
                || !datos.getFecha_inicio().toLocalTime().equals(java.time.LocalTime.MIDNIGHT)
                || !datos.getFecha_fin().toLocalTime().equals(java.time.LocalTime.MIDNIGHT))
            throw new IllegalArgumentException("Indica fechas válidas a las 00:00; la fecha final queda excluida.");
        if (datos.getPrecio_especial() != null && (!Double.isFinite(datos.getPrecio_especial())
                || datos.getPrecio_especial() <= 0
                || BigDecimal.valueOf(datos.getPrecio_especial()).stripTrailingZeros().scale() > 2))
            throw new IllegalArgumentException("El precio especial debe ser positivo con máximo dos decimales.");
        Long roomId = habitacion == null ? null : habitacion.getId_habitacion();
        for (DisponibilidadModel otro : listar(propiedad.getId_propiedad())) {
            if (!Objects.equals(otro.getId_disponibilidad(), id)
                    && Objects.equals(roomId, otro.getHabitacion() == null ? null : otro.getHabitacion().getId_habitacion())
                    && datos.getFecha_inicio().isBefore(otro.getFecha_fin()) && datos.getFecha_fin().isAfter(otro.getFecha_inicio()))
                throw new IllegalStateException("Ya existe un intervalo para este espacio en esas fechas.");
        }
        if (!datos.getDisponible()) {
            for (ReservaModel reserva : reservas.findByPropiedadId(propiedad.getId_propiedad())) {
                if (reserva.getEstado() != ReservaModel.EstadoReserva.CANCELADA
                        && (roomId == null || reserva.getHabitacion() == null || roomId.equals(reserva.getHabitacion().getId_habitacion()))
                        && datos.getFecha_inicio().toLocalDate().isBefore(reserva.getFecha_fin())
                        && datos.getFecha_fin().toLocalDate().isAfter(reserva.getFecha_inicio()))
                    throw new IllegalStateException("El bloqueo se cruza con una reserva activa; cancélala o cambia las fechas primero.");
            }
        }
        datos.setId_disponibilidad(id);
        datos.setPropiedad(propiedad);
        datos.setHabitacion(habitacion);
        return repository.save(datos);
    }
    @Transactional
    public void eliminar(Long id) {
        DisponibilidadModel existente = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        repository.delete(existente);
    }
    public boolean bloqueado(Long propiedadId, Long roomId, LocalDate inicio, LocalDate fin) {
        return listar(propiedadId).stream()
                .anyMatch(disponibilidad -> Boolean.FALSE.equals(disponibilidad.getDisponible())
                        && (disponibilidad.getHabitacion() == null || roomId == null
                            || roomId.equals(disponibilidad.getHabitacion().getId_habitacion()))
                        && inicio.isBefore(disponibilidad.getFecha_fin().toLocalDate())
                        && fin.isAfter(disponibilidad.getFecha_inicio().toLocalDate()));
    }

    public double total(Long propiedadId, Long roomId, LocalDate inicio, LocalDate fin, double base) {
        List<DisponibilidadModel> lista = listar(propiedadId);
        BigDecimal total = BigDecimal.ZERO;
        for (LocalDate noche = inicio; noche.isBefore(fin); noche = noche.plusDays(1)) {
            double precio = base;
            for (DisponibilidadModel d : lista) {
                if (Objects.equals(roomId, d.getHabitacion() == null ? null : d.getHabitacion().getId_habitacion())
                        && Boolean.TRUE.equals(d.getDisponible()) && d.getPrecio_especial() != null
                        && !noche.isBefore(d.getFecha_inicio().toLocalDate()) && noche.isBefore(d.getFecha_fin().toLocalDate())) {
                    precio = d.getPrecio_especial(); break;
                }
            }
            total = total.add(BigDecimal.valueOf(precio));
        }
        return total.setScale(2, java.math.RoundingMode.HALF_UP).doubleValue();
    }
}

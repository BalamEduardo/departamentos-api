package com.blue.apartamentos.services;

import com.blue.apartamentos.models.PagoModel;
import com.blue.apartamentos.repositories.IPagoRepository;
import com.blue.apartamentos.repositories.IPropiedadRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blue.apartamentos.models.ClienteModel;
import com.blue.apartamentos.models.HabitacionModel;
import com.blue.apartamentos.models.PropiedadModel;
import com.blue.apartamentos.models.ReservaModel;
import com.blue.apartamentos.repositories.IReservaRepository;

@Service
public class ReservaService {

    @Autowired
    private IReservaRepository reservaRepository;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PropiedadService propiedadService;

    @Autowired
    private HabitacionService habitacionService;

    public List<ReservaModel> getAll() {
        return reservaRepository.findAll();
    }

    public Optional<ReservaModel> getById(Long id) {
        return reservaRepository.findById(id);
    }

    public List<ReservaModel> getAllByProperty(Long propertyId) {
        return reservaRepository.findByPropiedadId(propertyId);
    }

    @Autowired
    private IPropiedadRepository propiedadRepository;

    @Transactional
    public ReservaModel crear(ReservaModel datos) {
        return guardar(datos, null);
    }

    @Autowired
    private DisponibilidadService disponibilidadService;
    @Autowired
    private IPagoRepository pagoRepository;

    @Transactional
    public ReservaModel actualizar(Long id, ReservaModel datos) {
        ReservaModel existente = reservaRepository.findById(id).orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));
        if (existente.getEstado() == ReservaModel.EstadoReserva.CANCELADA)
            throw new IllegalStateException("Una reserva cancelada no se puede editar.");
        if (datos.getPropiedad() != null && !existente.getPropiedad().getId_propiedad().equals(datos.getPropiedad().getId_propiedad()))
            throw new IllegalArgumentException("Conserva la propiedad de la reserva; cancela y crea otra para cambiarla.");
        datos.setPropiedad(existente.getPropiedad());
        if (datos.getArrendatario() == null) datos.setArrendatario(existente.getArrendatario());
        if (datos.getEstado() == ReservaModel.EstadoReserva.CANCELADA)
            throw new IllegalArgumentException("Usa la acción Cancelar para cancelar la reserva.");
        return guardar(datos, existente);
    }

    private ReservaModel guardar(ReservaModel datos, ReservaModel anterior) {
        if (datos.getArrendatario() == null || datos.getArrendatario().getId() == null) {
            throw new IllegalArgumentException("Selecciona un arrendatario.");
        }
        if (datos.getPropiedad() == null || datos.getPropiedad().getId_propiedad() == null) {
            throw new IllegalArgumentException("Selecciona una propiedad.");
        }

        ClienteModel arrendatario = clienteService.getClienteById(datos.getArrendatario().getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe el arrendatario seleccionado."));
        if (!arrendatario.esArrendatarioActivo()) {
            throw new IllegalArgumentException("El arrendatario debe estar activo y tener tipo CLIENTE.");
        }

        PropiedadModel propiedad = propiedadRepository.findById(datos.getPropiedad().getId_propiedad())
                .orElseThrow(() -> new IllegalArgumentException("No existe la propiedad seleccionada."));

        HabitacionModel habitacion = null;
        if (datos.getHabitacion() != null && datos.getHabitacion().getId_habitacion() != null) {
            habitacion = habitacionService.getHabitacionById(datos.getHabitacion().getId_habitacion())
                    .orElseThrow(() -> new IllegalArgumentException("No existe la habitación seleccionada."));
            if (!habitacion.getPropiedad().getId_propiedad().equals(propiedad.getId_propiedad())) {
                throw new IllegalArgumentException("La habitación no pertenece a la propiedad seleccionada.");
            }
        }

        validarFechas(datos.getFecha_inicio(), datos.getFecha_fin());
        int capacidad = habitacion == null ? propiedad.getCapacidad() : habitacion.getCapacidad();
        double precioNoche = habitacion == null ? propiedad.getPrecio_noche() : habitacion.getPrecio_noche();
        if (datos.getNum_personas() < 1 || datos.getNum_personas() > capacidad) {
            throw new IllegalArgumentException("El número de personas excede la capacidad disponible.");
        }

        if (!estaDisponible(propiedad.getId_propiedad(), habitacion == null ? null : habitacion.getId_habitacion(),
                datos.getFecha_inicio(), datos.getFecha_fin(), anterior == null ? null : anterior.getId_reserva())) {
            throw new IllegalStateException("Ya existe una reserva que ocupa esas fechas.");
        }

        double total = disponibilidadService.total(propiedad.getId_propiedad(), habitacion == null ? null : habitacion.getId_habitacion(),
                datos.getFecha_inicio(), datos.getFecha_fin(), precioNoche);
        if (anterior != null) {
            double programado = pagoRepository.findByReservaId(anterior.getId_reserva()).stream()
                    .mapToDouble(PagoModel::getMonto).sum();
            if (total + 0.001 < programado) throw new IllegalStateException("El nuevo total no puede ser menor que los abonos registrados.");
        }
        datos.setId_reserva(anterior == null ? null : anterior.getId_reserva());
        datos.setFecha_registro(anterior == null ? null : anterior.getFecha_registro());
        datos.setArrendatario(arrendatario);
        datos.setPropiedad(propiedad);
        datos.setHabitacion(habitacion);
        datos.setMonto_total(total);
        datos.setEstado(anterior == null ? ReservaModel.EstadoReserva.PENDIENTE : anterior.getEstado());
        return reservaRepository.save(datos);
    }

    public boolean estaDisponible(Long propiedadId, Long habitacionId, LocalDate inicio, LocalDate fin) {
        return estaDisponible(propiedadId, habitacionId, inicio, fin, null);
    }

    private boolean estaDisponible(Long propiedadId, Long habitacionId, LocalDate inicio, LocalDate fin, Long excluirId) {
        validarFechas(inicio, fin);
        Optional<PropiedadModel> propiedadEncontrada = propiedadService.getPropiedadById(propiedadId);
        if (propiedadEncontrada.isEmpty()) {
            throw new IllegalArgumentException("No existe la propiedad seleccionada.");
        }

        if (propiedadEncontrada.get().getEstado() != PropiedadModel.EstadoHab.DISPONIBLE) return false;

        if (habitacionId != null) {
            HabitacionModel habitacion = habitacionService.getHabitacionById(habitacionId)
                    .orElseThrow(() -> new IllegalArgumentException("No existe la habitación seleccionada."));
            if (habitacion.getEstado() != HabitacionModel.EstadoHabitacion.DISPONIBLE) return false;
            if (!habitacion.getPropiedad().getId_propiedad().equals(propiedadId)) {
                throw new IllegalArgumentException("La habitación no pertenece a la propiedad seleccionada.");
            }
        }

        if (disponibilidadService.bloqueado(propiedadId, habitacionId, inicio, fin)) return false;
        for (ReservaModel existente : getAllByProperty(propiedadId)) {
            if (java.util.Objects.equals(excluirId, existente.getId_reserva())) continue;
            if (existente.getEstado() == ReservaModel.EstadoReserva.CANCELADA) {
                continue;
            }
            boolean fechasSeCruzan = inicio.isBefore(existente.getFecha_fin())
                    && fin.isAfter(existente.getFecha_inicio());
            if (!fechasSeCruzan) {
                continue;
            }
            if (habitacionId == null || existente.getHabitacion() == null
                    || habitacionId.equals(existente.getHabitacion().getId_habitacion())) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public ReservaModel confirmar(Long id) {
        ReservaModel reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la reserva."));
        reserva.confirmar();
        return reservaRepository.save(reserva);
    }

    @Transactional
    public ReservaModel cancelar(Long id) {
        ReservaModel reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la reserva."));
        reserva.cancelar();
        return reservaRepository.save(reserva);
    }

    private void validarFechas(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null || !inicio.isBefore(fin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de salida.");
        }
    }
}

package com.blue.apartamentos.services;

import com.blue.apartamentos.repositories.IPropiedadRepository;
import java.util.List;
import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blue.apartamentos.models.HabitacionModel;
import com.blue.apartamentos.models.PropiedadModel;
import com.blue.apartamentos.repositories.IHabitacionRepository;

@Service
public class HabitacionService {

    @Autowired
    private IHabitacionRepository habitacionRepository;

    public List<HabitacionModel> getAllHabitaciones() {
        return habitacionRepository.findAll();
    }

    public Optional<HabitacionModel> getHabitacionById(Long id) {
        return habitacionRepository.findById(id);
    }

    public List<HabitacionModel> getHabitacionesByPropiedad(PropiedadModel propiedad) {
        return habitacionRepository.findByPropiedad(propiedad);
    }

    @Autowired
    private IPropiedadRepository propiedades;

    @Transactional
    public HabitacionModel saveHabitacion(HabitacionModel habitacion) {
        propiedades.findById(habitacion.getPropiedad().getId_propiedad())
                .orElseThrow(() -> new IllegalArgumentException("No existe la propiedad."));
        if (habitacion.getNumero() == null || habitacion.getNumero().isBlank()
                || habitacion.getDescripcion() == null || habitacion.getDescripcion().isBlank()
                || habitacion.getTipo_habitacion() == null || habitacion.getEstado() == null
                || habitacion.getCapacidad() < 1 || habitacion.getNum_camas() < 1
                || !Double.isFinite(habitacion.getMetro_cuadrados()) || habitacion.getMetro_cuadrados() <= 0
                || !Double.isFinite(habitacion.getPrecio_noche()) || habitacion.getPrecio_noche() <= 0
                || BigDecimal.valueOf(habitacion.getPrecio_noche()).stripTrailingZeros().scale() > 2
                || habitacion.getNivel() < 0) {
            throw new IllegalArgumentException("Completa la habitación con capacidad, camas, superficie y precio mayores a cero.");
        }
        boolean duplicada = getHabitacionesByPropiedad(habitacion.getPropiedad()).stream()
                .anyMatch(otra -> otra.getNumero().equalsIgnoreCase(habitacion.getNumero().trim())
                        && !java.util.Objects.equals(otra.getId_habitacion(), habitacion.getId_habitacion()));
        if (duplicada) {
            throw new IllegalStateException("Ya existe una habitacion con ese numero o nombre en esta propiedad.");
        }
        habitacion.setNumero(habitacion.getNumero().trim());
        return habitacionRepository.save(habitacion);
    }

    public void deleteHabitacion(Long id) {
        habitacionRepository.deleteById(id);
    }
}

package com.blue.apartamentos.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blue.apartamentos.models.HabitacionModel;
import com.blue.apartamentos.models.PropiedadModel;
import com.blue.apartamentos.services.HabitacionService;
import com.blue.apartamentos.services.PropiedadService;

@RestController
@RequestMapping("/api")
public class HabitacionController {

    @Autowired
    private HabitacionService habitacionService;

    @Autowired
    private PropiedadService propiedadService;

    @GetMapping("/propiedades/{propiedadId}/habitaciones")
    public ResponseEntity<List<HabitacionModel>> getHabitacionesByPropiedad(
            @PathVariable Long propiedadId) {

        Optional<PropiedadModel> propiedad = propiedadService.getPropiedadById(propiedadId);

        if (propiedad.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(habitacionService.getHabitacionesByPropiedad(propiedad.get()));
    }

    @PostMapping("/propiedades/{propiedadId}/habitaciones")
    public ResponseEntity<HabitacionModel> createHabitacion(
            @PathVariable Long propiedadId,
            @RequestBody HabitacionModel habitacion) {

        Optional<PropiedadModel> propiedad = propiedadService.getPropiedadById(propiedadId);

        if (propiedad.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        habitacion.setId_habitacion(null);
        habitacion.setPropiedad(propiedad.get());
        return ResponseEntity.ok(habitacionService.saveHabitacion(habitacion));
    }

    @GetMapping("/habitaciones/{id}")
    public ResponseEntity<HabitacionModel> getHabitacionById(@PathVariable Long id) {
        Optional<HabitacionModel> habitacion = habitacionService.getHabitacionById(id);

        return habitacion.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/habitaciones/{id}")
    public ResponseEntity<HabitacionModel> updateHabitacion(
            @PathVariable Long id,
            @RequestBody HabitacionModel habitacionDetails) {

        Optional<HabitacionModel> habitacionOptional = habitacionService.getHabitacionById(id);

        if (habitacionOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HabitacionModel habitacion = habitacionOptional.get();
        habitacion.setNumero(habitacionDetails.getNumero());
        habitacion.setTipo_habitacion(habitacionDetails.getTipo_habitacion());
        habitacion.setDescripcion(habitacionDetails.getDescripcion());
        habitacion.setCapacidad(habitacionDetails.getCapacidad());
        habitacion.setNum_camas(habitacionDetails.getNum_camas());
        habitacion.setBano_privado(habitacionDetails.isBano_privado());
        habitacion.setAmueblada(habitacionDetails.isAmueblada());
        habitacion.setMetro_cuadrados(habitacionDetails.getMetro_cuadrados());
        habitacion.setNivel(habitacionDetails.getNivel());
        habitacion.setPrecio_noche(habitacionDetails.getPrecio_noche());
        habitacion.setComodidades(habitacionDetails.getComodidades());
        habitacion.setEstado(habitacionDetails.getEstado());

        return ResponseEntity.ok(habitacionService.saveHabitacion(habitacion));
    }

    @DeleteMapping("/habitaciones/{id}")
    public ResponseEntity<Void> deleteHabitacion(@PathVariable Long id) {
        Optional<HabitacionModel> habitacion = habitacionService.getHabitacionById(id);

        if (habitacion.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        habitacionService.deleteHabitacion(id);
        return ResponseEntity.noContent().build();
    }
}

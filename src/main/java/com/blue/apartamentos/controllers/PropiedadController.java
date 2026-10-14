package com.blue.apartamentos.controllers;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blue.apartamentos.models.PropiedadModel;
import com.blue.apartamentos.services.PropiedadService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/propiedades")
public class PropiedadController {
    
    @Autowired
    private PropiedadService propiedadService;

    // Aquí irán los métodos para manejar las solicitudes HTTP relacionadas con las propiedades
    // Obtener todas las propiedades, obtener por ID, crear, actualizar, eliminar, etc.
    
    //Recuperar todas la propiedades
    @GetMapping
    public List<PropiedadModel> getAllPropiedades(
            @org.springframework.web.bind.annotation.RequestParam(required = false) PropiedadModel.TipoPropiedad tipo,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Long propietarioId) {
        return propiedadService.getAllPropiedades().stream()
                .filter(propiedad -> tipo == null || propiedad.getTipo() == tipo)
                .filter(propiedad -> propietarioId == null || propiedad.getPropietario().getId().equals(propietarioId))
                .toList();
    }

    //Propiedad por ID
    //... Similar a ClienteController
    @GetMapping("/{id}")
    public ResponseEntity<PropiedadModel> getPropiedadesById(@PathVariable Long id) {
        Optional<PropiedadModel> propiedad = propiedadService.getPropiedadById(id);
        return propiedad.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear nueva propiedad
    @PostMapping
    public PropiedadModel createPropiedad(@RequestBody PropiedadModel propiedad) {
        propiedad.setId_propiedad(null);
        propiedad.setHabitaciones(new ArrayList<>());
        return propiedadService.savePropiedad(propiedad);
    }

    // Actualizar propiedad existente
    @PutMapping("/{id}")
    public ResponseEntity<PropiedadModel> updatePropiedad(@PathVariable Long id, @RequestBody PropiedadModel propiedadDetails) {
        Optional<PropiedadModel> propiedadOptional = propiedadService.getPropiedadById(id);
        if (propiedadOptional.isPresent()) {
            PropiedadModel propiedadToUpdate = propiedadOptional.get();
            propiedadToUpdate.setDireccion(propiedadDetails.getDireccion());
            propiedadToUpdate.setTipo(propiedadDetails.getTipo());
            propiedadToUpdate.setPrecio_noche(propiedadDetails.getPrecio_noche());
            propiedadToUpdate.setTitulo(propiedadDetails.getTitulo());
            propiedadToUpdate.setDescripcion(propiedadDetails.getDescripcion());
            propiedadToUpdate.setCiudad(propiedadDetails.getCiudad());
            propiedadToUpdate.setEstidadFederativa(propiedadDetails.getEstidadFederativa());
            propiedadToUpdate.setCodigo_postal(propiedadDetails.getCodigo_postal());
            propiedadToUpdate.setPais(propiedadDetails.getPais());
            propiedadToUpdate.setLatitud(propiedadDetails.getLatitud());
            propiedadToUpdate.setLongitud(propiedadDetails.getLongitud());
            propiedadToUpdate.setCapacidad(propiedadDetails.getCapacidad());
            propiedadToUpdate.setAmueblada(propiedadDetails.isAmueblada());
            propiedadToUpdate.setNum_estacionamientos(propiedadDetails.getNum_estacionamientos());
            propiedadToUpdate.setNum_habitaciones(propiedadDetails.getNum_habitaciones());
            propiedadToUpdate.setNum_banos(propiedadDetails.getNum_banos());
            propiedadToUpdate.setMetro_cuadrados(propiedadDetails.getMetro_cuadrados());
            propiedadToUpdate.setComodidades(propiedadDetails.getComodidades());
            propiedadToUpdate.setReglas(propiedadDetails.getReglas());
            propiedadToUpdate.setEstado(propiedadDetails.getEstado());

            PropiedadModel updatedPropiedad = propiedadService.savePropiedad(propiedadToUpdate);
            return ResponseEntity.ok(updatedPropiedad);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar una propiedad
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePropiedad(@PathVariable Long id) {
        Optional<PropiedadModel> propiedad = propiedadService.getPropiedadById(id);
        if (propiedad.isPresent()) {
            propiedadService.deletePropiedad(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ACTIVIDAD: Crea un metodo para Recuperar propiedades por tipo

    // ACTIVIDAD: Crea un metodo para Recuoperar todas propiedades de un Propietario

    
}

package com.blue.apartamentos.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blue.apartamentos.models.ReservaModel;
import com.blue.apartamentos.services.ReservaService;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    public List<ReservaModel> getAll() {
        return reservaService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaModel> getById(@PathVariable Long id) {
        Optional<ReservaModel> reserva = reservaService.getById(id);
        return reserva.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Object actualizar(@PathVariable Long id, @RequestBody ReservaModel datos) {
        return reservaService.actualizar(id, datos);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ReservaModel datos) {
        try {
            return ResponseEntity.ok(reservaService.crear(datos));
        } catch (IllegalStateException error) {
            return ResponseEntity.status(409).body(Map.of("message", error.getMessage()));
        } catch (IllegalArgumentException error) {
            return ResponseEntity.badRequest().body(Map.of("message", error.getMessage()));
        }
    }

    @GetMapping("/disponibilidad")
    public ResponseEntity<?> disponibilidad(
            @RequestParam Long propiedadId,
            @RequestParam(required = false) Long habitacionId,
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {
        try {
            return ResponseEntity.ok(Map.of("disponible",
                    reservaService.estaDisponible(propiedadId, habitacionId, fechaInicio, fechaFin)));
        } catch (IllegalArgumentException error) {
            return ResponseEntity.badRequest().body(Map.of("message", error.getMessage()));
        }
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reservaService.confirmar(id));
        } catch (IllegalStateException error) {
            return ResponseEntity.badRequest().body(Map.of("message", error.getMessage()));
        } catch (IllegalArgumentException error) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reservaService.cancelar(id));
        } catch (IllegalArgumentException error) {
            return ResponseEntity.notFound().build();
        }
    }
}

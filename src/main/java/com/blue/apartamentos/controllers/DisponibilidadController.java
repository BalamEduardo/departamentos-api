package com.blue.apartamentos.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.blue.apartamentos.models.DisponibilidadModel;
import com.blue.apartamentos.services.DisponibilidadService;
@RestController
@RequestMapping("/api/disponibilidades")
public class DisponibilidadController {
    @Autowired
    private DisponibilidadService service;
    @GetMapping public Object listar(@RequestParam(required = false) Long propiedadId) { return service.listar(propiedadId); }
    @PostMapping public Object crear(@RequestBody DisponibilidadModel datos) { return service.guardar(null, datos); }
    @PutMapping("/{id}") public Object actualizar(@PathVariable Long id, @RequestBody DisponibilidadModel datos) { return service.guardar(id, datos); }
    @DeleteMapping("/{id}") public ResponseEntity<?> eliminar(@PathVariable Long id) { service.eliminar(id); return ResponseEntity.noContent().build(); }
}

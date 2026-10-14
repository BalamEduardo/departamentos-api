package com.blue.apartamentos.controllers;

import com.blue.apartamentos.services.ReservaService;
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
import org.springframework.web.bind.annotation.RestController;

import com.blue.apartamentos.models.PagoModel;
import com.blue.apartamentos.services.PagoService;

@RestController
@RequestMapping("/api")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private ReservaService reservaService;

    @GetMapping("/reservas/{reservaId}/saldo")
    public ResponseEntity<?> saldo(@PathVariable Long reservaId) {
        if (reservaService.getById(reservaId).isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("saldo", pagoService.getSaldo(reservaId)));
    }

    @GetMapping("/pagos")
    public List<PagoModel> getAll() {
        return pagoService.getAll();
    }

    @GetMapping("/pagos/{id}")
    public ResponseEntity<PagoModel> getById(@PathVariable Long id) {
        Optional<PagoModel> pago = pagoService.getById(id);
        return pago.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/reservas/{reservaId}/pagos")
    public ResponseEntity<?> getByReserva(@PathVariable Long reservaId) {
        if (reservaService.getById(reservaId).isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(pagoService.getByReserva(reservaId));
    }

    @PostMapping("/reservas/{reservaId}/pagos")
    public ResponseEntity<?> crear(@PathVariable Long reservaId, @RequestBody PagoModel datos) {
        try {
            return ResponseEntity.ok(pagoService.crear(reservaId, datos));
        } catch (IllegalArgumentException error) {
            return ResponseEntity.badRequest().body(Map.of("message", error.getMessage()));
        }
    }

    @PutMapping("/pagos/{id}/marcar-pagado")
    public ResponseEntity<?> marcarPagado(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pagoService.marcarPagado(id));
        } catch (IllegalArgumentException error) {
            return ResponseEntity.notFound().build();
        }
    }
}

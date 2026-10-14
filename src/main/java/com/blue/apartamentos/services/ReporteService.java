package com.blue.apartamentos.services;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.blue.apartamentos.models.PagoModel;
import com.blue.apartamentos.models.ReservaModel;

@Service
public class ReporteService {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private PagoService pagoService;

    private boolean coincide(ReservaModel reserva, Long propiedadId, Long clienteId) {
        return (propiedadId == null || reserva.getPropiedad().getId_propiedad().equals(propiedadId))
                && (clienteId == null || reserva.getArrendatario().getId().equals(clienteId));
    }

    public List<ReservaModel> ocupacion(LocalDate inicio, LocalDate fin, Long propiedadId, Long clienteId) {
        return reservaService.getAll().stream()
                .filter(reserva -> reserva.getEstado() == ReservaModel.EstadoReserva.CONFIRMADA)
                .filter(reserva -> coincide(reserva, propiedadId, clienteId))
                .filter(reserva -> reserva.getFecha_inicio().isBefore(fin) && reserva.getFecha_fin().isAfter(inicio))
                .toList();
    }

    public double ingresos(LocalDate inicio, LocalDate fin, Long propiedadId, Long clienteId) {
        return pagoService.getAll().stream()
                .filter(pago -> pago.getEstado() == PagoModel.EstadoPago.PAGADO)
                .filter(pago -> coincide(pago.getReserva(), propiedadId, clienteId))
                .filter(pago -> pago.getFecha_pago() != null
                        && !pago.getFecha_pago().isBefore(inicio) && pago.getFecha_pago().isBefore(fin))
                .map(pago -> BigDecimal.valueOf(pago.getMonto()))
                .reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
    }

    public List<PagoModel> morosidad(LocalDate inicio, LocalDate fin, Long propiedadId, Long clienteId) {
        return pagoService.getVencidos().stream()
                .filter(pago -> coincide(pago.getReserva(), propiedadId, clienteId))
                .filter(pago -> inicio == null || !pago.getFecha_vencimiento().isBefore(inicio))
                .filter(pago -> fin == null || pago.getFecha_vencimiento().isBefore(fin))
                .toList();
    }
}

package com.blue.apartamentos.services;

import com.blue.apartamentos.repositories.IReservaRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blue.apartamentos.models.PagoModel;
import com.blue.apartamentos.models.ReservaModel;
import com.blue.apartamentos.repositories.IPagoRepository;

@Service
public class PagoService {

    @Autowired
    private IPagoRepository pagoRepository;

    @Autowired
    private ReservaService reservaService;

    public List<PagoModel> getAll() {
        return pagoRepository.findAll();
    }

    public Optional<PagoModel> getById(Long id) {
        return pagoRepository.findById(id);
    }

    public List<PagoModel> getByReserva(Long reservaId) {
        return pagoRepository.findByReservaId(reservaId);
    }

    @Autowired
    private IReservaRepository reservaRepository;

    @Transactional
    public PagoModel crear(Long reservaId, PagoModel datos) {
        ReservaModel reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("No existe la reserva."));
        if (reserva.getEstado() == ReservaModel.EstadoReserva.CANCELADA) {
            throw new IllegalStateException("No se pueden registrar pagos de una reserva cancelada.");
        }
        if (!Double.isFinite(datos.getMonto()) || datos.getMonto() <= 0 || datos.getFecha_vencimiento() == null
                || BigDecimal.valueOf(datos.getMonto()).stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero y debes indicar el vencimiento.");
        }

        double programado = getByReserva(reservaId).stream().mapToDouble(PagoModel::getMonto).sum();
        double disponibleParaAbonos = reserva.getMonto_total() - programado;
        if (datos.getMonto() > disponibleParaAbonos + 0.001) {
            throw new IllegalArgumentException("El monto del abono supera el saldo disponible de la reserva.");
        }

        if (datos.getMetodo() == null) throw new IllegalArgumentException("Selecciona un método de pago.");
        datos.setId_pago(null);
        datos.setFecha_registro(null);
        datos.setReserva(reserva);
        datos.setEstado(PagoModel.EstadoPago.PENDIENTE);
        datos.setFecha_pago(null);
        return pagoRepository.save(datos);
    }

    @Transactional
    public PagoModel marcarPagado(Long id) {
        PagoModel pago = getById(id).orElseThrow(() -> new IllegalArgumentException("No existe el pago."));
        ReservaModel reserva = reservaRepository.findById(pago.getReserva().getId_reserva()).orElseThrow();
        if (reserva.getEstado() == ReservaModel.EstadoReserva.CANCELADA) {
            throw new IllegalStateException("No se puede cobrar una reserva cancelada.");
        }
        if (pago.getEstado() != PagoModel.EstadoPago.PAGADO) {
            pago.setEstado(PagoModel.EstadoPago.PAGADO);
            pago.setFecha_pago(LocalDate.now());
            pagoRepository.save(pago);
        }
        return pago;
    }

    public List<PagoModel> getVencidos() {
        LocalDate hoy = LocalDate.now();
        return getAll().stream()
                .filter(pago -> pago.getEstado() == PagoModel.EstadoPago.PENDIENTE)
                .filter(pago -> pago.getReserva().getEstado() != ReservaModel.EstadoReserva.CANCELADA)
                .filter(pago -> pago.getFecha_vencimiento() != null && pago.getFecha_vencimiento().isBefore(hoy))
                .toList();
    }

    public double getIngresos(YearMonth mes) {
        LocalDate inicio = mes.atDay(1);
        LocalDate fin = mes.plusMonths(1).atDay(1);
        return getAll().stream()
                .filter(pago -> pago.getEstado() == PagoModel.EstadoPago.PAGADO)
                .filter(pago -> pago.getFecha_pago() != null
                        && !pago.getFecha_pago().isBefore(inicio) && pago.getFecha_pago().isBefore(fin))
                .map(pago -> BigDecimal.valueOf(pago.getMonto()))
                .reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
    }

    public double getSaldo(Long reservaId) {
        ReservaModel reserva = reservaService.getById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("No existe la reserva."));
        BigDecimal pagado = getByReserva(reservaId).stream()
                .filter(pago -> pago.getEstado() == PagoModel.EstadoPago.PAGADO)
                .map(pago -> BigDecimal.valueOf(pago.getMonto()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return BigDecimal.valueOf(reserva.getMonto_total()).subtract(pagado)
                .setScale(2, java.math.RoundingMode.HALF_UP).doubleValue();
    }
}

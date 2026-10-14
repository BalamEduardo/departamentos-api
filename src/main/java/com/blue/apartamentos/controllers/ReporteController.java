package com.blue.apartamentos.controllers;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.blue.apartamentos.services.ReporteService;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {
    @Autowired
    private ReporteService service;
    private LocalDate[] periodo(String mes, LocalDate inicio, LocalDate fin) {
        if (inicio != null || fin != null) {
            if (inicio == null || fin == null || !inicio.isBefore(fin))
                throw new IllegalArgumentException("Indica ambas fechas; la fecha final debe ser posterior y queda excluida.");
            return new LocalDate[]{inicio, fin};
        }
        try {
            YearMonth month = mes == null ? YearMonth.now() : YearMonth.parse(mes);
            return new LocalDate[]{month.atDay(1), month.plusMonths(1).atDay(1)};
        } catch (DateTimeParseException error) {
            throw new IllegalArgumentException("Usa el formato de mes AAAA-MM.");
        }
    }
    @GetMapping("/ocupacion")
    public Object ocupacion(@RequestParam(required = false) String mes,
            @RequestParam(required = false) LocalDate fechaInicio, @RequestParam(required = false) LocalDate fechaFin,
            @RequestParam(required = false) Long propiedadId, @RequestParam(required = false) Long clienteId) {
        LocalDate[] fechas = periodo(mes, fechaInicio, fechaFin);
        return service.ocupacion(fechas[0], fechas[1], propiedadId, clienteId);
    }
    @GetMapping("/ingresos")
    public Object ingresos(@RequestParam(required = false) String mes,
            @RequestParam(required = false) LocalDate fechaInicio, @RequestParam(required = false) LocalDate fechaFin,
            @RequestParam(required = false) Long propiedadId, @RequestParam(required = false) Long clienteId) {
        LocalDate[] fechas = periodo(mes, fechaInicio, fechaFin);
        return Map.of("fechaInicio", fechas[0], "fechaFin", fechas[1], "total", service.ingresos(fechas[0], fechas[1], propiedadId, clienteId));
    }
    @GetMapping("/morosidad")
    public Object morosidad(@RequestParam(required = false) String mes,
            @RequestParam(required = false) LocalDate fechaInicio, @RequestParam(required = false) LocalDate fechaFin,
            @RequestParam(required = false) Long propiedadId, @RequestParam(required = false) Long clienteId) {
        LocalDate[] fechas = mes == null && fechaInicio == null && fechaFin == null ? new LocalDate[]{null,null} : periodo(mes, fechaInicio, fechaFin);
        return service.morosidad(fechas[0], fechas[1], propiedadId, clienteId);
    }
}

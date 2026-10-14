package com.blue.apartamentos.services;

import com.blue.apartamentos.models.ClienteModel;
import java.util.List;
import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blue.apartamentos.models.PropiedadModel;
import com.blue.apartamentos.repositories.IPropiedadRepository;

@Service
public class PropiedadService {

    @Autowired
    private IPropiedadRepository propiedadRepository;

    public List<PropiedadModel> getAllPropiedades() {
        return propiedadRepository.findAll();
    }

    public Optional<PropiedadModel> getPropiedadById(Long id) {
        return propiedadRepository.findById(id);
    }

    @Autowired
    private ClienteService clienteService;

    public PropiedadModel savePropiedad(PropiedadModel propiedad) {
        if (propiedad.getPropietario() == null || propiedad.getPropietario().getId() == null) {
            throw new IllegalArgumentException("Selecciona un propietario.");
        }
        ClienteModel propietario = clienteService.getClienteById(propiedad.getPropietario().getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe el propietario."));
        if (propiedad.getId_propiedad() == null && !propietario.esPropietarioActivo()) {
            throw new IllegalArgumentException("Selecciona un propietario activo de tipo PROPIETARIO.");
        }
        if (propiedad.getTipo() == null || propiedad.getEstado() == null
                || vacio(propiedad.getTitulo()) || vacio(propiedad.getDescripcion())
                || vacio(propiedad.getDireccion()) || vacio(propiedad.getCiudad())
                || vacio(propiedad.getEstidadFederativa()) || vacio(propiedad.getPais())
                || !Double.isFinite(propiedad.getPrecio_noche()) || propiedad.getPrecio_noche() <= 0
                || BigDecimal.valueOf(propiedad.getPrecio_noche()).stripTrailingZeros().scale() > 2
                || propiedad.getCapacidad() < 1 || propiedad.getMetro_cuadrados() < 1
                || propiedad.getNum_habitaciones() < 0 || propiedad.getNum_banos() < 0
                || propiedad.getNum_estacionamientos() < 0
                || propiedad.getCodigo_postal() < 0 || propiedad.getCodigo_postal() > 99999
                || !Double.isFinite(propiedad.getLatitud()) || Math.abs(propiedad.getLatitud()) > 90
                || !Double.isFinite(propiedad.getLongitud()) || Math.abs(propiedad.getLongitud()) > 180) {
            throw new IllegalArgumentException("Completa las características obligatorias con valores válidos y precio mayor a cero.");
        }
        propiedad.setPropietario(propietario);
        return propiedadRepository.save(propiedad);
    } 
    
    public void deletePropiedad(Long id) {
        propiedadRepository.deleteById(id);
    }

    private boolean vacio(String texto) { return texto == null || texto.isBlank(); }
}

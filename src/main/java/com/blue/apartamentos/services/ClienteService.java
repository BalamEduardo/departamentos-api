package com.blue.apartamentos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blue.apartamentos.repositories.IClienteRepository;
import com.blue.apartamentos.models.ClienteModel;

@Service
public class ClienteService {
    
    @Autowired
    private IClienteRepository clienteRepository;

    public List<ClienteModel> getAllClientes() {
        return clienteRepository.findAll();
    }

    public Optional<ClienteModel>  getClienteById(Long id) {
        return clienteRepository.findById(id);
    }

    public ClienteModel saveCliente(ClienteModel cliente) {
        if (cliente.getNombres() == null || cliente.getNombres().isBlank()
                || cliente.getApellidos() == null || cliente.getApellidos().isBlank()
                || cliente.getEmail() == null || !cliente.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                || cliente.getPassword() == null || cliente.getPassword().isBlank()
                || cliente.getTipo() == null) {
            throw new IllegalArgumentException("Indica nombres, apellidos, email válido, tipo y contraseña.");
        }
        cliente.setEmail(cliente.getEmail().trim().toLowerCase(java.util.Locale.ROOT));
        if (cliente.getIne() != null && cliente.getIne().isBlank()) cliente.setIne(null);
        return clienteRepository.save(cliente);
    }

}

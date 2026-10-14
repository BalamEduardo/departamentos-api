package com.blue.apartamentos.controllers;

import com.blue.apartamentos.models.ClienteModel;
import com.blue.apartamentos.services.ClienteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // Obtener todos los clientes
    @GetMapping
    public List<ClienteModel> getAllClientes() {
        return clienteService.getAllClientes();
    }

    // Obtener un cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<ClienteModel> getClienteById(@PathVariable Long id) {
        Optional <ClienteModel> cliente = clienteService.getClienteById(id);
        return cliente.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());        
    }

    // Crear un nuevo cliente
    @PostMapping
    public ClienteModel createCliente(@RequestBody ClienteModel cliente) {
        cliente.setId(null);
        return clienteService.saveCliente(cliente);
    }

    // Actualizar un cliente existente
    @PutMapping("/{id}")
    public ResponseEntity<ClienteModel> updateCliente(@PathVariable Long id, @RequestBody ClienteModel clienteDetails) {
        Optional<ClienteModel> clienteOptional = clienteService.getClienteById(id);
        if (clienteOptional.isPresent()) {
            ClienteModel clienteToUpdate = clienteOptional.get();
            clienteToUpdate.copiarTipoDe(clienteDetails);
            clienteToUpdate.setNombres(clienteDetails.getNombres());
            clienteToUpdate.setApellidos(clienteDetails.getApellidos());
            clienteToUpdate.setEmail(clienteDetails.getEmail());
            clienteToUpdate.setTelefono(clienteDetails.getTelefono());
            clienteToUpdate.setFechaNacimiento(clienteDetails.getFechaNacimiento());
            clienteToUpdate.setIne(clienteDetails.getIne());
            clienteToUpdate.setDireccion(clienteDetails.getDireccion());
            if (clienteDetails.getPassword() != null && !clienteDetails.getPassword().isBlank()) {
                clienteToUpdate.setPassword(clienteDetails.getPassword());
            }

            ClienteModel updatedCliente = clienteService.saveCliente(clienteToUpdate);
            return ResponseEntity.ok(updatedCliente);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    // Inactivar al cliente para conservar el historial de reservas y pagos.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        Optional<ClienteModel> cliente = clienteService.getClienteById(id);
        if (cliente.isPresent()) {
            cliente.get().desactivar();
            clienteService.saveCliente(cliente.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<ClienteModel> activarCliente(@PathVariable Long id) {
        Optional<ClienteModel> cliente = clienteService.getClienteById(id);
        if (cliente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        cliente.get().activar();
        return ResponseEntity.ok(clienteService.saveCliente(cliente.get()));
    }
}

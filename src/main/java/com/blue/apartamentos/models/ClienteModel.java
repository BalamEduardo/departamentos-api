package com.blue.apartamentos.models;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;

@Entity
@Table(name = "clientes")
public class ClienteModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cliente", nullable = false, length = 20)
    private TipoCliente tipo = TipoCliente.CLIENTE;

    @Column(name = "nombre", nullable = false, length = 35)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 35)
    private String apellidos;

    @Column(name = "email", unique = true, nullable = false, length = 50)
    private String email;

    @Column(name = "telefono", length = 15)
    private String telefono;

    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;
    
    @Column(name = "numero_ine", unique = true, length = 20)
    private String ine;

    @Column(name = "direccion", length = 100)
    private String direccion;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus", nullable = false)
    private ClienteStatus estatus = ClienteStatus.ACTIVO;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    private enum ClienteStatus {
        ACTIVO,
        INACTIVO,
        SUSPENDIDO
    }

    private enum TipoCliente {
        PROPIETARIO,
        CLIENTE,
        EMPRESA
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoCliente getTipo() {
        return tipo;
    }

    public void setTipo(TipoCliente tipo) {
        this.tipo = tipo;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getIne() {
        return ine;
    }

    public void setIne(String ine) {
        this.ine = ine;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public ClienteStatus getEstatus() {
        return estatus;
    }

    public void setEstatus(ClienteStatus estatus) {
        this.estatus = estatus;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public ClienteModel() {
        
    }

    @PrePersist
    public void prepararRegistro() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (estatus == null) {
            estatus = ClienteStatus.ACTIVO;
        }
    }

    public void desactivar() {
        estatus = ClienteStatus.INACTIVO;
    }

    public void activar() {
        estatus = ClienteStatus.ACTIVO;
    }

    public void copiarTipoDe(ClienteModel otro) {
        tipo = otro.tipo;
    }

    public boolean esArrendatarioActivo() {
        return tipo == TipoCliente.CLIENTE && estatus == ClienteStatus.ACTIVO;
    }

    public boolean esPropietarioActivo() {
        return tipo == TipoCliente.PROPIETARIO && estatus == ClienteStatus.ACTIVO;
    }

    public ClienteModel(Long id, TipoCliente tipo, String nombres, String apellidos, String email,
            String telefono, Date fechaNacimiento, String ine, String direccion, LocalDateTime fechaRegistro,
            ClienteStatus estatus, String password) {
        this.id = id;
        this.tipo = tipo;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.ine = ine;
        this.direccion = direccion;
        this.fechaRegistro = LocalDateTime.now();
        this.estatus = estatus;
        this.password = password;
    }

    // Crea otro constructor con los atributos necesarios para registrar un cliente
    public ClienteModel(TipoCliente tipo, String nombres, String apellidos, String email,
            String telefono, Date fechaNacimiento, String ine, String direccion, ClienteStatus estatus, String password) {
        this.tipo = tipo;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.ine = ine;
        this.direccion = direccion;
        this.fechaRegistro = LocalDateTime.now();
        this.estatus = estatus;
        this.password = password;
    }
}

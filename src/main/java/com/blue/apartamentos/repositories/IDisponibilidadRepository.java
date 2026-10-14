package com.blue.apartamentos.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.blue.apartamentos.models.DisponibilidadModel;
public interface IDisponibilidadRepository extends JpaRepository<DisponibilidadModel, Long> {}

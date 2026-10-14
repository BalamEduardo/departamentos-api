package com.blue.apartamentos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blue.apartamentos.models.PropiedadModel;

@Repository
public interface IPropiedadRepository extends JpaRepository<PropiedadModel, Long> {
    
}

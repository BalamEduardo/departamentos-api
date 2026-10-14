package com.blue.apartamentos.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blue.apartamentos.models.HabitacionModel;
import com.blue.apartamentos.models.PropiedadModel;

@Repository
public interface IHabitacionRepository extends JpaRepository<HabitacionModel, Long> {

    List<HabitacionModel> findByPropiedad(PropiedadModel propiedad);
}
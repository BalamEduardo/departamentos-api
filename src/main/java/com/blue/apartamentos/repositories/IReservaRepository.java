package com.blue.apartamentos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blue.apartamentos.models.ReservaModel;

@Repository
public interface IReservaRepository extends JpaRepository<ReservaModel, Long> {

    @org.springframework.data.jpa.repository.Query("select r from ReservaModel r where r.propiedad.id_propiedad = :id")
    java.util.List<ReservaModel> findByPropiedadId(@org.springframework.data.repository.query.Param("id") Long id);
}

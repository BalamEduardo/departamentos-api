package com.blue.apartamentos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.blue.apartamentos.models.ClienteModel;

@Repository
public interface IClienteRepository extends JpaRepository<ClienteModel, Long> {
    //aqui no lleva codigo
}

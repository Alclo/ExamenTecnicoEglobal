package com.examen.productos.repositories;

import com.examen.productos.entitys.Derechohabiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DerechohabienteRepository extends JpaRepository<Derechohabiente, Long> {

    List<Derechohabiente> findByNombreContainingIgnoreCase(String nombre);
}

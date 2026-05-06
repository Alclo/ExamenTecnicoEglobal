package com.examen.productos.interfaces;

import com.examen.productos.dtos.DerechohabientePatchRequest;
import com.examen.productos.dtos.DerechohabienteRequest;
import com.examen.productos.dtos.DerechohabienteResponse;

import java.util.List;

public interface IDerechohabienteService {

    List<DerechohabienteResponse> listar();

    DerechohabienteResponse buscarPorId(Long id);

    List<DerechohabienteResponse> buscarPorNombre(String nombre);

    DerechohabienteResponse crear(DerechohabienteRequest request);

    DerechohabienteResponse actualizar(Long id, DerechohabienteRequest request);

    DerechohabienteResponse actualizarParcial(Long id, DerechohabientePatchRequest request);
}

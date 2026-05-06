package com.examen.productos.interfaces;

import com.examen.productos.dtos.ProductoRequest;
import com.examen.productos.dtos.ProductoResponse;

import java.util.List;

public interface IProductoService {

    List<ProductoResponse> listar();

    ProductoResponse buscarPorId(Long id);

    List<ProductoResponse> buscarPorNombre(String nombre);

    ProductoResponse crear(ProductoRequest request);

    ProductoResponse actualizar(Long id, ProductoRequest request);

    void eliminar(Long id);
}

package com.examen.productos.services;

import com.examen.productos.dtos.ProductoRequest;
import com.examen.productos.dtos.ProductoResponse;
import com.examen.productos.entitys.Producto;
import com.examen.productos.exceptions.ResourceNotFoundException;
import com.examen.productos.interfaces.IProductoService;
import com.examen.productos.repositories.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductoService implements IProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ProductoResponse> listar() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ProductoResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    @Override
    public List<ProductoResponse> buscarPorNombre(String nombre) {
        return repository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        Producto entity = new Producto();
        entity.setNombre(request.nombre());
        entity.setPrecio(request.precio());
        entity.setStock(request.stock() != null ? request.stock() : 0);
        entity.setDescripcion(request.descripcion());

        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        entity.setNombre(request.nombre());
        entity.setPrecio(request.precio());
        entity.setStock(request.stock() != null ? request.stock() : entity.getStock());
        entity.setDescripcion(request.descripcion());

        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    private ProductoResponse toResponse(Producto entity) {
        return new ProductoResponse(
                entity.getId(),
                entity.getNombre(),
                entity.getPrecio(),
                entity.getStock(),
                entity.getDescripcion()
        );
    }
}

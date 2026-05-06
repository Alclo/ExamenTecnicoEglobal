package com.examen.productos.services;

import com.examen.productos.dtos.DerechohabientePatchRequest;
import com.examen.productos.dtos.DerechohabienteRequest;
import com.examen.productos.dtos.DerechohabienteResponse;
import com.examen.productos.entitys.Derechohabiente;
import com.examen.productos.exceptions.ResourceNotFoundException;
import com.examen.productos.interfaces.IDerechohabienteService;
import com.examen.productos.repositories.DerechohabienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DerechohabienteService implements IDerechohabienteService {

    private final DerechohabienteRepository repository;

    public DerechohabienteService(DerechohabienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<DerechohabienteResponse> listar() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public DerechohabienteResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Derechohabiente no encontrado con id: " + id));
    }

    @Override
    public List<DerechohabienteResponse> buscarPorNombre(String nombre) {
        return repository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public DerechohabienteResponse crear(DerechohabienteRequest request) {
        Derechohabiente entity = new Derechohabiente();
        entity.setNombre(request.nombre());
        entity.setImporte(request.importe());
        entity.setCuenta(request.cuenta());

        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public DerechohabienteResponse actualizar(Long id, DerechohabienteRequest request) {
        Derechohabiente entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Derechohabiente no encontrado con id: " + id));

        entity.setNombre(request.nombre());
        entity.setImporte(request.importe());
        entity.setCuenta(request.cuenta());

        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public DerechohabienteResponse actualizarParcial(Long id, DerechohabientePatchRequest request) {
        Derechohabiente entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Derechohabiente no encontrado con id: " + id));

        if (request.nombre() != null && !request.nombre().isBlank()) {
            entity.setNombre(request.nombre());
        }
        if (request.importe() != null) {
            entity.setImporte(request.importe());
        }
        if (request.cuenta() != null) {
            entity.setCuenta(request.cuenta());
        }

        return toResponse(repository.save(entity));
    }

    private DerechohabienteResponse toResponse(Derechohabiente entity) {
        return new DerechohabienteResponse(
                entity.getId(),
                entity.getNombre(),
                entity.getImporte(),
                entity.getCuenta()
        );
    }
}

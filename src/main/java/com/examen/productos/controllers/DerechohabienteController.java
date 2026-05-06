package com.examen.productos.controllers;

import com.examen.productos.dtos.DerechohabientePatchRequest;
import com.examen.productos.dtos.DerechohabienteRequest;
import com.examen.productos.dtos.DerechohabienteResponse;
import com.examen.productos.interfaces.IDerechohabienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/derechohabiente")
@Tag(name = "Derechohabientes", description = "Gestión de derechohabientes de la consulta")
@SecurityRequirement(name = "Bearer Authentication")
public class DerechohabienteController {

    private final IDerechohabienteService service;

    public DerechohabienteController(IDerechohabienteService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos los derechohabientes")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<DerechohabienteResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener derechohabiente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Derechohabiente encontrado"),
            @ApiResponse(responseCode = "404", description = "Derechohabiente no encontrado")
    })
    public ResponseEntity<DerechohabienteResponse> obtenerPorId(
            @Parameter(description = "ID del derechohabiente") @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/buscar")
    @Operation(
            summary = "Buscar derechohabientes por nombre",
            description = "Devuelve una lista de derechohabientes cuyo nombre contenga el texto indicado (sin distinguir mayúsculas/minúsculas)"
    )
    @ApiResponse(responseCode = "200", description = "Búsqueda realizada correctamente")
    public ResponseEntity<List<DerechohabienteResponse>> buscarPorNombre(
            @Parameter(description = "Texto a buscar en el nombre") @RequestParam String nombre) {
        return ResponseEntity.ok(service.buscarPorNombre(nombre));
    }

    @PostMapping
    @Operation(summary = "Crear derechohabiente", description = "Registra un nuevo derechohabiente en el sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Derechohabiente creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<DerechohabienteResponse> crear(@Valid @RequestBody DerechohabienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar derechohabiente (reemplazo completo)",
            description = "Reemplaza todos los datos del derechohabiente con el ID indicado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Derechohabiente actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Derechohabiente no encontrado")
    })
    public ResponseEntity<DerechohabienteResponse> actualizar(
            @Parameter(description = "ID del derechohabiente") @PathVariable Long id,
            @Valid @RequestBody DerechohabienteRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Actualizar derechohabiente parcialmente",
            description = "Actualiza solo los campos enviados; los campos omitidos o nulos conservan su valor actual"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Derechohabiente actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Derechohabiente no encontrado")
    })
    public ResponseEntity<DerechohabienteResponse> actualizarParcial(
            @Parameter(description = "ID del derechohabiente") @PathVariable Long id,
            @Valid @RequestBody DerechohabientePatchRequest request) {
        return ResponseEntity.ok(service.actualizarParcial(id, request));
    }
}

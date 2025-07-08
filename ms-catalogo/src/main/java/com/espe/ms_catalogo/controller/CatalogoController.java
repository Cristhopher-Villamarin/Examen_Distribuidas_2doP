package com.espe.ms_catalogo.controller;

import com.espe.ms_catalogo.dto.CatalogoDto;
import com.espe.ms_catalogo.entity.Catalogo;
import com.espe.ms_catalogo.service.CatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalogos")
public class CatalogoController {

    @Autowired
    private CatalogoService service;

    // 🔹 GET /catalogos
    @GetMapping
    public List<Catalogo> listarTodas() {
        return service.listarTodas();
    }

    // 🔹 GET /catalogos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Catalogo> obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 POST /catalogos
    @PostMapping
    public ResponseEntity<String> guardarCatalogo(@RequestBody CatalogoDto dto) {
        service.guardar(dto);
        return ResponseEntity.ok("Catálogo guardado correctamente");
    }

    // 🔹 PUT /catalogos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Catalogo> actualizar(@PathVariable Long id, @RequestBody CatalogoDto dto) {
        try {
            Catalogo actualizado = service.actualizar(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔹 DELETE /catalogos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.ok("Catálogo eliminado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
package com.espe.ms_catalogo.service;

import com.espe.ms_catalogo.dto.CatalogoDto;
import com.espe.ms_catalogo.entity.Catalogo;
import com.espe.ms_catalogo.repository.CatalogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CatalogoService {

    @Autowired
    private CatalogoRepository repository;

    // Listar todos los catálogos
    public List<Catalogo> listarTodas() {
        return repository.findAll();
    }

    // Obtener catálogo por ID
    public Optional<Catalogo> obtenerPorId(Long id) {
        return repository.findById(id);
    }

    // Guardar nuevo catálogo
    public void guardar(CatalogoDto dto) {
        Catalogo catalogo = new Catalogo();
        catalogo.setTipo(dto.getTipo());

        if ("LIBRO".equalsIgnoreCase(dto.getTipo()) && dto.getLibro() != null) {
            catalogo.setTitulo(dto.getLibro().getTitulo());
            catalogo.setAnioPublicacion(dto.getLibro().getAnioPublicacion());
            catalogo.setResumen(dto.getLibro().getResumen());
            catalogo.setEditorial(dto.getLibro().getEditorial());
            catalogo.setIsbn(dto.getLibro().getIsbn());

        } else if ("ARTICULO".equalsIgnoreCase(dto.getTipo()) && dto.getArticulo() != null) {
            catalogo.setTitulo(dto.getArticulo().getTitulo());
            catalogo.setAnioPublicacion(dto.getArticulo().getAnioPublicacion());
            catalogo.setResumen(dto.getArticulo().getResumen());
            catalogo.setEditorial(dto.getArticulo().getEditorial());
            catalogo.setIsbn(dto.getArticulo().getIsbn());

        } else {
            throw new IllegalArgumentException("Tipo de publicación no válido o datos incompletos");
        }

        repository.save(catalogo);
    }

    // Actualizar catálogo existente
    public Catalogo actualizar(Long id, CatalogoDto dto) {
        Catalogo catalogo = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Catálogo no encontrado con ID: " + id));

        catalogo.setTipo(dto.getTipo());

        if ("LIBRO".equalsIgnoreCase(dto.getTipo()) && dto.getLibro() != null) {
            catalogo.setTitulo(dto.getLibro().getTitulo());
            catalogo.setAnioPublicacion(dto.getLibro().getAnioPublicacion());
            catalogo.setResumen(dto.getLibro().getResumen());
            catalogo.setEditorial(dto.getLibro().getEditorial());
            catalogo.setIsbn(dto.getLibro().getIsbn());

        } else if ("ARTICULO".equalsIgnoreCase(dto.getTipo()) && dto.getArticulo() != null) {
            catalogo.setTitulo(dto.getArticulo().getTitulo());
            catalogo.setAnioPublicacion(dto.getArticulo().getAnioPublicacion());
            catalogo.setResumen(dto.getArticulo().getResumen());
            catalogo.setEditorial(dto.getArticulo().getEditorial());
            catalogo.setIsbn(dto.getArticulo().getIsbn());
        }

        return repository.save(catalogo);
    }

    // Eliminar catálogo
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("No existe catálogo con ID: " + id);
        }
        repository.deleteById(id);
    }
}
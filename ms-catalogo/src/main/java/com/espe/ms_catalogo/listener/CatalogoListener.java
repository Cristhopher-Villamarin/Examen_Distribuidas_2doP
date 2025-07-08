package com.espe.ms_catalogo.listener;

import com.espe.ms_catalogo.dto.ArticulosCientificosDto;
import com.espe.ms_catalogo.dto.CatalogoDto;
import com.espe.ms_catalogo.dto.LibroDto;
import com.espe.ms_catalogo.service.CatalogoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CatalogoListener {

    @Autowired
    private CatalogoService service;

    @Autowired
    private ObjectMapper mapper;

    @RabbitListener(queues = "catalogo.cola")
    public void recibirMensajes(String mensaje) {
        try {
            JsonNode root = mapper.readTree(mensaje);

            CatalogoDto catalogoDto = new CatalogoDto();

            // Si el mensaje viene con tipo directo (mensaje plano)
            if (root.has("tipo")) {
                String tipo = root.get("tipo").asText().toUpperCase();
                catalogoDto.setTipo(tipo);

                if ("LIBRO".equalsIgnoreCase(tipo)) {
                    LibroDto libro = new LibroDto();
                    libro.setTitulo(root.get("titulo").asText());
                    libro.setAnioPublicacion(root.get("anioPublicacion").asInt());
                    libro.setResumen(root.get("resumen").asText());
                    libro.setEditorial(root.get("editorial").asText());
                    libro.setIsbn(root.get("isbn").asText());
                    // Opcionalmente puedes ignorar o rellenar estos si no vienen:
                    libro.setGenero(null);
                    libro.setNumeroPaginas(0);
                    libro.setIdAutor(null);

                    catalogoDto.setLibro(libro);

                } else if ("ARTICULO".equalsIgnoreCase(tipo)) {
                    ArticulosCientificosDto articulo = new ArticulosCientificosDto();
                    articulo.setTitulo(root.get("titulo").asText());
                    articulo.setAnioPublicacion(root.get("anioPublicacion").asInt());
                    articulo.setResumen(root.get("resumen").asText());
                    articulo.setEditorial(root.get("editorial").asText());
                    articulo.setIsbn(root.get("isbn").asText());
                    // Rellenar con null o vacío si no hay más info
                    articulo.setOrcid(null);
                    articulo.setFechaPublicacion(null);
                    articulo.setRevista(null);
                    articulo.setAreaInvestigacion(null);
                    articulo.setIdAutor(null);

                    catalogoDto.setArticulo(articulo);
                } else {
                    System.err.println("Tipo de publicación no reconocido: " + tipo);
                    return;
                }

                service.guardar(catalogoDto);
                return;
            }

            // Si el mensaje tiene estructura con 'objeto'
            JsonNode objeto = root.get("objeto");
            if (objeto != null) {
                // Aquí puedes llamar al otro procesamiento (como ya lo tienes implementado)
                System.out.println("Procesar estructura con 'objeto'...");
                // Puedes incluso reutilizar el código del otro bloque
            } else {
                System.err.println("Mensaje inválido: no contiene campo 'tipo' ni 'objeto'");
            }

        } catch (Exception e) {
            System.err.println("Error procesando mensaje en CatalogoListener: " + e.getMessage());
            e.printStackTrace();
        }
    }
    }


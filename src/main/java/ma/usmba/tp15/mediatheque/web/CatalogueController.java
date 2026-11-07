package ma.usmba.tp15.mediatheque.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import ma.usmba.tp15.mediatheque.modele.Ouvrage;
import ma.usmba.tp15.mediatheque.service.CatalogueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalogue")
@RequiredArgsConstructor
public class CatalogueController {

    private final CatalogueService service;

    @GetMapping("/ouvrages")
    public List<Ouvrage> ouvrages() {
        return service.tous();
    }

    @GetMapping("/ouvrages/{id}")
    public ResponseEntity<Ouvrage> ouvrage(@PathVariable Long id) {
        return service.parId(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/categories/{categorie}")
    public List<Ouvrage> parCategorie(@PathVariable String categorie) {
        return service.parCategorie(categorie);
    }

    @GetMapping("/disponibles")
    public List<Ouvrage> disponibles() {
        return service.disponibles();
    }

    @GetMapping("/synthese")
    public Map<String, Object> synthese() {
        Map<String, Object> reponse = new LinkedHashMap<>();
        reponse.put("application", "conteneur-mediatheque");
        reponse.put("totalOuvrages", service.total());
        reponse.put("ouvragesDisponibles", service.disponibles().size());
        reponse.put("ouvragesInformatique", service.totalParCategorie("informatique"));
        return reponse;
    }
}

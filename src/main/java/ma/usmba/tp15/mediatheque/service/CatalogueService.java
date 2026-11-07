package ma.usmba.tp15.mediatheque.service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import ma.usmba.tp15.mediatheque.depot.OuvrageDepot;
import ma.usmba.tp15.mediatheque.modele.Ouvrage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogueService {

    private final OuvrageDepot depot;

    /**
     * Amorcage idempotent : si la base MySQL contient deja les ouvrages
     * (volume Docker conserve), on ne reinsere rien.
     */
    @PostConstruct
    void amorcer() {
        if (depot.count() > 0) {
            return;
        }
        depot.saveAll(List.of(
                Ouvrage.builder().titre("Le Sel de tous les oublis").auteur("Yasmina Khadra")
                        .categorie("roman").anneeParution(2020).disponible(true)
                        .dateAcquisition(LocalDate.of(2023, 2, 14)).build(),
                Ouvrage.builder().titre("Architecture logicielle expliquee").auteur("Rachid Guerraoui")
                        .categorie("informatique").anneeParution(2019).disponible(false)
                        .dateAcquisition(LocalDate.of(2022, 9, 3)).build(),
                Ouvrage.builder().titre("Histoire du Maghreb contemporain").auteur("Pierre Vermeren")
                        .categorie("histoire").anneeParution(2016).disponible(true)
                        .dateAcquisition(LocalDate.of(2021, 11, 25)).build(),
                Ouvrage.builder().titre("Les algorithmes du quotidien").auteur("Claire Mathieu")
                        .categorie("informatique").anneeParution(2021).disponible(true)
                        .dateAcquisition(LocalDate.of(2024, 1, 9)).build(),
                Ouvrage.builder().titre("Contes de la medina").auteur("Ahmed Bouziane")
                        .categorie("roman").anneeParution(2014).disponible(false)
                        .dateAcquisition(LocalDate.of(2020, 6, 18)).build()));
    }

    public List<Ouvrage> tous() {
        return depot.findAll();
    }

    public List<Ouvrage> parCategorie(String categorie) {
        return depot.findByCategorieOrderByTitreAsc(categorie);
    }

    public List<Ouvrage> disponibles() {
        return depot.findByDisponibleTrueOrderByTitreAsc();
    }

    public Optional<Ouvrage> parId(Long id) {
        return depot.findById(id);
    }

    public long total() {
        return depot.count();
    }

    public long totalParCategorie(String categorie) {
        return depot.countByCategorie(categorie);
    }
}

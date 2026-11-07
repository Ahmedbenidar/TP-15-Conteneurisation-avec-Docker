package ma.usmba.tp15.mediatheque.depot;

import java.util.List;
import ma.usmba.tp15.mediatheque.modele.Ouvrage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OuvrageDepot extends JpaRepository<Ouvrage, Long> {

    List<Ouvrage> findByCategorieOrderByTitreAsc(String categorie);

    List<Ouvrage> findByDisponibleTrueOrderByTitreAsc();

    long countByCategorie(String categorie);
}

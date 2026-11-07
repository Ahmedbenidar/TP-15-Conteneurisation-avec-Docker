package ma.usmba.tp15.mediatheque.modele;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ouvrage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ouvrage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String titre;

    @Column(nullable = false, length = 60)
    private String auteur;

    @Column(nullable = false, length = 30)
    private String categorie;

    @Column(name = "annee_parution", nullable = false)
    private Integer anneeParution;

    @Column(nullable = false)
    private Boolean disponible;

    @Column(name = "date_acquisition", nullable = false)
    private LocalDate dateAcquisition;
}

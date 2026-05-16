package co.gov.idrd.siged.domain.repository;

import co.gov.idrd.siged.domain.model.EstadoTorneo;
import co.gov.idrd.siged.domain.model.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    List<Torneo> findByEstado(EstadoTorneo estado);
}

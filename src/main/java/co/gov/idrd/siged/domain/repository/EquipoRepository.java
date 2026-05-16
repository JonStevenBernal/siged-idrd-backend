package co.gov.idrd.siged.domain.repository;

import co.gov.idrd.siged.domain.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    List<Equipo> findByTorneoId(Long torneoId);

    boolean existsByTorneoIdAndNombreIgnoreCase(Long torneoId, String nombre);
}

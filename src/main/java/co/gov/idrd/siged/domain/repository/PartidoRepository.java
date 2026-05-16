package co.gov.idrd.siged.domain.repository;

import co.gov.idrd.siged.domain.model.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long> {

    List<Partido> findByTorneoIdOrderByFechaProgramadaAscIdAsc(Long torneoId);

    List<Partido> findByArbitroDocumentoOrderByFechaProgramadaAsc(String arbitroDocumento);
}

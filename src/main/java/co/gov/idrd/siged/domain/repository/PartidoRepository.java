package co.gov.idrd.siged.domain.repository;

import co.gov.idrd.siged.domain.model.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long> {

    /**
     * Consulta los partidos de un torneo ordenados por fecha programada e identificador.
     *
     * @param torneoId identificador del torneo.
     * @return lista ordenada de partidos del torneo; puede estar vacia.
     */
    List<Partido> findByTorneoIdOrderByFechaProgramadaAscIdAsc(Long torneoId);

    /**
     * Consulta los partidos asignados a un arbitro ordenados por fecha programada.
     *
     * @param arbitroDocumento documento del arbitro responsable del partido.
     * @return lista ordenada de partidos asignados al arbitro.
     */
    List<Partido> findByArbitroDocumentoOrderByFechaProgramadaAsc(String arbitroDocumento);
}

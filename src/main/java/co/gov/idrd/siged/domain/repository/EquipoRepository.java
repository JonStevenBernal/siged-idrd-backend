package co.gov.idrd.siged.domain.repository;

import co.gov.idrd.siged.domain.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    /**
     * Consulta los equipos asociados al identificador de un torneo.
     *
     * @param torneoId identificador del torneo propietario de las inscripciones.
     * @return lista de equipos registrados para el torneo indicado; puede estar vacia.
     */
    List<Equipo> findByTorneoId(Long torneoId);

    /**
     * Verifica duplicidad de nombre de equipo dentro del mismo torneo ignorando mayusculas.
     *
     * @param torneoId identificador del torneo donde se valida la inscripcion.
     * @param nombre nombre del equipo a validar.
     * @return {@code true} si ya existe un equipo con ese nombre en el torneo.
     */
    boolean existsByTorneoIdAndNombreIgnoreCase(Long torneoId, String nombre);
}

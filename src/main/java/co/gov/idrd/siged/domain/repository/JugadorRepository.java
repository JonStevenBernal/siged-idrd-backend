package co.gov.idrd.siged.domain.repository;

import co.gov.idrd.siged.domain.model.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Puerto de persistencia JPA para la entidad {@link Jugador}.
 */
@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {

    /**
     * Consulta los jugadores vinculados a un equipo.
     *
     * @param equipoId identificador del equipo propietario de los jugadores.
     * @return lista de jugadores registrados para el equipo indicado; puede estar vacia.
     */
    List<Jugador> findByEquipoId(Long equipoId);

    /**
     * Verifica si un documento de identidad ya esta inscrito en algun equipo del torneo.
     * La consulta evita que un jugador participe con dos equipos en el mismo torneo.
     *
     * @param documento documento de identidad del jugador.
     * @param torneoId identificador del torneo donde se valida la unicidad.
     * @return {@code true} si el documento ya existe dentro del torneo.
     */
    @Query("""
            select count(j) > 0
            from Jugador j
            where j.documentoIdentidad = :documento
            and j.equipo.torneo.id = :torneoId
            """)
    boolean existsByDocumentoInTorneo(@Param("documento") String documento, @Param("torneoId") Long torneoId);
}

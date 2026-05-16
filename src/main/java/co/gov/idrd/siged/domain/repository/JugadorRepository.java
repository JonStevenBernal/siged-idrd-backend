package co.gov.idrd.siged.domain.repository;

import co.gov.idrd.siged.domain.model.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {

    List<Jugador> findByEquipoId(Long equipoId);

    @Query("""
            select count(j) > 0
            from Jugador j
            where j.documentoIdentidad = :documento
            and j.equipo.torneo.id = :torneoId
            """)
    boolean existsByDocumentoInTorneo(@Param("documento") String documento, @Param("torneoId") Long torneoId);
}

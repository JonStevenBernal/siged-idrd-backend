package co.gov.idrd.siged.domain.repository;

import co.gov.idrd.siged.domain.model.EstadoTorneo;
import co.gov.idrd.siged.domain.model.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Long> {

    /**
     * Verifica si ya existe un torneo con el mismo nombre ignorando mayusculas.
     *
     * @param nombre nombre del torneo a validar.
     * @return {@code true} cuando el nombre ya esta registrado.
     */
    boolean existsByNombreIgnoreCase(String nombre);

    /**
     * Consulta torneos filtrando por su estado operativo.
     *
     * @param estado estado del ciclo de vida del torneo.
     * @return lista de torneos que coinciden con el estado solicitado.
     */
    List<Torneo> findByEstado(EstadoTorneo estado);
}

package co.gov.idrd.siged.domain.repository;

import co.gov.idrd.siged.domain.model.UsuarioCuenta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioCuentaRepository extends MongoRepository<UsuarioCuenta, String> {

    boolean existsByCedula(String cedula);

    boolean existsByCorreoIgnoreCase(String correo);

    Optional<UsuarioCuenta> findByCorreoIgnoreCase(String correo);
}

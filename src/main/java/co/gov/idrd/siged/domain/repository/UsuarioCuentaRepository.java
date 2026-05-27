package co.gov.idrd.siged.domain.repository;

import co.gov.idrd.siged.domain.model.UsuarioCuenta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioCuentaRepository extends MongoRepository<UsuarioCuenta, String> {

    /**
     * Verifica si existe una cuenta asociada a una cedula.
     *
     * @param cedula numero de identificacion del usuario.
     * @return {@code true} si la cedula ya esta registrada.
     */
    boolean existsByCedula(String cedula);

    /**
     * Verifica si existe una cuenta asociada a un correo ignorando mayusculas.
     *
     * @param correo correo electronico a validar.
     * @return {@code true} si el correo ya esta registrado.
     */
    boolean existsByCorreoIgnoreCase(String correo);

    /**
     * Busca una cuenta de usuario por correo ignorando mayusculas.
     *
     * @param correo correo electronico usado para autenticacion.
     * @return usuario encontrado o {@link Optional#empty()} si no existe.
     */
    Optional<UsuarioCuenta> findByCorreoIgnoreCase(String correo);
}

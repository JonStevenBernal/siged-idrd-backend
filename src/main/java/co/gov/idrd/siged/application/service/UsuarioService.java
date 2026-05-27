package co.gov.idrd.siged.application.service;

import co.gov.idrd.siged.application.dto.AuthPayload;
import co.gov.idrd.siged.application.dto.LoginInput;
import co.gov.idrd.siged.application.dto.UsuarioDTO;
import co.gov.idrd.siged.application.dto.UsuarioInput;
import co.gov.idrd.siged.domain.model.EstadoCuenta;
import co.gov.idrd.siged.domain.model.RolUsuario;
import co.gov.idrd.siged.domain.model.UsuarioCuenta;
import co.gov.idrd.siged.domain.repository.UsuarioCuentaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioCuentaRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Recibe las dependencias necesarias para gestionar usuarios y claves.
     *
     * @param usuarioRepository repositorio MongoDB de cuentas de usuario.
     * @param passwordEncoder componente encargado de cifrar y validar claves.
     */
    public UsuarioService(UsuarioCuentaRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Lista todas las cuentas de usuario registradas en MongoDB.
     *
     * @return usuarios registrados sin incluir hashes de clave.
     */
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream().map(this::toDTO).toList();
    }

    /**
     * Crea una cuenta de usuario con clave cifrada y estado activo.
     *
     * @param input datos de cedula, nombre, correo, rol y clave del nuevo usuario.
     * @return usuario persistido como DTO seguro.
     * @throws IllegalArgumentException si la cedula o el correo ya existen, o si el rol no es valido.
     */
    public UsuarioDTO crearUsuario(UsuarioInput input) {
        if (usuarioRepository.existsByCedula(input.cedula()) || usuarioRepository.existsByCorreoIgnoreCase(input.correo())) {
            throw new IllegalArgumentException("El usuario ya esta registrado");
        }

        UsuarioCuenta usuario = new UsuarioCuenta();
        usuario.setCedula(input.cedula());
        usuario.setNombre(input.nombre());
        usuario.setCorreo(input.correo());
        usuario.setRol(RolUsuario.valueOf(input.rol()));
        usuario.setEstadoCuenta(EstadoCuenta.ACTIVO);
        usuario.setPasswordHash(passwordEncoder.encode(input.password()));

        return toDTO(usuarioRepository.save(usuario));
    }

    /**
     * Valida correo y clave para retornar el usuario autenticado con token temporal.
     *
     * @param input credenciales enviadas por el cliente.
     * @return payload con el usuario autenticado y token temporal de desarrollo.
     * @throws IllegalArgumentException si el correo no existe, la cuenta esta inactiva o la clave no coincide.
     */
    public AuthPayload autenticar(LoginInput input) {
        UsuarioCuenta usuario = usuarioRepository.findByCorreoIgnoreCase(input.correo())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales invalidas"));
        if (usuario.getEstadoCuenta() != EstadoCuenta.ACTIVO || !passwordEncoder.matches(input.password(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales invalidas");
        }
        return new AuthPayload(toDTO(usuario), "dev-token-" + UUID.randomUUID());
    }

    /**
     * Cambia una cuenta a estado inactivo si el usuario existe.
     *
     * @param id identificador MongoDB de la cuenta de usuario.
     * @return usuario inactivado como DTO o {@link Optional#empty()} si no existe.
     */
    public Optional<UsuarioDTO> inactivarUsuario(String id) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setEstadoCuenta(EstadoCuenta.INACTIVO);
            return toDTO(usuarioRepository.save(usuario));
        });
    }

    /**
     * Convierte la entidad MongoDB a DTO sin exponer el hash de la clave.
     *
     * @param usuario entidad de cuenta almacenada en MongoDB.
     * @return representacion segura del usuario para API REST o GraphQL.
     */
    private UsuarioDTO toDTO(UsuarioCuenta usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getCedula(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol() == null ? null : usuario.getRol().name(),
                usuario.getEstadoCuenta().name()
        );
    }
}

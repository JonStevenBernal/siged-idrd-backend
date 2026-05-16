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

    public UsuarioService(UsuarioCuentaRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream().map(this::toDTO).toList();
    }

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

    public AuthPayload autenticar(LoginInput input) {
        UsuarioCuenta usuario = usuarioRepository.findByCorreoIgnoreCase(input.correo())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales invalidas"));
        if (usuario.getEstadoCuenta() != EstadoCuenta.ACTIVO || !passwordEncoder.matches(input.password(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales invalidas");
        }
        return new AuthPayload(toDTO(usuario), "dev-token-" + UUID.randomUUID());
    }

    public Optional<UsuarioDTO> inactivarUsuario(String id) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setEstadoCuenta(EstadoCuenta.INACTIVO);
            return toDTO(usuarioRepository.save(usuario));
        });
    }

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

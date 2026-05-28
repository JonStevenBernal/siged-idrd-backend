package co.gov.idrd.siged.adapters.rest;

import co.gov.idrd.siged.application.dto.AuthPayload;
import co.gov.idrd.siged.application.dto.EquipoDTO;
import co.gov.idrd.siged.application.dto.EquipoInput;
import co.gov.idrd.siged.application.dto.JugadorDTO;
import co.gov.idrd.siged.application.dto.LoginInput;
import co.gov.idrd.siged.application.dto.PartidoDTO;
import co.gov.idrd.siged.application.dto.PartidoInput;
import co.gov.idrd.siged.application.dto.ResultadoPartidoInput;
import co.gov.idrd.siged.application.dto.TablaPosicionDTO;
import co.gov.idrd.siged.application.dto.TorneoDTO;
import co.gov.idrd.siged.application.dto.TorneoInput;
import co.gov.idrd.siged.application.dto.UsuarioDTO;
import co.gov.idrd.siged.application.dto.UsuarioInput;
import co.gov.idrd.siged.application.service.SigedService;
import co.gov.idrd.siged.application.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Adaptador REST principal de SIGED.
 * Expone endpoints HTTP y delega reglas de negocio a los servicios de aplicacion.
 */
@RestController
@RequestMapping("/api")
public class SigedRestController {

    private final SigedService sigedService;
    private final UsuarioService usuarioService;

    /**
     * Inyecta los casos de uso usados por los endpoints REST.
     *
     * @param sigedService servicio de aplicacion para torneos, equipos, jugadores y partidos.
     * @param usuarioService servicio de aplicacion para usuarios y autenticacion.
     */
    public SigedRestController(SigedService sigedService, UsuarioService usuarioService) {
        this.sigedService = sigedService;
        this.usuarioService = usuarioService;
    }

    /**
     * Atiende la consulta REST de todos los torneos registrados.
     *
     * @return lista de torneos disponibles en el sistema.
     */
    @GetMapping("/torneos")
    public List<TorneoDTO> listarTorneos() {
        return sigedService.listarTorneos();
    }

    /**
     * Atiende la consulta REST de torneos con inscripciones abiertas.
     *
     * @return lista de torneos que aceptan inscripciones.
     */
    @GetMapping("/torneos/abiertos")
    public List<TorneoDTO> listarTorneosAbiertos() {
        return sigedService.listarTorneosAbiertos();
    }

    /**
     * Busca un torneo por id desde REST.
     *
     * @param id identificador del torneo solicitado.
     * @return HTTP 200 con el torneo o HTTP 404 si no existe.
     */
    @GetMapping("/torneos/{id}")
    public ResponseEntity<TorneoDTO> buscarTorneo(@PathVariable Long id) {
        return sigedService.buscarTorneo(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Crea un torneo a partir del cuerpo JSON recibido.
     *
     * @param input datos validados del torneo a crear.
     * @return HTTP 201 con el torneo creado.
     */
    @PostMapping("/torneos")
    public ResponseEntity<TorneoDTO> crearTorneo(@Valid @RequestBody TorneoInput input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sigedService.crearTorneo(input));
    }

    /**
     * Actualiza los datos de un torneo existente.
     *
     * @param id identificador del torneo a modificar.
     * @param input nuevos datos del torneo.
     * @return HTTP 200 con el torneo actualizado o HTTP 404 si no existe.
     */
    @PutMapping("/torneos/{id}")
    public ResponseEntity<TorneoDTO> actualizarTorneo(
            @PathVariable Long id,
            @Valid @RequestBody TorneoInput input
    ) {
        return sigedService.actualizarTorneo(id, input)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Publica un torneo para dejarlo en estado de inscripciones abiertas.
     *
     * @param id identificador del torneo a publicar.
     * @return HTTP 200 con el torneo publicado o HTTP 404 si no existe.
     */
    @PostMapping("/torneos/{id}/publicar")
    public ResponseEntity<TorneoDTO> publicarTorneo(@PathVariable Long id) {
        return sigedService.publicarTorneo(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Registra una solicitud de inscripcion de equipo con jugadores.
     *
     * @param input datos del equipo, torneo y jugadores inscritos.
     * @return HTTP 201 con el resumen de la inscripcion creada.
     */
    @PostMapping("/equipos/inscripciones")
    public ResponseEntity<EquipoDTO> inscribirEquipo(@Valid @RequestBody EquipoInput input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sigedService.inscribirEquipo(input));
    }

    /**
     * Consulta equipos registrados en un torneo.
     *
     * @param torneoId identificador del torneo.
     * @return lista de equipos inscritos en el torneo.
     */
    @GetMapping("/torneos/{torneoId}/equipos")
    public List<EquipoDTO> equiposPorTorneo(@PathVariable Long torneoId) {
        return sigedService.equiposPorTorneo(torneoId);
    }

    /**
     * Cambia el estado administrativo de una inscripcion de equipo.
     *
     * @param equipoId identificador del equipo.
     * @param estado nuevo estado de inscripcion recibido como texto del enum.
     * @return HTTP 200 con el equipo actualizado o HTTP 404 si no existe.
     */
    @PatchMapping("/equipos/{equipoId}/estado")
    public ResponseEntity<EquipoDTO> cambiarEstadoInscripcion(
            @PathVariable Long equipoId,
            @RequestParam String estado
    ) {
        return sigedService.cambiarEstadoInscripcion(equipoId, estado)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Consulta jugadores de un equipo.
     *
     * @param equipoId identificador del equipo.
     * @return lista de jugadores registrados para el equipo.
     */
    @GetMapping("/equipos/{equipoId}/jugadores")
    public List<JugadorDTO> jugadoresPorEquipo(@PathVariable Long equipoId) {
        return sigedService.jugadoresPorEquipo(equipoId);
    }

    /**
     * Programa manualmente un partido entre dos equipos.
     *
     * @param input datos del torneo, equipos, escenario, fecha y arbitro.
     * @return HTTP 201 con el partido creado.
     */
    @PostMapping("/partidos")
    public ResponseEntity<PartidoDTO> crearPartido(@Valid @RequestBody PartidoInput input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sigedService.crearPartido(input));
    }

    /**
     * Genera fixture todos contra todos para los equipos aprobados de un torneo.
     *
     * @param torneoId identificador del torneo.
     * @return lista de partidos nuevos generados.
     */
    @PostMapping("/torneos/{torneoId}/fixture")
    public List<PartidoDTO> generarFixture(@PathVariable Long torneoId) {
        return sigedService.generarFixture(torneoId);
    }

    /**
     * Consulta partidos de un torneo.
     *
     * @param torneoId identificador del torneo.
     * @return lista de partidos ordenada por fecha e identificador.
     */
    @GetMapping("/torneos/{torneoId}/partidos")
    public List<PartidoDTO> partidosPorTorneo(@PathVariable Long torneoId) {
        return sigedService.partidosPorTorneo(torneoId);
    }

    /**
     * Consulta partidos asignados a un arbitro.
     *
     * @param arbitroDocumento documento del arbitro.
     * @return lista de partidos asignados al arbitro.
     */
    @GetMapping("/arbitros/{arbitroDocumento}/partidos")
    public List<PartidoDTO> partidosPorArbitro(@PathVariable String arbitroDocumento) {
        return sigedService.partidosPorArbitro(arbitroDocumento);
    }

    /**
     * Registra marcador y eventos de un partido.
     *
     * @param partidoId identificador del partido.
     * @param input marcador final y eventos del partido.
     * @return HTTP 200 con el partido finalizado o HTTP 404 si no existe.
     */
    @PostMapping("/partidos/{partidoId}/resultado")
    public ResponseEntity<PartidoDTO> registrarResultado(
            @PathVariable Long partidoId,
            @Valid @RequestBody ResultadoPartidoInput input
    ) {
        return sigedService.registrarResultado(partidoId, input)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Calcula la tabla de posiciones de un torneo.
     *
     * @param torneoId identificador del torneo.
     * @return posiciones calculadas con puntos, goles y diferencia.
     */
    @GetMapping("/torneos/{torneoId}/tabla")
    public List<TablaPosicionDTO> tablaPosiciones(@PathVariable Long torneoId) {
        return sigedService.tablaPosiciones(torneoId);
    }

    /**
     * Consulta cuentas de usuario registradas.
     *
     * @return lista de usuarios sin exponer hashes de clave.
     */
    @GetMapping("/usuarios")
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    /**
     * Crea una cuenta de usuario.
     *
     * @param input datos de identificacion, rol, correo y clave.
     * @return HTTP 201 con el usuario creado.
     */
    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioDTO> crearUsuario(@Valid @RequestBody UsuarioInput input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crearUsuario(input));
    }

    /**
     * Autentica credenciales de usuario.
     *
     * @param input correo y clave enviados por el cliente.
     * @return datos del usuario autenticado y token temporal.
     */
    @PostMapping("/auth/login")
    public AuthPayload login(@Valid @RequestBody LoginInput input) {
        return usuarioService.autenticar(input);
    }

    /**
     * Inactiva una cuenta de usuario.
     *
     * @param id identificador MongoDB de la cuenta.
     * @return HTTP 200 con el usuario inactivado o HTTP 404 si no existe.
     */
    @PatchMapping("/usuarios/{id}/inactivar")
    public ResponseEntity<UsuarioDTO> inactivarUsuario(@PathVariable String id) {
        return usuarioService.inactivarUsuario(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

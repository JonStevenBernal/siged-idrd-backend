package co.gov.idrd.siged.adapters.graphql;

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
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class SigedResolver {

    private final SigedService sigedService;
    private final UsuarioService usuarioService;

    /**
     * Inyecta los casos de uso consumidos por las operaciones GraphQL.
     *
     * @param sigedService servicio de aplicacion para torneos, equipos, jugadores y partidos.
     * @param usuarioService servicio de aplicacion para usuarios y autenticacion.
     */
    public SigedResolver(SigedService sigedService, UsuarioService usuarioService) {
        this.sigedService = sigedService;
        this.usuarioService = usuarioService;
    }

    /**
     * Resuelve la consulta GraphQL de todos los torneos.
     *
     * @return lista de torneos registrados.
     */
    @QueryMapping
    public List<TorneoDTO> torneos() {
        return sigedService.listarTorneos();
    }

    /**
     * Resuelve la consulta GraphQL de torneos con inscripciones abiertas.
     *
     * @return lista de torneos disponibles para inscripcion.
     */
    @QueryMapping
    public List<TorneoDTO> torneosAbiertos() {
        return sigedService.listarTorneosAbiertos();
    }

    /**
     * Resuelve la consulta GraphQL de torneo por id.
     *
     * @param id identificador del torneo solicitado.
     * @return torneo encontrado o {@code null} cuando no existe.
     */
    @QueryMapping
    public TorneoDTO torneo(@Argument Long id) {
        return sigedService.buscarTorneo(id).orElse(null);
    }

    /**
     * Resuelve la consulta GraphQL de equipos por torneo.
     *
     * @param torneoId identificador del torneo.
     * @return lista de equipos inscritos en el torneo.
     */
    @QueryMapping
    public List<EquipoDTO> equiposPorTorneo(@Argument Long torneoId) {
        return sigedService.equiposPorTorneo(torneoId);
    }

    /**
     * Resuelve la consulta GraphQL de jugadores por equipo.
     *
     * @param equipoId identificador del equipo.
     * @return lista de jugadores del equipo.
     */
    @QueryMapping
    public List<JugadorDTO> jugadoresPorEquipo(@Argument Long equipoId) {
        return sigedService.jugadoresPorEquipo(equipoId);
    }

    /**
     * Resuelve la consulta GraphQL de partidos por torneo.
     *
     * @param torneoId identificador del torneo.
     * @return lista de partidos ordenados por fecha e identificador.
     */
    @QueryMapping
    public List<PartidoDTO> partidosPorTorneo(@Argument Long torneoId) {
        return sigedService.partidosPorTorneo(torneoId);
    }

    /**
     * Resuelve la consulta GraphQL de partidos asignados a un arbitro.
     *
     * @param arbitroDocumento documento del arbitro.
     * @return lista de partidos asignados.
     */
    @QueryMapping
    public List<PartidoDTO> partidosPorArbitro(@Argument String arbitroDocumento) {
        return sigedService.partidosPorArbitro(arbitroDocumento);
    }

    /**
     * Resuelve la consulta GraphQL de tabla de posiciones.
     *
     * @param torneoId identificador del torneo.
     * @return posiciones calculadas del torneo.
     */
    @QueryMapping
    public List<TablaPosicionDTO> tablaPosiciones(@Argument Long torneoId) {
        return sigedService.tablaPosiciones(torneoId);
    }

    /**
     * Resuelve la consulta GraphQL de usuarios.
     *
     * @return lista de usuarios sin exponer hashes de clave.
     */
    @QueryMapping
    public List<UsuarioDTO> usuarios() {
        return usuarioService.listarUsuarios();
    }

    /**
     * Ejecuta la mutacion GraphQL para crear torneo.
     *
     * @param input datos del torneo a registrar.
     * @return torneo creado.
     */
    @MutationMapping
    public TorneoDTO crearTorneo(@Argument TorneoInput input) {
        return sigedService.crearTorneo(input);
    }

    /**
     * Ejecuta la mutacion GraphQL para actualizar torneo.
     *
     * @param id identificador del torneo.
     * @param input nuevos datos del torneo.
     * @return torneo actualizado o {@code null} si no existe.
     */
    @MutationMapping
    public TorneoDTO actualizarTorneo(@Argument Long id, @Argument TorneoInput input) {
        return sigedService.actualizarTorneo(id, input).orElse(null);
    }

    /**
     * Ejecuta la mutacion GraphQL para publicar un torneo.
     *
     * @param id identificador del torneo.
     * @return torneo publicado o {@code null} si no existe.
     */
    @MutationMapping
    public TorneoDTO publicarTorneo(@Argument Long id) {
        return sigedService.publicarTorneo(id).orElse(null);
    }

    /**
     * Ejecuta la mutacion GraphQL para inscribir un equipo.
     *
     * @param input datos del equipo y jugadores inscritos.
     * @return equipo inscrito.
     */
    @MutationMapping
    public EquipoDTO inscribirEquipo(@Argument EquipoInput input) {
        return sigedService.inscribirEquipo(input);
    }

    /**
     * Ejecuta la mutacion GraphQL para cambiar estado de inscripcion.
     *
     * @param equipoId identificador del equipo.
     * @param estado nuevo estado de inscripcion.
     * @return equipo actualizado o {@code null} si no existe.
     */
    @MutationMapping
    public EquipoDTO cambiarEstadoInscripcion(@Argument Long equipoId, @Argument String estado) {
        return sigedService.cambiarEstadoInscripcion(equipoId, estado).orElse(null);
    }

    /**
     * Ejecuta la mutacion GraphQL para crear un partido.
     *
     * @param input datos de programacion del partido.
     * @return partido creado.
     */
    @MutationMapping
    public PartidoDTO crearPartido(@Argument PartidoInput input) {
        return sigedService.crearPartido(input);
    }

    /**
     * Ejecuta la mutacion GraphQL para generar fixture.
     *
     * @param torneoId identificador del torneo.
     * @return partidos nuevos generados.
     */
    @MutationMapping
    public List<PartidoDTO> generarFixture(@Argument Long torneoId) {
        return sigedService.generarFixture(torneoId);
    }

    /**
     * Ejecuta la mutacion GraphQL para registrar resultado de partido.
     *
     * @param partidoId identificador del partido.
     * @param input marcador final y eventos.
     * @return partido finalizado o {@code null} si no existe.
     */
    @MutationMapping
    public PartidoDTO registrarResultado(@Argument Long partidoId, @Argument ResultadoPartidoInput input) {
        return sigedService.registrarResultado(partidoId, input).orElse(null);
    }

    /**
     * Ejecuta la mutacion GraphQL para crear usuario.
     *
     * @param input datos de identificacion, rol, correo y clave.
     * @return usuario creado.
     */
    @MutationMapping
    public UsuarioDTO crearUsuario(@Argument UsuarioInput input) {
        return usuarioService.crearUsuario(input);
    }

    /**
     * Ejecuta la mutacion GraphQL de autenticacion.
     *
     * @param input correo y clave del usuario.
     * @return payload de autenticacion con usuario y token temporal.
     */
    @MutationMapping
    public AuthPayload login(@Argument LoginInput input) {
        return usuarioService.autenticar(input);
    }

    /**
     * Ejecuta la mutacion GraphQL para inactivar usuario.
     *
     * @param id identificador MongoDB del usuario.
     * @return usuario inactivado o {@code null} si no existe.
     */
    @MutationMapping
    public UsuarioDTO inactivarUsuario(@Argument String id) {
        return usuarioService.inactivarUsuario(id).orElse(null);
    }
}

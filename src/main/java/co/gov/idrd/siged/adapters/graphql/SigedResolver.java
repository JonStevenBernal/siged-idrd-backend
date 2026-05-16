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

    public SigedResolver(SigedService sigedService, UsuarioService usuarioService) {
        this.sigedService = sigedService;
        this.usuarioService = usuarioService;
    }

    @QueryMapping
    public List<TorneoDTO> torneos() {
        return sigedService.listarTorneos();
    }

    @QueryMapping
    public List<TorneoDTO> torneosAbiertos() {
        return sigedService.listarTorneosAbiertos();
    }

    @QueryMapping
    public TorneoDTO torneo(@Argument Long id) {
        return sigedService.buscarTorneo(id).orElse(null);
    }

    @QueryMapping
    public List<EquipoDTO> equiposPorTorneo(@Argument Long torneoId) {
        return sigedService.equiposPorTorneo(torneoId);
    }

    @QueryMapping
    public List<JugadorDTO> jugadoresPorEquipo(@Argument Long equipoId) {
        return sigedService.jugadoresPorEquipo(equipoId);
    }

    @QueryMapping
    public List<PartidoDTO> partidosPorTorneo(@Argument Long torneoId) {
        return sigedService.partidosPorTorneo(torneoId);
    }

    @QueryMapping
    public List<PartidoDTO> partidosPorArbitro(@Argument String arbitroDocumento) {
        return sigedService.partidosPorArbitro(arbitroDocumento);
    }

    @QueryMapping
    public List<TablaPosicionDTO> tablaPosiciones(@Argument Long torneoId) {
        return sigedService.tablaPosiciones(torneoId);
    }

    @QueryMapping
    public List<UsuarioDTO> usuarios() {
        return usuarioService.listarUsuarios();
    }

    @MutationMapping
    public TorneoDTO crearTorneo(@Argument TorneoInput input) {
        return sigedService.crearTorneo(input);
    }

    @MutationMapping
    public TorneoDTO actualizarTorneo(@Argument Long id, @Argument TorneoInput input) {
        return sigedService.actualizarTorneo(id, input).orElse(null);
    }

    @MutationMapping
    public TorneoDTO publicarTorneo(@Argument Long id) {
        return sigedService.publicarTorneo(id).orElse(null);
    }

    @MutationMapping
    public EquipoDTO inscribirEquipo(@Argument EquipoInput input) {
        return sigedService.inscribirEquipo(input);
    }

    @MutationMapping
    public EquipoDTO cambiarEstadoInscripcion(@Argument Long equipoId, @Argument String estado) {
        return sigedService.cambiarEstadoInscripcion(equipoId, estado).orElse(null);
    }

    @MutationMapping
    public PartidoDTO crearPartido(@Argument PartidoInput input) {
        return sigedService.crearPartido(input);
    }

    @MutationMapping
    public List<PartidoDTO> generarFixture(@Argument Long torneoId) {
        return sigedService.generarFixture(torneoId);
    }

    @MutationMapping
    public PartidoDTO registrarResultado(@Argument Long partidoId, @Argument ResultadoPartidoInput input) {
        return sigedService.registrarResultado(partidoId, input).orElse(null);
    }

    @MutationMapping
    public UsuarioDTO crearUsuario(@Argument UsuarioInput input) {
        return usuarioService.crearUsuario(input);
    }

    @MutationMapping
    public AuthPayload login(@Argument LoginInput input) {
        return usuarioService.autenticar(input);
    }

    @MutationMapping
    public UsuarioDTO inactivarUsuario(@Argument String id) {
        return usuarioService.inactivarUsuario(id).orElse(null);
    }
}

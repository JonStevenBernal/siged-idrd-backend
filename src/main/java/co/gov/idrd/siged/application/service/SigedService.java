package co.gov.idrd.siged.application.service;

import co.gov.idrd.siged.application.dto.EquipoDTO;
import co.gov.idrd.siged.application.dto.EquipoInput;
import co.gov.idrd.siged.application.dto.JugadorDTO;
import co.gov.idrd.siged.application.dto.JugadorInput;
import co.gov.idrd.siged.application.dto.PartidoDTO;
import co.gov.idrd.siged.application.dto.PartidoInput;
import co.gov.idrd.siged.application.dto.ResultadoPartidoInput;
import co.gov.idrd.siged.application.dto.TablaPosicionDTO;
import co.gov.idrd.siged.application.dto.TorneoDTO;
import co.gov.idrd.siged.application.dto.TorneoInput;
import co.gov.idrd.siged.domain.model.Equipo;
import co.gov.idrd.siged.domain.model.EstadoInscripcion;
import co.gov.idrd.siged.domain.model.EstadoPartido;
import co.gov.idrd.siged.domain.model.EstadoTorneo;
import co.gov.idrd.siged.domain.model.Jugador;
import co.gov.idrd.siged.domain.model.Partido;
import co.gov.idrd.siged.domain.model.Torneo;
import co.gov.idrd.siged.domain.repository.EquipoRepository;
import co.gov.idrd.siged.domain.repository.JugadorRepository;
import co.gov.idrd.siged.domain.repository.PartidoRepository;
import co.gov.idrd.siged.domain.repository.TorneoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class SigedService {

    private final TorneoRepository torneoRepository;
    private final EquipoRepository equipoRepository;
    private final JugadorRepository jugadorRepository;
    private final PartidoRepository partidoRepository;

    public SigedService(
            TorneoRepository torneoRepository,
            EquipoRepository equipoRepository,
            JugadorRepository jugadorRepository,
            PartidoRepository partidoRepository
    ) {
        this.torneoRepository = torneoRepository;
        this.equipoRepository = equipoRepository;
        this.jugadorRepository = jugadorRepository;
        this.partidoRepository = partidoRepository;
    }

    @Transactional(readOnly = true)
    public List<TorneoDTO> listarTorneos() {
        return torneoRepository.findAll().stream().map(this::toTorneoDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<TorneoDTO> listarTorneosAbiertos() {
        return torneoRepository.findByEstado(EstadoTorneo.INSCRIPCIONES_ABIERTAS).stream()
                .map(this::toTorneoDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<TorneoDTO> buscarTorneo(Long id) {
        return torneoRepository.findById(id).map(this::toTorneoDTO);
    }

    public TorneoDTO crearTorneo(TorneoInput input) {
        validarNombreObligatorio(input.nombre(), "El nombre del torneo es obligatorio");
        validarNombreObligatorio(input.deporte(), "El deporte es obligatorio");
        validarReglamentoPdf(input.reglamentoPdfUrl());

        if (torneoRepository.existsByNombreIgnoreCase(input.nombre())) {
            throw new IllegalArgumentException("Ya existe un torneo con este nombre");
        }

        Torneo torneo = new Torneo();
        aplicarDatosTorneo(torneo, input);
        torneo.setEstado(EstadoTorneo.INSCRIPCIONES_ABIERTAS);

        return toTorneoDTO(torneoRepository.save(torneo));
    }

    public Optional<TorneoDTO> actualizarTorneo(Long id, TorneoInput input) {
        validarReglamentoPdf(input.reglamentoPdfUrl());
        return torneoRepository.findById(id).map(torneo -> {
            aplicarDatosTorneo(torneo, input);
            return toTorneoDTO(torneoRepository.save(torneo));
        });
    }

    public Optional<TorneoDTO> publicarTorneo(Long id) {
        return torneoRepository.findById(id).map(torneo -> {
            torneo.setEstado(EstadoTorneo.INSCRIPCIONES_ABIERTAS);
            return toTorneoDTO(torneoRepository.save(torneo));
        });
    }

    public EquipoDTO inscribirEquipo(EquipoInput input) {
        Torneo torneo = torneoRepository.findById(input.torneoId())
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));

        validarInscripcionesAbiertas(torneo);
        validarNombreObligatorio(input.nombre(), "El nombre del equipo es obligatorio");

        if (equipoRepository.existsByTorneoIdAndNombreIgnoreCase(torneo.getId(), input.nombre())) {
            throw new IllegalArgumentException("Ya existe un equipo con este nombre en el torneo");
        }

        List<JugadorInput> jugadores = input.jugadores() == null ? List.of() : input.jugadores();
        if (jugadores.isEmpty()) {
            throw new IllegalArgumentException("Campos obligatorios incompletos");
        }

        validarJugadoresUnicos(torneo.getId(), jugadores);

        Equipo equipo = new Equipo();
        equipo.setTorneo(torneo);
        equipo.setNombre(input.nombre());
        equipo.setCategoria(input.categoria());
        equipo.setComprobantePago(input.comprobantePago());
        equipo.setDelegadoDocumento(input.delegadoDocumento());
        equipo.setEstadoInscripcion(EstadoInscripcion.PENDIENTE_VALIDACION);

        jugadores.forEach(jugadorInput -> {
            Jugador jugador = new Jugador();
            jugador.setEquipo(equipo);
            jugador.setDocumentoIdentidad(jugadorInput.documentoIdentidad());
            jugador.setNombres(jugadorInput.nombres());
            jugador.setFechaNacimiento(parseDate(jugadorInput.fechaNacimiento()));
            jugador.setFotoUrl(jugadorInput.fotoUrl());
            equipo.getJugadores().add(jugador);
        });

        return toEquipoDTO(equipoRepository.save(equipo));
    }

    @Transactional(readOnly = true)
    public List<EquipoDTO> equiposPorTorneo(Long torneoId) {
        return equipoRepository.findByTorneoId(torneoId).stream().map(this::toEquipoDTO).toList();
    }

    public Optional<EquipoDTO> cambiarEstadoInscripcion(Long equipoId, String estado) {
        EstadoInscripcion nuevoEstado = EstadoInscripcion.valueOf(estado);
        return equipoRepository.findById(equipoId).map(equipo -> {
            equipo.setEstadoInscripcion(nuevoEstado);
            return toEquipoDTO(equipoRepository.save(equipo));
        });
    }

    @Transactional(readOnly = true)
    public List<JugadorDTO> jugadoresPorEquipo(Long equipoId) {
        return jugadorRepository.findByEquipoId(equipoId).stream().map(this::toJugadorDTO).toList();
    }

    public PartidoDTO crearPartido(PartidoInput input) {
        Torneo torneo = torneoRepository.findById(input.torneoId())
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));
        Equipo local = equipoRepository.findById(input.equipoLocalId())
                .orElseThrow(() -> new IllegalArgumentException("Equipo local no encontrado"));
        Equipo visitante = equipoRepository.findById(input.equipoVisitanteId())
                .orElseThrow(() -> new IllegalArgumentException("Equipo visitante no encontrado"));

        validarEquiposDelTorneo(torneo.getId(), local, visitante);

        Partido partido = new Partido();
        partido.setTorneo(torneo);
        partido.setEquipoLocal(local);
        partido.setEquipoVisitante(visitante);
        partido.setFechaProgramada(parseDateTime(input.fechaProgramada()));
        partido.setEscenario(input.escenario());
        partido.setArbitroDocumento(input.arbitroDocumento());
        partido.setEstado(EstadoPartido.PROGRAMADO);

        return toPartidoDTO(partidoRepository.save(partido));
    }

    public List<PartidoDTO> generarFixture(Long torneoId) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));
        List<Equipo> equipos = equipoRepository.findByTorneoId(torneoId).stream()
                .filter(equipo -> equipo.getEstadoInscripcion() == EstadoInscripcion.APROBADA)
                .toList();

        Set<String> partidosExistentes = new HashSet<>();
        partidoRepository.findByTorneoIdOrderByFechaProgramadaAscIdAsc(torneoId)
                .forEach(partido -> partidosExistentes.add(pairKey(partido.getEquipoLocal().getId(), partido.getEquipoVisitante().getId())));

        List<Partido> nuevos = new ArrayList<>();
        for (int i = 0; i < equipos.size(); i++) {
            for (int j = i + 1; j < equipos.size(); j++) {
                Equipo local = equipos.get(i);
                Equipo visitante = equipos.get(j);
                if (partidosExistentes.contains(pairKey(local.getId(), visitante.getId()))) {
                    continue;
                }
                Partido partido = new Partido();
                partido.setTorneo(torneo);
                partido.setEquipoLocal(local);
                partido.setEquipoVisitante(visitante);
                partido.setEstado(EstadoPartido.PROGRAMADO);
                nuevos.add(partido);
            }
        }

        return partidoRepository.saveAll(nuevos).stream().map(this::toPartidoDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<PartidoDTO> partidosPorTorneo(Long torneoId) {
        return partidoRepository.findByTorneoIdOrderByFechaProgramadaAscIdAsc(torneoId).stream()
                .map(this::toPartidoDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PartidoDTO> partidosPorArbitro(String arbitroDocumento) {
        return partidoRepository.findByArbitroDocumentoOrderByFechaProgramadaAsc(arbitroDocumento).stream()
                .map(this::toPartidoDTO)
                .toList();
    }

    public Optional<PartidoDTO> registrarResultado(Long partidoId, ResultadoPartidoInput input) {
        validarMarcador(input);
        return partidoRepository.findById(partidoId).map(partido -> {
            if (partido.getEstado() == EstadoPartido.FINALIZADO) {
                throw new IllegalArgumentException("Este partido ya tiene un resultado registrado");
            }
            partido.setGolesLocal(input.golesLocal());
            partido.setGolesVisitante(input.golesVisitante());
            partido.setEventos(input.eventos());
            partido.setEstado(EstadoPartido.FINALIZADO);
            return toPartidoDTO(partidoRepository.save(partido));
        });
    }

    @Transactional(readOnly = true)
    public List<TablaPosicionDTO> tablaPosiciones(Long torneoId) {
        Map<Long, EstadisticaEquipo> estadisticas = new HashMap<>();
        equipoRepository.findByTorneoId(torneoId)
                .forEach(equipo -> estadisticas.put(equipo.getId(), new EstadisticaEquipo(equipo.getId(), equipo.getNombre())));

        partidoRepository.findByTorneoIdOrderByFechaProgramadaAscIdAsc(torneoId).stream()
                .filter(partido -> partido.getEstado() == EstadoPartido.FINALIZADO)
                .forEach(partido -> aplicarResultado(estadisticas, partido));

        return estadisticas.values().stream()
                .sorted(Comparator.comparingInt(EstadisticaEquipo::puntos).reversed()
                        .thenComparing(Comparator.comparingInt(EstadisticaEquipo::diferenciaGol).reversed())
                        .thenComparing(Comparator.comparingInt(EstadisticaEquipo::golesFavor).reversed())
                        .thenComparing(EstadisticaEquipo::equipo))
                .map(EstadisticaEquipo::toDTO)
                .toList();
    }

    private void aplicarDatosTorneo(Torneo torneo, TorneoInput input) {
        torneo.setNombre(input.nombre());
        torneo.setDeporte(input.deporte());
        torneo.setFechaInicio(parseDate(input.fechaInicio()));
        torneo.setFechaFin(parseDate(input.fechaFin()));
        torneo.setFechaInicioInscripcion(parseDate(input.fechaInicioInscripcion()));
        torneo.setFechaFinInscripcion(parseDate(input.fechaFinInscripcion()));
        torneo.setFormatoCompeticion(input.formatoCompeticion());
        torneo.setCategorias(input.categorias());
        torneo.setReglasPuntuacion(input.reglasPuntuacion());
        torneo.setReglamentoPdfUrl(input.reglamentoPdfUrl());
    }

    private void validarInscripcionesAbiertas(Torneo torneo) {
        if (torneo.getEstado() != EstadoTorneo.INSCRIPCIONES_ABIERTAS) {
            throw new IllegalArgumentException("Las inscripciones para este torneo estan cerradas");
        }
        LocalDate hoy = LocalDate.now();
        if (torneo.getFechaInicioInscripcion() != null && hoy.isBefore(torneo.getFechaInicioInscripcion())) {
            throw new IllegalArgumentException("Las inscripciones para este torneo estan cerradas");
        }
        if (torneo.getFechaFinInscripcion() != null && hoy.isAfter(torneo.getFechaFinInscripcion())) {
            throw new IllegalArgumentException("Las inscripciones para este torneo estan cerradas");
        }
    }

    private void validarJugadoresUnicos(Long torneoId, List<JugadorInput> jugadores) {
        Set<String> documentos = new HashSet<>();
        for (JugadorInput jugador : jugadores) {
            validarNombreObligatorio(jugador.documentoIdentidad(), "El documento del jugador es obligatorio");
            validarNombreObligatorio(jugador.nombres(), "Los nombres del jugador son obligatorios");
            if (!documentos.add(jugador.documentoIdentidad())) {
                throw new IllegalArgumentException("El jugador esta duplicado en la inscripcion");
            }
            if (jugadorRepository.existsByDocumentoInTorneo(jugador.documentoIdentidad(), torneoId)) {
                throw new IllegalArgumentException("El jugador ya esta registrado en otro equipo");
            }
        }
    }

    private void validarEquiposDelTorneo(Long torneoId, Equipo local, Equipo visitante) {
        if (local.getId().equals(visitante.getId())) {
            throw new IllegalArgumentException("El equipo local y visitante deben ser diferentes");
        }
        if (!local.getTorneo().getId().equals(torneoId) || !visitante.getTorneo().getId().equals(torneoId)) {
            throw new IllegalArgumentException("Los equipos deben pertenecer al torneo indicado");
        }
    }

    private void validarMarcador(ResultadoPartidoInput input) {
        if (input.golesLocal() == null || input.golesVisitante() == null) {
            throw new IllegalArgumentException("Debe ingresar un marcador para finalizar");
        }
        if (input.golesLocal() < 0 || input.golesVisitante() < 0) {
            throw new IllegalArgumentException("Valor de marcador no valido");
        }
    }

    private void validarReglamentoPdf(String reglamentoPdfUrl) {
        if (reglamentoPdfUrl != null && !reglamentoPdfUrl.isBlank()
                && !reglamentoPdfUrl.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Formato de archivo no valido. Solo se permite PDF");
        }
    }

    private void validarNombreObligatorio(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    private String pairKey(Long equipoUno, Long equipoDos) {
        Long menor = Math.min(equipoUno, equipoDos);
        Long mayor = Math.max(equipoUno, equipoDos);
        return menor + "-" + mayor;
    }

    private LocalDate parseDate(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }

    private LocalDateTime parseDateTime(String value) {
        return value == null || value.isBlank() ? null : LocalDateTime.parse(value);
    }

    private String format(LocalDate value) {
        return value == null ? null : value.toString();
    }

    private String format(LocalDateTime value) {
        return value == null ? null : value.toString();
    }

    private TorneoDTO toTorneoDTO(Torneo torneo) {
        return new TorneoDTO(
                torneo.getId(),
                torneo.getNombre(),
                torneo.getDeporte(),
                format(torneo.getFechaInicio()),
                format(torneo.getFechaFin()),
                format(torneo.getFechaInicioInscripcion()),
                format(torneo.getFechaFinInscripcion()),
                torneo.getFormatoCompeticion(),
                torneo.getCategorias(),
                torneo.getReglasPuntuacion(),
                torneo.getReglamentoPdfUrl(),
                torneo.getEstado().name()
        );
    }

    private EquipoDTO toEquipoDTO(Equipo equipo) {
        return new EquipoDTO(
                equipo.getId(),
                equipo.getTorneo().getId(),
                equipo.getNombre(),
                equipo.getCategoria(),
                equipo.getComprobantePago(),
                equipo.getDelegadoDocumento(),
                equipo.getEstadoInscripcion().name(),
                equipo.getJugadores().size()
        );
    }

    private JugadorDTO toJugadorDTO(Jugador jugador) {
        return new JugadorDTO(
                jugador.getId(),
                jugador.getEquipo().getId(),
                jugador.getEquipo().getTorneo().getId(),
                jugador.getDocumentoIdentidad(),
                jugador.getNombres(),
                format(jugador.getFechaNacimiento()),
                jugador.getFotoUrl()
        );
    }

    private PartidoDTO toPartidoDTO(Partido partido) {
        return new PartidoDTO(
                partido.getId(),
                partido.getTorneo().getId(),
                partido.getEquipoLocal().getId(),
                partido.getEquipoLocal().getNombre(),
                partido.getEquipoVisitante().getId(),
                partido.getEquipoVisitante().getNombre(),
                partido.getGolesLocal(),
                partido.getGolesVisitante(),
                format(partido.getFechaProgramada()),
                partido.getEscenario(),
                partido.getArbitroDocumento(),
                partido.getEventos(),
                partido.getEstado().name()
        );
    }

    private void aplicarResultado(Map<Long, EstadisticaEquipo> estadisticas, Partido partido) {
        EstadisticaEquipo local = estadisticas.get(partido.getEquipoLocal().getId());
        EstadisticaEquipo visitante = estadisticas.get(partido.getEquipoVisitante().getId());
        if (local == null || visitante == null) {
            return;
        }

        local.registrar(partido.getGolesLocal(), partido.getGolesVisitante());
        visitante.registrar(partido.getGolesVisitante(), partido.getGolesLocal());
    }

    private static final class EstadisticaEquipo {
        private final Long equipoId;
        private final String equipo;
        private int partidosJugados;
        private int ganados;
        private int empatados;
        private int perdidos;
        private int golesFavor;
        private int golesContra;

        private EstadisticaEquipo(Long equipoId, String equipo) {
            this.equipoId = equipoId;
            this.equipo = equipo;
        }

        private void registrar(int golesPropios, int golesRival) {
            partidosJugados++;
            golesFavor += golesPropios;
            golesContra += golesRival;
            if (golesPropios > golesRival) {
                ganados++;
            } else if (golesPropios == golesRival) {
                empatados++;
            } else {
                perdidos++;
            }
        }

        private int puntos() {
            return ganados * 3 + empatados;
        }

        private int diferenciaGol() {
            return golesFavor - golesContra;
        }

        private int golesFavor() {
            return golesFavor;
        }

        private String equipo() {
            return equipo;
        }

        private TablaPosicionDTO toDTO() {
            return new TablaPosicionDTO(
                    equipoId,
                    equipo,
                    partidosJugados,
                    ganados,
                    empatados,
                    perdidos,
                    golesFavor,
                    golesContra,
                    diferenciaGol(),
                    puntos()
            );
        }
    }
}

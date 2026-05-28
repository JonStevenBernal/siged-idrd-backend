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

/**
 * Servicio de aplicacion para la gestion deportiva de SIGED.
 * Orquesta reglas de negocio de torneos, equipos, jugadores, partidos y tablas.
 */
@Service
@Transactional
public class SigedService {

    private final TorneoRepository torneoRepository;
    private final EquipoRepository equipoRepository;
    private final JugadorRepository jugadorRepository;
    private final PartidoRepository partidoRepository;

    /**
     * Recibe los repositorios necesarios para operar torneos, equipos, jugadores y partidos.
     *
     * @param torneoRepository repositorio JPA de torneos.
     * @param equipoRepository repositorio JPA de equipos.
     * @param jugadorRepository repositorio JPA de jugadores.
     * @param partidoRepository repositorio JPA de partidos.
     */
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

    /**
     * Lista todos los torneos registrados.
     *
     * @return torneos existentes convertidos a DTO de salida.
     */
    @Transactional(readOnly = true)
    public List<TorneoDTO> listarTorneos() {
        return torneoRepository.findAll().stream().map(this::toTorneoDTO).toList();
    }

    /**
     * Lista los torneos que tienen inscripciones abiertas.
     *
     * @return torneos con estado {@link EstadoTorneo#INSCRIPCIONES_ABIERTAS}.
     */
    @Transactional(readOnly = true)
    public List<TorneoDTO> listarTorneosAbiertos() {
        return torneoRepository.findByEstado(EstadoTorneo.INSCRIPCIONES_ABIERTAS).stream()
                .map(this::toTorneoDTO)
                .toList();
    }

    /**
     * Busca un torneo por identificador y lo retorna como DTO.
     *
     * @param id identificador del torneo.
     * @return torneo encontrado o {@link Optional#empty()} cuando no existe.
     */
    @Transactional(readOnly = true)
    public Optional<TorneoDTO> buscarTorneo(Long id) {
        return torneoRepository.findById(id).map(this::toTorneoDTO);
    }

    /**
     * Crea un torneo validando nombre, deporte, PDF y duplicidad.
     *
     * @param input datos funcionales del torneo a registrar.
     * @return torneo creado con estado de inscripciones abiertas.
     * @throws IllegalArgumentException si faltan campos obligatorios, el PDF no es valido o el nombre ya existe.
     */
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

    /**
     * Actualiza los datos principales de un torneo existente.
     *
     * @param id identificador del torneo a actualizar.
     * @param input datos que reemplazan la informacion editable del torneo.
     * @return torneo actualizado o {@link Optional#empty()} cuando no existe.
     * @throws IllegalArgumentException si la URL de reglamento no apunta a un PDF.
     */
    public Optional<TorneoDTO> actualizarTorneo(Long id, TorneoInput input) {
        validarReglamentoPdf(input.reglamentoPdfUrl());
        return torneoRepository.findById(id).map(torneo -> {
            aplicarDatosTorneo(torneo, input);
            return toTorneoDTO(torneoRepository.save(torneo));
        });
    }

    /**
     * Cambia un torneo a estado de inscripciones abiertas.
     *
     * @param id identificador del torneo a publicar.
     * @return torneo publicado o {@link Optional#empty()} cuando no existe.
     */
    public Optional<TorneoDTO> publicarTorneo(Long id) {
        return torneoRepository.findById(id).map(torneo -> {
            torneo.setEstado(EstadoTorneo.INSCRIPCIONES_ABIERTAS);
            return toTorneoDTO(torneoRepository.save(torneo));
        });
    }

    /**
     * Inscribe un equipo con sus jugadores dentro de un torneo abierto.
     *
     * @param input datos del torneo, equipo, delegado, soporte de pago y jugadores.
     * @return equipo inscrito en estado pendiente de validacion.
     * @throws IllegalArgumentException si el torneo no existe, esta cerrado, el equipo se duplica o los jugadores son invalidos.
     */
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

    /**
     * Lista los equipos asociados a un torneo.
     *
     * @param torneoId identificador del torneo.
     * @return equipos registrados en el torneo indicado.
     */
    @Transactional(readOnly = true)
    public List<EquipoDTO> equiposPorTorneo(Long torneoId) {
        return equipoRepository.findByTorneoId(torneoId).stream().map(this::toEquipoDTO).toList();
    }

    /**
     * Actualiza el estado de inscripcion de un equipo.
     *
     * @param equipoId identificador del equipo.
     * @param estado nombre del enum {@link EstadoInscripcion} que se aplicara.
     * @return equipo actualizado o {@link Optional#empty()} si no existe.
     * @throws IllegalArgumentException si el texto del estado no corresponde al enum.
     */
    public Optional<EquipoDTO> cambiarEstadoInscripcion(Long equipoId, String estado) {
        EstadoInscripcion nuevoEstado = EstadoInscripcion.valueOf(estado);
        return equipoRepository.findById(equipoId).map(equipo -> {
            equipo.setEstadoInscripcion(nuevoEstado);
            return toEquipoDTO(equipoRepository.save(equipo));
        });
    }

    /**
     * Lista los jugadores registrados en un equipo.
     *
     * @param equipoId identificador del equipo.
     * @return jugadores vinculados al equipo indicado.
     */
    @Transactional(readOnly = true)
    public List<JugadorDTO> jugadoresPorEquipo(Long equipoId) {
        return jugadorRepository.findByEquipoId(equipoId).stream().map(this::toJugadorDTO).toList();
    }

    /**
     * Programa un partido entre dos equipos del mismo torneo.
     *
     * @param input datos de torneo, equipos, fecha, escenario y arbitro.
     * @return partido creado en estado programado.
     * @throws IllegalArgumentException si el torneo o los equipos no existen, son iguales o no pertenecen al torneo.
     */
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

    /**
     * Genera los partidos faltantes todos contra todos para equipos aprobados.
     *
     * @param torneoId identificador del torneo.
     * @return partidos nuevos creados; no incluye cruces que ya existian.
     * @throws IllegalArgumentException si el torneo no existe.
     */
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

    /**
     * Lista los partidos de un torneo ordenados por fecha e identificador.
     *
     * @param torneoId identificador del torneo.
     * @return partidos del torneo ordenados de forma estable.
     */
    @Transactional(readOnly = true)
    public List<PartidoDTO> partidosPorTorneo(Long torneoId) {
        return partidoRepository.findByTorneoIdOrderByFechaProgramadaAscIdAsc(torneoId).stream()
                .map(this::toPartidoDTO)
                .toList();
    }

    /**
     * Lista los partidos asignados a un arbitro.
     *
     * @param arbitroDocumento documento del arbitro asignado.
     * @return partidos programados para el arbitro indicado.
     */
    @Transactional(readOnly = true)
    public List<PartidoDTO> partidosPorArbitro(String arbitroDocumento) {
        return partidoRepository.findByArbitroDocumentoOrderByFechaProgramadaAsc(arbitroDocumento).stream()
                .map(this::toPartidoDTO)
                .toList();
    }

    /**
     * Registra el marcador final y eventos de un partido no finalizado.
     *
     * @param partidoId identificador del partido.
     * @param input marcador final y eventos relevantes del partido.
     * @return partido finalizado o {@link Optional#empty()} si no existe.
     * @throws IllegalArgumentException si el marcador es invalido o el partido ya estaba finalizado.
     */
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

    /**
     * Calcula la tabla de posiciones del torneo con partidos finalizados.
     *
     * @param torneoId identificador del torneo.
     * @return tabla ordenada por puntos, diferencia de gol, goles a favor y nombre.
     */
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

    /**
     * Copia los datos del input sobre la entidad Torneo.
     *
     * @param torneo entidad destino que sera modificada.
     * @param input datos recibidos desde REST o GraphQL.
     */
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

    /**
     * Verifica estado y fechas para permitir inscripciones en el torneo.
     *
     * @param torneo torneo sobre el cual se intenta inscribir un equipo.
     * @throws IllegalArgumentException si el torneo no esta abierto o esta fuera de fechas de inscripcion.
     */
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

    /**
     * Valida datos obligatorios y evita jugadores repetidos en un torneo.
     *
     * @param torneoId identificador del torneo donde se valida la unicidad.
     * @param jugadores jugadores enviados en la solicitud de inscripcion.
     * @throws IllegalArgumentException si hay documentos vacios, nombres vacios, duplicados locales o registros previos.
     */
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

    /**
     * Verifica que los equipos sean distintos y pertenezcan al torneo indicado.
     *
     * @param torneoId identificador del torneo del partido.
     * @param local equipo local propuesto.
     * @param visitante equipo visitante propuesto.
     * @throws IllegalArgumentException si los equipos son iguales o pertenecen a otro torneo.
     */
    private void validarEquiposDelTorneo(Long torneoId, Equipo local, Equipo visitante) {
        if (local.getId().equals(visitante.getId())) {
            throw new IllegalArgumentException("El equipo local y visitante deben ser diferentes");
        }
        if (!local.getTorneo().getId().equals(torneoId) || !visitante.getTorneo().getId().equals(torneoId)) {
            throw new IllegalArgumentException("Los equipos deben pertenecer al torneo indicado");
        }
    }

    /**
     * Valida que el marcador exista y no tenga valores negativos.
     *
     * @param input marcador recibido para cerrar el partido.
     * @throws IllegalArgumentException si faltan goles o algun valor es negativo.
     */
    private void validarMarcador(ResultadoPartidoInput input) {
        if (input.golesLocal() == null || input.golesVisitante() == null) {
            throw new IllegalArgumentException("Debe ingresar un marcador para finalizar");
        }
        if (input.golesLocal() < 0 || input.golesVisitante() < 0) {
            throw new IllegalArgumentException("Valor de marcador no valido");
        }
    }

    /**
     * Permite solamente URLs de reglamento con extension PDF.
     *
     * @param reglamentoPdfUrl URL o ruta del archivo de reglamento.
     * @throws IllegalArgumentException si el valor no esta vacio y no termina en {@code .pdf}.
     */
    private void validarReglamentoPdf(String reglamentoPdfUrl) {
        if (reglamentoPdfUrl != null && !reglamentoPdfUrl.isBlank()
                && !reglamentoPdfUrl.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Formato de archivo no valido. Solo se permite PDF");
        }
    }

    /**
     * Valida que un texto obligatorio no llegue nulo ni vacio.
     *
     * @param valor texto a validar.
     * @param mensaje mensaje de error funcional si el valor es invalido.
     * @throws IllegalArgumentException si el texto es nulo o esta en blanco.
     */
    private void validarNombreObligatorio(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    /**
     * Crea una clave unica para una pareja de equipos sin importar el orden.
     *
     * @param equipoUno identificador del primer equipo.
     * @param equipoDos identificador del segundo equipo.
     * @return clave normalizada con el id menor primero.
     */
    private String pairKey(Long equipoUno, Long equipoDos) {
        Long menor = Math.min(equipoUno, equipoDos);
        Long mayor = Math.max(equipoUno, equipoDos);
        return menor + "-" + mayor;
    }

    /**
     * Convierte un texto ISO-8601 a fecha o retorna null si viene vacio.
     *
     * @param value texto con formato {@code yyyy-MM-dd}.
     * @return fecha parseada o {@code null} si el valor llega vacio.
     */
    private LocalDate parseDate(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }

    /**
     * Convierte un texto ISO-8601 a fecha y hora o retorna null si viene vacio.
     *
     * @param value texto con formato ISO-8601 de fecha y hora.
     * @return fecha y hora parseada o {@code null} si el valor llega vacio.
     */
    private LocalDateTime parseDateTime(String value) {
        return value == null || value.isBlank() ? null : LocalDateTime.parse(value);
    }

    /**
     * Formatea una fecha para exponerla en los DTOs.
     *
     * @param value fecha de dominio.
     * @return texto ISO-8601 o {@code null} si no hay fecha.
     */
    private String format(LocalDate value) {
        return value == null ? null : value.toString();
    }

    /**
     * Formatea una fecha y hora para exponerla en los DTOs.
     *
     * @param value fecha y hora de dominio.
     * @return texto ISO-8601 o {@code null} si no hay valor.
     */
    private String format(LocalDateTime value) {
        return value == null ? null : value.toString();
    }

    /**
     * Convierte la entidad Torneo a DTO de salida.
     *
     * @param torneo entidad JPA de torneo.
     * @return DTO consumible por REST y GraphQL.
     */
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

    /**
     * Convierte la entidad Equipo a DTO de salida.
     *
     * @param equipo entidad JPA de equipo.
     * @return DTO consumible por REST y GraphQL.
     */
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

    /**
     * Convierte la entidad Jugador a DTO de salida.
     *
     * @param jugador entidad JPA de jugador.
     * @return DTO consumible por REST y GraphQL.
     */
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

    /**
     * Convierte la entidad Partido a DTO de salida.
     *
     * @param partido entidad JPA de partido.
     * @return DTO consumible por REST y GraphQL.
     */
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

    /**
     * Acumula el resultado de un partido en las estadisticas de ambos equipos.
     *
     * @param estadisticas mapa mutable de estadisticas por identificador de equipo.
     * @param partido partido finalizado que aporta goles y resultado.
     */
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

        /**
         * Inicializa el acumulador de estadisticas para un equipo.
         *
         * @param equipoId identificador del equipo.
         * @param equipo nombre del equipo para salida y ordenamiento.
         */
        private EstadisticaEquipo(Long equipoId, String equipo) {
            this.equipoId = equipoId;
            this.equipo = equipo;
        }

        /**
         * Registra goles, partido jugado y resultado competitivo.
         *
         * @param golesPropios goles marcados por el equipo.
         * @param golesRival goles marcados por el rival.
         */
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

        /**
         * Calcula los puntos con regla de 3 por victoria y 1 por empate.
         *
         * @return puntos acumulados del equipo.
         */
        private int puntos() {
            return ganados * 3 + empatados;
        }

        /**
         * Calcula la diferencia entre goles a favor y goles en contra.
         *
         * @return diferencia de gol acumulada.
         */
        private int diferenciaGol() {
            return golesFavor - golesContra;
        }

        /**
         * Retorna goles a favor para usarlo como criterio de desempate.
         *
         * @return goles a favor acumulados.
         */
        private int golesFavor() {
            return golesFavor;
        }

        /**
         * Retorna el nombre del equipo para ordenar alfabeticamente.
         *
         * @return nombre del equipo.
         */
        private String equipo() {
            return equipo;
        }

        /**
         * Convierte las estadisticas acumuladas a DTO de tabla de posiciones.
         *
         * @return DTO con partidos, resultados, goles, diferencia y puntos.
         */
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

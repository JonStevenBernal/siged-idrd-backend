package co.gov.idrd.siged.domain.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Documento MongoDB que representa una cuenta de usuario del sistema.
 */
@Document(collection = "usuarios")
public class UsuarioCuenta {

    @Id
    private String id;

    @NotBlank(message = "La cedula es obligatoria")
    @Indexed(unique = true)
    private String cedula;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Email(message = "El correo no es valido")
    @NotBlank(message = "El correo es obligatorio")
    @Indexed(unique = true)
    private String correo;

    @NotBlank(message = "La clave es obligatoria")
    private String passwordHash;

    private RolUsuario rol;

    private EstadoCuenta estadoCuenta = EstadoCuenta.ACTIVO;

    private Instant creadoEn = Instant.now();

    /**
     * Obtiene el identificador MongoDB de la cuenta.
     *
     * @return id del documento de usuario.
     */
    public String getId() {
        return id;
    }

    /**
     * Asigna el identificador MongoDB de la cuenta.
     *
     * @param id id del documento de usuario.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene la cedula unica del usuario.
     *
     * @return numero de cedula registrado.
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * Asigna la cedula unica del usuario.
     *
     * @param cedula numero de identificacion usado para evitar duplicados.
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    /**
     * Obtiene el nombre completo del usuario.
     *
     * @return nombre registrado.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna el nombre completo del usuario.
     *
     * @param nombre nombre requerido para identificar la cuenta.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el correo electronico unico del usuario.
     *
     * @return correo usado para autenticacion.
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Asigna el correo electronico unico del usuario.
     *
     * @param correo correo usado para autenticacion y contacto.
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Obtiene el hash de la clave del usuario.
     *
     * @return clave cifrada; nunca debe exponerse por API.
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Asigna el hash de la clave del usuario.
     *
     * @param passwordHash clave previamente cifrada con el codificador configurado.
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Obtiene el rol funcional del usuario.
     *
     * @return rol usado para autorizacion funcional.
     */
    public RolUsuario getRol() {
        return rol;
    }

    /**
     * Asigna el rol funcional del usuario.
     *
     * @param rol rol usado para autorizacion funcional.
     */
    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    /**
     * Obtiene el estado de la cuenta.
     *
     * @return estado activo o inactivo.
     */
    public EstadoCuenta getEstadoCuenta() {
        return estadoCuenta;
    }

    /**
     * Asigna el estado de la cuenta.
     *
     * @param estadoCuenta nuevo estado operativo de la cuenta.
     */
    public void setEstadoCuenta(EstadoCuenta estadoCuenta) {
        this.estadoCuenta = estadoCuenta;
    }

    /**
     * Obtiene la fecha tecnica de creacion de la cuenta.
     *
     * @return instante en que se creo el documento.
     */
    public Instant getCreadoEn() {
        return creadoEn;
    }

    /**
     * Asigna la fecha tecnica de creacion de la cuenta.
     *
     * @param creadoEn instante de creacion del documento.
     */
    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }
}

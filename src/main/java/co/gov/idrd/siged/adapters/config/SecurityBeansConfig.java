package co.gov.idrd.siged.adapters.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityBeansConfig {

    /**
     * Expone el codificador de claves usado por el servicio de usuarios.
     * BCrypt agrega sal por clave y evita persistir contrasenas en texto plano.
     *
     * @return implementacion BCrypt de {@link PasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

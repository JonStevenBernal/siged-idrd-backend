package co.gov.idrd.siged;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SigedIdrdApplication {

    /**
     * Punto de entrada de Spring Boot.
     *
     * @param args argumentos recibidos por linea de comandos al iniciar la aplicacion.
     */
    public static void main(String[] args) {
        SpringApplication.run(SigedIdrdApplication.class, args);
    }
}

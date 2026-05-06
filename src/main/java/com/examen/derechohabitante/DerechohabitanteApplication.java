package com.examen.derechohabitante;

import com.examen.derechohabitante.exception.BatchProcessException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class DerechohabitanteApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DerechohabitanteApplication.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importUserJob;

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(DerechohabitanteApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        logPerfilesSpring();

        if (args.length >= 2) {
            log.info("### PARÁMETROS RECIBIDOS POR LÍNEA DE COMANDOS ###");
            log.info("Parámetro 1 (Ejecutor): {}", args[0]);
            log.info("Parámetro 2 (Entorno): {}", args[1]);

            try {
                JobParameters params = new JobParametersBuilder()
                        .addString("ejecutor", args[0])
                        .addString("entorno", args[1])
                        .addLong("inicio", System.currentTimeMillis())
                        .toJobParameters();

                jobLauncher.run(importUserJob, params);
            } catch (JobExecutionException e) {
                throw new BatchProcessException("Fallo ejecutando el Job de importación (importUserJob)", e);
            }
        } else {
            log.error("**********************************************************");
            log.error("Faltan parámetros de ejecución.");
            log.error("Uso esperado: java -jar app.jar PARAM_LOG_1 PARAM_LOG_2");
            log.error("**********************************************************");
        }
    }

    private void logPerfilesSpring() {
        String[] activos = environment.getActiveProfiles();
        String[] def = environment.getDefaultProfiles();
        if (activos.length > 0) {
            log.info(
                    "Perfil(es) Spring Boot activo(s): {} — se cargan los application-<perfil>.yml correspondientes (DEV/QA/Prod).",
                    Arrays.toString(activos));
        } else {
            log.info(
                    "Perfil(es) Spring Boot: ninguno explícito (--spring.profiles.active); perfiles por defecto: {}",
                    Arrays.toString(def));
        }
    }
}

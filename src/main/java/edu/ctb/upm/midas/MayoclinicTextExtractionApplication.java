package edu.ctb.upm.midas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MayoclinicTextExtractionApplication {

    public static void main(String[] args) {
        SpringApplication.run(MayoclinicTextExtractionApplication.class, args);
    }
}

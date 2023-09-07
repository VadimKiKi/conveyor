package ru.taratonov.conveyor;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Conveyor REST Api",
                description = "1 MVP level conveyor",
                version = "1.0.0",
                contact = @Contact(
                        name = "Taratonov Vadim",
                        email = "taratonovv8@bk.ru",
                        url = "https://github.com/VadimTaratonov/conveyor"
                )
        )
)
public class ConveyorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConveyorApplication.class, args);
    }

}

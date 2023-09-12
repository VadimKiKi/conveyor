package ru.taratonov.conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorDTO {
    @Schema(
            description = "error message",
            name = "msg",
            example = "term must be greater or equal than 6")
    private String msg;

    @Schema(
            description = "time of error",
            name = "errorTime",
            example = "2023-08-10T12:31:43.1545756")
    private LocalDateTime errorTime;

    @Schema(
            description = "http status of error",
            name = "httpStatus",
            example = "BAD_REQUEST")
    private HttpStatus httpStatus;
}

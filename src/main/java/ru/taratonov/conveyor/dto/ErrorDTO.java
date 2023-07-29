package ru.taratonov.conveyor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorDTO {
    private String msg;
    private LocalDateTime errorTime;
    private HttpStatus httpStatus;
}

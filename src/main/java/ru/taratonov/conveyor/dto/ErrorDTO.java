package ru.taratonov.conveyor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class ErrorDTO {
    private String msg;
    private LocalDateTime errorTime;
    private HttpStatus httpStatus;
}

package ru.taratonov.conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class LoanOfferDTO {
    @Schema(
            description = "id of application",
            name = "applicationId",
            example = "165464")
    @NotNull(message = "must not be empty")
    @Min(value = 1, message = "must be greater or equal than 1")
    private Long applicationId;

    @Schema(
            description = "the amount requested by the client",
            name = "requestedAmount",
            example = "10000")
    @NotNull(message = "must not be empty")
    @DecimalMin(value = "10000.0", message = "must be greater or equal than 10000")
    private BigDecimal requestedAmount;

    @Schema(
            description = "total amount of the loan",
            name = "totalAmount",
            example = "10000")
    @NotNull(message = "must not be empty")
    @DecimalMin(value = "10000.0", message = "must be greater or equal than 10000")
    private BigDecimal totalAmount;

    @Schema(
            description = "loan term",
            name = "term",
            example = "4")
    @NotNull(message = "must not be empty")
    @Min(value = 6, message = "must be greater or equal than 6")
    private Integer term;

    @Schema(
            description = "monthly payment of the loan",
            name = "monthlyPayment",
            example = "1245.67")
    @NotNull(message = "must not be empty")
    @DecimalMin(value = "1", message = "must be greater or equal than 1")
    private BigDecimal monthlyPayment;

    @Schema(
            description = "loan rate",
            name = "rate",
            example = "3")
    @NotNull(message = "must not be empty")
    @DecimalMin(value = "0.1", message = "must be greater or equal than 0.1")
    private BigDecimal rate;

    @Schema(
            description = "availability of credit insurance",
            name = "isInsuranceEnabled",
            example = "false")
    @NotNull(message = "must not be empty")
    private Boolean isInsuranceEnabled;

    @Schema(
            description = "salary client",
            name = "isSalaryClient",
            example = "true")
    @NotNull(message = "must not be empty")
    private Boolean isSalaryClient;
}

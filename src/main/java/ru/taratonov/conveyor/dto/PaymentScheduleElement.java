package ru.taratonov.conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PaymentScheduleElement {
    @Schema(
            description = "payment number",
            name = "number",
            example = "1")
    private Integer number;

    @Schema(
            description = "payment date",
            name = "date",
            example = "2023-12-11")
    private LocalDate date;

    @Schema(
            description = "the full amount of the monthly payment",
            name = "totalPayment",
            example = "11000")
    private BigDecimal totalPayment;

    @Schema(
            description = "the amount of the interest payment",
            name = "interestPayment",
            example = "1200")
    private BigDecimal interestPayment;

    @Schema(
            description = "the amount to repay the loan body",
            name = "debtPayment",
            example = "5785")
    private BigDecimal debtPayment;

    @Schema(
            description = "remaining debt",
            name = "remainingDebt",
            example = "235367")
    private BigDecimal remainingDebt;
}

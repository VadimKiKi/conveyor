package ru.taratonov.conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class CreditDTO {
    @Schema(
            description = "amount of the loan",
            name = "amount",
            example = "10000")
    private BigDecimal amount;
    @Schema(
            description = "loan term",
            name = "term",
            example = "7")
    private Integer term;

    @Schema(
            description = "monthly payment of the loan",
            name = "monthlyPayment",
            example = "1245.67")
    private BigDecimal monthlyPayment;

    @Schema(
            description = "loan rate",
            name = "rate",
            example = "3")
    private BigDecimal rate;

    @Schema(
            description = "full cost of the loan",
            name = "psk",
            example = "2.56")
    private BigDecimal psk;

    @Schema(
            description = "availability of credit insurance",
            name = "isInsuranceEnabled",
            example = "false")
    private Boolean isInsuranceEnabled;

    @Schema(
            description = "salary client",
            name = "isSalaryClient",
            example = "true")
    private Boolean isSalaryClient;

    @Schema(
            description = "list of payments",
            name = "paymentSchedule")
    private List<PaymentScheduleElement> paymentSchedule;
}

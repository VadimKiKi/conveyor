package ru.taratonov.conveyor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentScheduleElement {
    // Номер платежа
    private Integer number;
    // Дата платежа
    private LocalDate date;
    // Полная сумма месячного платежа
    private BigDecimal totalPayment;
    // Сумма платежа в погашение процентов
    private BigDecimal interestPayment;
    // Сумма в погашение тела кредита
    private BigDecimal debtPayment;
    // Остаток долга
    private BigDecimal remainingDebt;
}

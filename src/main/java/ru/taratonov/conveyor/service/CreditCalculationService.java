package ru.taratonov.conveyor.service;

import org.springframework.stereotype.Service;
import ru.taratonov.conveyor.dto.CreditDTO;
import ru.taratonov.conveyor.dto.PaymentScheduleElement;
import ru.taratonov.conveyor.dto.ScoringDataDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class CreditCalculationService {

    private final Calendar calendar = Calendar.getInstance();
    private final BigDecimal INSURANCE_PERCENTAGE = BigDecimal.valueOf(10);

    // Вычисление необходимых полей для заявки клиента
    public CreditDTO calculateLoanParameters(ScoringDataDTO scoringData, BigDecimal newRate) {
        BigDecimal amount = calculateTotalAmount(scoringData.getAmount(),scoringData.getIsInsuranceEnabled());

        Integer term = scoringData.getTerm();
        List<PaymentScheduleElement> paymentSchedule = calculatePaymentSchedule(amount, term, newRate);

        CreditDTO credit = new CreditDTO(
                amount,
                term,
                calculateMonthlyPayment(amount, newRate, term),
                newRate,
                calculatePSK(amount, term, paymentSchedule),
                scoringData.getIsInsuranceEnabled(),
                scoringData.getIsSalaryClient(),
                paymentSchedule
        );

        return credit;
    }

    // Вычисление ежемесячного платежа
    public BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal rate, Integer term) {

        // Месячная процентная ставка
        BigDecimal monthlyRate = rate
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.CEILING)
                .divide(BigDecimal.valueOf(12), 6, RoundingMode.CEILING);

        // Промежуточное вычисление скобки в выражении
        BigDecimal annuityCalculationBracket = (BigDecimal.ONE.add(monthlyRate))
                .pow(term)
                .setScale(6, RoundingMode.CEILING);

        // Коэффициент аннуитета
        BigDecimal annuityRatio = (annuityCalculationBracket.multiply(monthlyRate))
                .divide(annuityCalculationBracket.subtract(BigDecimal.ONE), 6, RoundingMode.CEILING);

        // Ежемесячный платеж
        BigDecimal monthlyPayment = amount.multiply(annuityRatio).setScale(2, RoundingMode.CEILING);

        return monthlyPayment;
    }

    // Вычисление графиков платежей
    public List<PaymentScheduleElement> calculatePaymentSchedule(BigDecimal amount, Integer term, BigDecimal rate) {


        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();

        LocalDate paymentDate = LocalDate.now();;
        BigDecimal remainingDebt = amount;
        BigDecimal monthlyPayment = calculateMonthlyPayment(amount, rate, term);

        for (int i = 1; i < term + 1; i++) {

            paymentDate = paymentDate.plusMonths(1);
            calendar.set(paymentDate.getYear(), paymentDate.getMonthValue() - 2, 1);
            int numOfDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int numOfDaysInYear = Year.of(paymentDate.getYear()).length();

            BigDecimal interestPayment = calculateInterestPayment(remainingDebt, rate, numOfDaysInMonth, numOfDaysInYear);
            BigDecimal debtPayment = calculateDebtPayment(monthlyPayment, interestPayment);
            remainingDebt = calculateRemainingDebt(remainingDebt, debtPayment);

            paymentSchedule.add(
                    new PaymentScheduleElement(i, paymentDate, monthlyPayment, interestPayment, debtPayment, remainingDebt));
        }

        return paymentSchedule;
    }

    // Вычисление ПСК
    public BigDecimal calculatePSK(BigDecimal amount, Integer term, List<PaymentScheduleElement> scheduleElements) {
        BigDecimal yearTerm = BigDecimal.valueOf(term).divide(BigDecimal.valueOf(12), 2, RoundingMode.CEILING);

        BigDecimal totalAmount = scheduleElements.stream()
                .map(PaymentScheduleElement::getTotalPayment)
                .reduce((x, y) -> x.add(y))
                .get();

        BigDecimal psk = totalAmount
                .divide(amount, 6, RoundingMode.CEILING)
                .subtract(BigDecimal.ONE)
                .divide(yearTerm, 6, RoundingMode.CEILING)
                .multiply(BigDecimal.valueOf(100))
                .setScale(3, RoundingMode.CEILING);

        return psk;
    }

    // Вычисление полной стоимости кредита с учетом страховки
    public BigDecimal calculateTotalAmount(BigDecimal amount, Boolean IsInsuranceEnabled){
        if (IsInsuranceEnabled) {
            amount = amount
                    .add(amount.multiply(INSURANCE_PERCENTAGE.divide(BigDecimal.valueOf(100), 2, RoundingMode.CEILING)));
        }
        return amount;
    }

    // вычисление процентов по платежу
    private BigDecimal calculateInterestPayment(BigDecimal remainingDebt, BigDecimal rate, Integer numOfDaysInMonth,
                                                Integer numOfDaysInYear) {
        BigDecimal rateValue = rate.divide(BigDecimal.valueOf(100), 6, RoundingMode.CEILING);

        BigDecimal interestPayment = remainingDebt
                .multiply(rateValue)
                .multiply(BigDecimal.valueOf(numOfDaysInMonth))
                .divide(BigDecimal.valueOf(numOfDaysInYear), 2, RoundingMode.CEILING);

        return interestPayment;
    }

    // Вычисление суммы тела кредита
    private BigDecimal calculateDebtPayment(BigDecimal totalPayment, BigDecimal interestPayment) {
        return totalPayment.subtract(interestPayment).setScale(2, RoundingMode.CEILING);
    }

    // Вычисление остаточной суммы долга
    private BigDecimal calculateRemainingDebt(BigDecimal amount, BigDecimal debtPayment) {
        return amount.subtract(debtPayment).setScale(2, RoundingMode.CEILING);
    }
}

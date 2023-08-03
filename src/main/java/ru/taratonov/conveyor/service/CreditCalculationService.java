package ru.taratonov.conveyor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

@Slf4j
@Service
public class CreditCalculationService {
    private final Calendar calendar = Calendar.getInstance();
    @Value("${insurance.percentage}")
    private BigDecimal INSURANCE_PERCENTAGE;

    // Вычисление необходимых полей для заявки клиента
    public CreditDTO calculateLoanParameters(ScoringDataDTO scoringData, BigDecimal newRate) {

        if (newRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid value");
        }

        BigDecimal amount = calculateTotalAmount(scoringData.getAmount(), scoringData.getIsInsuranceEnabled());

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

    /**
     * ЕП = СК * КА
     * ЕП - ежемесячный платеж
     * СК - сумма кредита
     * КА - коэффициент аннуитета
     * КА = (МПС * (1 + МПС)^КП)/((1 + МПС)^КП - 1)
     * МПС - месячная процентная ставка
     * КП - количество платежей
     */
    public BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal rate, Integer term) {
        log.debug("!START CALCULATE MONTHLY PAYMENT WITH AMOUNT - {}, RATE - {}, TERM - {}!", amount, rate, term);

        if (amount.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(BigDecimal.ZERO) < 0 || term < 0) {
            throw new IllegalArgumentException("Invalid value");
        }

        // Месячная процентная ставка
        BigDecimal monthlyRate = rate
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.CEILING)
                .divide(BigDecimal.valueOf(12), 6, RoundingMode.CEILING);
        log.debug("Monthly rate is {}", monthlyRate);

        // Промежуточное вычисление скобки в выражении
        BigDecimal annuityCalculationBracket = (BigDecimal.ONE.add(monthlyRate))
                .pow(term)
                .setScale(6, RoundingMode.CEILING);

        // Коэффициент аннуитета
        BigDecimal annuityRatio = (annuityCalculationBracket.multiply(monthlyRate))
                .divide(annuityCalculationBracket.subtract(BigDecimal.ONE), 6, RoundingMode.CEILING);
        log.debug("Annuity ration is {}", annuityRatio);

        // Ежемесячный платеж
        BigDecimal monthlyPayment = amount.multiply(annuityRatio).setScale(2, RoundingMode.CEILING);
        log.debug("!FINISH CALCULATE MONTHLY PAYMENT. RESULT IS {}!", monthlyPayment);

        return monthlyPayment;
    }

    // Вычисление графиков платежей
    public List<PaymentScheduleElement> calculatePaymentSchedule(BigDecimal amount, Integer term, BigDecimal rate) {
        log.debug("!START CALCULATE PAYMENT SCHEDULE WITH AMOUNT - {}, TERM - {},  RATE - {}!", amount, term, rate);

        if (amount.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(BigDecimal.ZERO) < 0 || term < 0) {
            throw new IllegalArgumentException("Invalid value");
        }

        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();

        LocalDate paymentDate = LocalDate.now();

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
            log.info("{} payment will be made on {}, monthly payment - {}, interest payment - {}, debt payment - {}, remaining debt - {}",
                    i, paymentDate, monthlyPayment, interestPayment, debtPayment, remainingDebt);
        }
        log.debug("!FINISH CALCULATE PAYMENT SCHEDULE!");
        return paymentSchedule;
    }

    // Вычисление ПСК

    /**
     * ПСК = (СП / СК - 1) / N
     * ПСК - полная стоимость кредита
     * СП - сумма платежей
     * СК - сумма кредита
     * N - продолжительность кредита в годах
     */
    public BigDecimal calculatePSK(BigDecimal amount, Integer term, List<PaymentScheduleElement> scheduleElements) {
        log.debug("!START CALCULATE PSK WITH AMOUNT - {},TERM - {} AND PAYMENT SCHEDULE!", amount, term);
        BigDecimal yearTerm = BigDecimal.valueOf(term).divide(BigDecimal.valueOf(12), 2, RoundingMode.CEILING);


        BigDecimal totalAmount = scheduleElements.stream()
                .map(PaymentScheduleElement::getTotalPayment)
                .reduce((x, y) -> x.add(y))
                .get();
        log.debug("total amount for paying loan is {}", totalAmount);

        BigDecimal psk = totalAmount
                .divide(amount, 6, RoundingMode.CEILING)
                .subtract(BigDecimal.ONE)
                .divide(yearTerm, 6, RoundingMode.CEILING)
                .multiply(BigDecimal.valueOf(100))
                .setScale(3, RoundingMode.CEILING);
        log.debug("!FINISH CALCULATE PSK.");
        log.info("Result psk  is {}!", psk);

        return psk;
    }

    // Вычисление полной стоимости кредита с учетом страховки

    /**
     * ПС = СК + СС
     * ПС - полная стоимость
     * СК - сумма кредита
     * СС - сумма страховки
     */
    public BigDecimal calculateTotalAmount(BigDecimal amount, Boolean isInsuranceEnabled) {
        log.debug("!START CALCULATE BASE TOTAL AMOUNT WITH AMOUNT - {}, isInsuranceEnabled - {}!", amount, isInsuranceEnabled);

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid value");
        }

        if (isInsuranceEnabled) {
            amount = amount
                    .add(amount.multiply(INSURANCE_PERCENTAGE.divide(BigDecimal.valueOf(100), 2, RoundingMode.CEILING)));
        }
        log.debug("!FINISH CALCULATE BASE TOTAL AMOUNT.");
        log.info("Total amount is {}!", amount);
        return amount;
    }

    // вычисление процентов по платежу

    /**
     * ПК = ОД * ПС * КДМ * КДГ
     * ПК - проценты по кредиту
     * ОД - остаток долга
     * ПС - процентная ставка
     * КДМ - количество дней в месяце
     * КДГ - количество дней в году
     */
    private BigDecimal calculateInterestPayment(BigDecimal remainingDebt, BigDecimal rate, Integer numOfDaysInMonth,
                                                Integer numOfDaysInYear) {
        log.debug("!START CALCULATE INTEREST PAYMENT WITH remainingDebt - {}, rate - {}, numOfDaysInMonth - {}, " +
                "numOfDaysInYear - {}!", remainingDebt, rate, numOfDaysInMonth, numOfDaysInYear);

        if (remainingDebt.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(BigDecimal.ZERO) < 0 || numOfDaysInMonth < 27
                || numOfDaysInMonth > 31 || numOfDaysInYear < 365 || numOfDaysInYear > 366) {
            throw new IllegalArgumentException("Invalid value");
        }

        BigDecimal rateValue = rate.divide(BigDecimal.valueOf(100), 6, RoundingMode.CEILING);

        BigDecimal interestPayment = remainingDebt
                .multiply(rateValue)
                .multiply(BigDecimal.valueOf(numOfDaysInMonth))
                .divide(BigDecimal.valueOf(numOfDaysInYear), 2, RoundingMode.CEILING);
        log.debug("!FINISH CALCULATE INTEREST PAYMENT. RESULT IS {}!", interestPayment);

        return interestPayment;
    }

    // Вычисление суммы тела кредита

    /**
     * ТК = ЕП - ПК
     * ТК - тело кредита
     * ЕП - ежемесячный платеж
     * ПК - проценты по кредиту
     */
    private BigDecimal calculateDebtPayment(BigDecimal totalPayment, BigDecimal interestPayment) {
        log.debug("!START CALCULATE DEBT PAYMENT WITH totalPayment - {}, interestPayment - {}!",
                totalPayment, interestPayment);

        if (totalPayment.compareTo(BigDecimal.ZERO) < 0 || interestPayment.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid value");
        }

        BigDecimal debtPayment = totalPayment.subtract(interestPayment).setScale(2, RoundingMode.CEILING);
        log.debug("!FINISH CALCULATE DEBT PAYMENT. RESULT IS {}!", debtPayment);

        return debtPayment;
    }

    // Вычисление остаточной суммы долга

    /**
     * ОСД = СК - ТК
     * ОСД - остаточная сумма долга
     * СК - сумма кредита, который осталось выплатить
     * ТК - тело кредита
     */
    private BigDecimal calculateRemainingDebt(BigDecimal amount, BigDecimal debtPayment) {
        log.debug("!START CALCULATE REMAINING DEBT WITH amount - {}, debtPayment - {}!",
                amount, debtPayment);

        if (amount.compareTo(BigDecimal.ZERO) < 0 || debtPayment.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid value");
        }

        BigDecimal remainingDebt = amount.subtract(debtPayment).setScale(2, RoundingMode.CEILING);
        log.debug("!FINISH CALCULATE REMAINING DEBT. RESULT IS {}!", remainingDebt);

        return remainingDebt;
    }
}

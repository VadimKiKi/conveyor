package ru.taratonov.conveyor.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.taratonov.conveyor.dto.CreditDTO;
import ru.taratonov.conveyor.dto.PaymentScheduleElement;
import ru.taratonov.conveyor.dto.ScoringDataDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class CreditCalculationServiceTest {

    @InjectMocks
    @Autowired
    private CreditCalculationService creditCalculationService;

    // tests for calculate loan parameters
    @Test
    void calculateLoanParameters_LessThanZeroParameter() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            BigDecimal rate = BigDecimal.valueOf(-1);
            creditCalculationService.calculateLoanParameters(new ScoringDataDTO(), rate);
        });
        assertEquals("Invalid value", exception.getMessage());
    }

    @Test
    void calculateLoanParameters_CheckParametersInCredit() {
        ScoringDataDTO scoringData = new ScoringDataDTO();
        scoringData.setAmount(BigDecimal.valueOf(10000));
        scoringData.setTerm(1);
        scoringData.setIsInsuranceEnabled(false);
        scoringData.setIsSalaryClient(true);
        BigDecimal newRate = BigDecimal.valueOf(5);

        CreditDTO creditDTO = creditCalculationService.calculateLoanParameters(scoringData, newRate);

        assertEquals(scoringData.getTerm(), creditDTO.getTerm());
        assertEquals(scoringData.getIsInsuranceEnabled(), creditDTO.getIsInsuranceEnabled());
        assertEquals(scoringData.getIsSalaryClient(), creditDTO.getIsSalaryClient());
        assertEquals(scoringData.getAmount(), creditDTO.getAmount());
    }

    // tests for calculate monthly payment
    @Test
    void calculateMonthlyPayment_LessThanZeroParameter() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            BigDecimal amount = BigDecimal.valueOf(-1).setScale(2, RoundingMode.CEILING);
            BigDecimal rate = BigDecimal.valueOf(5);
            Integer term = 6;
            creditCalculationService.calculateMonthlyPayment(amount, rate, term);
        });
        assertEquals("Invalid value", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            BigDecimal amount = BigDecimal.valueOf(10000).setScale(2, RoundingMode.CEILING);
            BigDecimal rate = BigDecimal.valueOf(-1);
            Integer term = 6;
            creditCalculationService.calculateMonthlyPayment(amount, rate, term);
        });
        assertEquals("Invalid value", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            BigDecimal amount = BigDecimal.valueOf(10000).setScale(2, RoundingMode.CEILING);
            BigDecimal rate = BigDecimal.valueOf(5);
            Integer term = -1;
            creditCalculationService.calculateMonthlyPayment(amount, rate, term);
        });
        assertEquals("Invalid value", exception.getMessage());

    }

    @Test
    void calculateMonthlyPayment_ZeroAmount() {
        BigDecimal amount = BigDecimal.valueOf(0).setScale(2, RoundingMode.CEILING);
        BigDecimal rate = BigDecimal.valueOf(5);
        Integer term = 6;

        BigDecimal result = creditCalculationService.calculateMonthlyPayment(amount, rate, term);

        BigDecimal expected = BigDecimal.valueOf(0).setScale(2, RoundingMode.CEILING);

        Assertions.assertEquals(result, expected);
    }

    @Test
    void calculateMonthlyPayment_NotZeroAmount() {
        BigDecimal amount = BigDecimal.valueOf(10000).setScale(2, RoundingMode.CEILING);
        BigDecimal rate = BigDecimal.valueOf(5);
        Integer term = 6;

        BigDecimal result = creditCalculationService.calculateMonthlyPayment(amount, rate, term);

        BigDecimal expected = BigDecimal.valueOf(1691.06).setScale(2, RoundingMode.CEILING);

        Assertions.assertEquals(result, expected);
    }

    // tests for calculate payment schedule
    @Test
    void calculatePaymentSchedule_LessThanZeroParameter() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            BigDecimal amount = BigDecimal.valueOf(-1).setScale(2, RoundingMode.CEILING);
            BigDecimal rate = BigDecimal.valueOf(5);
            Integer term = 6;
            creditCalculationService.calculatePaymentSchedule(amount, term, rate);
        });
        assertEquals("Invalid value", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            BigDecimal amount = BigDecimal.valueOf(10000).setScale(2, RoundingMode.CEILING);
            BigDecimal rate = BigDecimal.valueOf(-1);
            Integer term = 6;
            creditCalculationService.calculatePaymentSchedule(amount, term, rate);
        });
        assertEquals("Invalid value", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            BigDecimal amount = BigDecimal.valueOf(10000).setScale(2, RoundingMode.CEILING);
            BigDecimal rate = BigDecimal.valueOf(5);
            Integer term = -1;
            creditCalculationService.calculatePaymentSchedule(amount, term, rate);
        });
        assertEquals("Invalid value", exception.getMessage());
    }

    @Test
    void calculatePaymentSchedule_CheckSizeOfList() {
        BigDecimal amount = BigDecimal.valueOf(10000).setScale(2, RoundingMode.CEILING);
        BigDecimal rate = BigDecimal.valueOf(5);
        Integer term = 6;

        Integer result = creditCalculationService.calculatePaymentSchedule(amount, term, rate).size();

        assertEquals(result, term);
    }

    @Test
    void calculatePaymentSchedule_CheckCalculationsOfFirstPaymentSchedule() {
        BigDecimal amount = BigDecimal.valueOf(10000).setScale(2, RoundingMode.CEILING);
        BigDecimal rate = BigDecimal.valueOf(5);
        Integer term = 6;

        List<PaymentScheduleElement> paymentScheduleElements = creditCalculationService.calculatePaymentSchedule(amount, term, rate);

        PaymentScheduleElement paymentScheduleElement = paymentScheduleElements.get(0);

        BigDecimal interestPaymentExpected = BigDecimal.valueOf(41.10).setScale(2, RoundingMode.CEILING);
        BigDecimal debtPaymentExpected = BigDecimal.valueOf(1649.96).setScale(2, RoundingMode.CEILING);
        BigDecimal remainingDebtExpected = BigDecimal.valueOf(8350.04).setScale(2, RoundingMode.CEILING);

        assertEquals(paymentScheduleElement.getInterestPayment(), interestPaymentExpected);
        assertEquals(paymentScheduleElement.getDebtPayment(), debtPaymentExpected);
        assertEquals(paymentScheduleElement.getRemainingDebt(), remainingDebtExpected);
    }

    // Tests for calculate total amount
    @Test
    void calculateTotalAmount_IsInsuranceEnabled() {
        boolean isInsuranceEnabled = true;
        BigDecimal amount = BigDecimal.valueOf(1000);

        BigDecimal result = creditCalculationService.calculateTotalAmount(amount, isInsuranceEnabled);

        BigDecimal expected = BigDecimal.valueOf(1100).setScale(2, RoundingMode.CEILING);

        Assertions.assertEquals(result, expected);
    }

    @Test
    void calculateTotalAmount_NotIsInsuranceEnabled() {
        boolean isInsuranceEnabled = false;
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);

        BigDecimal result = creditCalculationService.calculateTotalAmount(amount, isInsuranceEnabled);

        BigDecimal expected = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);

        Assertions.assertEquals(result, expected);
    }

    @Test
    void calculateTotalAmount_LessThanZeroAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            BigDecimal amount = BigDecimal.valueOf(-1).setScale(2, RoundingMode.CEILING);
            boolean isInsuranceEnabled = false;
            creditCalculationService.calculateTotalAmount(amount, isInsuranceEnabled);
        });
        assertEquals("Invalid value", exception.getMessage());
    }
}
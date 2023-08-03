package ru.taratonov.conveyor.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.taratonov.conveyor.dto.LoanApplicationRequestDTO;
import ru.taratonov.conveyor.dto.LoanOfferDTO;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {
    @Mock
    private CreditCalculationService creditCalculationService;

    @Mock
    private ScoringService scoringService;

    @InjectMocks
    private OfferService offerService;

    @Test
    void createOffers_SizeOfList() {
        List<LoanOfferDTO> offers = offerService.createOffers(new LoanApplicationRequestDTO());

        assertEquals(4, offers.size());
    }

    @Test
    void createOffers_CheckParametersInLoanOffer() {
        LoanApplicationRequestDTO loanApplicationRequest = new LoanApplicationRequestDTO();
        loanApplicationRequest.setAmount(BigDecimal.valueOf(10000));
        loanApplicationRequest.setTerm(5);

        BigDecimal rate = BigDecimal.valueOf(5);
        BigDecimal totalAmountInsurance = BigDecimal.valueOf(11000);
        BigDecimal totalAmountNotInsurance = loanApplicationRequest.getAmount();
        BigDecimal monthlyPayment = BigDecimal.valueOf(1000);

        when(scoringService.scoringPerson(anyBoolean(), anyBoolean())).thenReturn(rate);
        when(creditCalculationService.calculateTotalAmount(any(), eq(Boolean.TRUE))).thenReturn(totalAmountInsurance);
        when(creditCalculationService.calculateTotalAmount(any(), eq(Boolean.FALSE))).thenReturn(totalAmountNotInsurance);
        when(creditCalculationService.calculateMonthlyPayment(any(), any(), any())).thenReturn(monthlyPayment);

        List<LoanOfferDTO> offers = offerService.createOffers(loanApplicationRequest);
        LoanOfferDTO loanOffer = offers.get(0);

        assertEquals(loanApplicationRequest.getAmount(), loanOffer.getRequestedAmount());
        assertEquals(loanApplicationRequest.getTerm(), loanOffer.getTerm());
        assertEquals(rate, loanOffer.getRate());
        assertEquals(monthlyPayment, loanOffer.getMonthlyPayment());
        assertEquals(totalAmountNotInsurance, offers.get(0).getTotalAmount());
        assertEquals(totalAmountNotInsurance, offers.get(1).getTotalAmount());
        assertEquals(totalAmountInsurance, offers.get(2).getTotalAmount());
        assertEquals(totalAmountInsurance, offers.get(3).getTotalAmount());
    }

    @Test
    void createOffers_CheckNumOfMethodsCalling() {
        LoanApplicationRequestDTO loanApplicationRequest = new LoanApplicationRequestDTO();
        offerService.createOffers(loanApplicationRequest);

        verify(scoringService, times(4)).scoringPerson(anyBoolean(), anyBoolean());
        verify(creditCalculationService, times(4)).calculateTotalAmount(any(), anyBoolean());
        verify(creditCalculationService, times(4)).calculateMonthlyPayment(any(), any(), any());
    }
}
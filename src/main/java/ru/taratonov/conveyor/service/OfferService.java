package ru.taratonov.conveyor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.taratonov.conveyor.dto.LoanApplicationRequestDTO;
import ru.taratonov.conveyor.dto.LoanOfferDTO;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OfferService {

    private final CreditCalculationService creditCalculationService;
    private final ScoringService scoringService;

    @Autowired
    public OfferService(CreditCalculationService creditCalculationService, ScoringService scoringService) {
        this.creditCalculationService = creditCalculationService;
        this.scoringService = scoringService;
    }

    public List<LoanOfferDTO> createOffers(LoanApplicationRequestDTO loanApplicationRequest) {
        return List.of(
                generateOffer(loanApplicationRequest, false, false),
                generateOffer(loanApplicationRequest, false, true),
                generateOffer(loanApplicationRequest, true, false),
                generateOffer(loanApplicationRequest, true, true)
        );
    }

    private LoanOfferDTO generateOffer(LoanApplicationRequestDTO loanApplicationRequest, Boolean isInsuranceEnabled, Boolean isSalaryClient) {

        BigDecimal amount = loanApplicationRequest.getAmount();
        Integer term = loanApplicationRequest.getTerm();
        BigDecimal rate = scoringService.scoringPerson(isInsuranceEnabled, isSalaryClient);

        LoanOfferDTO loanOfferDTO = new LoanOfferDTO()
                .setApplicationId(null)
                .setRequestedAmount(amount)
                .setTotalAmount(creditCalculationService.calculateTotalAmount(amount, isInsuranceEnabled))
                .setTerm(term)
                .setMonthlyPayment(creditCalculationService.calculateMonthlyPayment(amount, rate, term))
                .setRate(rate)
                .setIsInsuranceEnabled(isInsuranceEnabled)
                .setIsSalaryClient(isSalaryClient);

        return loanOfferDTO;
    }


}

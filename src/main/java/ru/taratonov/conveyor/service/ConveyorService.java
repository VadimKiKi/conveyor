package ru.taratonov.conveyor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.taratonov.conveyor.dto.CreditDTO;
import ru.taratonov.conveyor.dto.LoanApplicationRequestDTO;
import ru.taratonov.conveyor.dto.LoanOfferDTO;
import ru.taratonov.conveyor.dto.ScoringDataDTO;

import java.util.List;

@Service
public class ConveyorService {

    private final ScoringService scoringService;
    private final CreditCalculationService creditCalculationService;
    private final OfferService offerService;

    @Autowired
    public ConveyorService(ScoringService scoringService, CreditCalculationService creditCalculationService, OfferService offerService) {
        this.scoringService = scoringService;
        this.creditCalculationService = creditCalculationService;
        this.offerService = offerService;
    }

    public List<LoanOfferDTO> getOffers(LoanApplicationRequestDTO loanApplicationRequest) {
        return offerService.createOffers(loanApplicationRequest);
    }

    public CreditDTO calculateParameters(ScoringDataDTO scoringData) {
        return creditCalculationService
                .calculateLoanParameters(scoringData, scoringService.scoringPerson(scoringData));
    }
}

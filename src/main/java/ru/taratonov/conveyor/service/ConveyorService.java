package ru.taratonov.conveyor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.taratonov.conveyor.dto.CreditDTO;
import ru.taratonov.conveyor.dto.LoanApplicationRequestDTO;
import ru.taratonov.conveyor.dto.LoanOfferDTO;
import ru.taratonov.conveyor.dto.ScoringDataDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConveyorService {

    private final ScoringService scoringService;
    private final CreditCalculationService creditCalculationService;
    private final OfferService offerService;

    public List<LoanOfferDTO> getOffers(LoanApplicationRequestDTO loanApplicationRequest) {
        return offerService.createOffers(loanApplicationRequest);
    }

    public CreditDTO calculateParameters(ScoringDataDTO scoringData) {
        return creditCalculationService
                .calculateLoanParameters(scoringData, scoringService.scoringPerson(scoringData));
    }
}

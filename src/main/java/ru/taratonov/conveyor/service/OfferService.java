package ru.taratonov.conveyor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.taratonov.conveyor.dto.LoanApplicationRequestDTO;
import ru.taratonov.conveyor.dto.LoanOfferDTO;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
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
        log.info("!REQUEST FOR GENERATING OFFERS RECEIVED!");
        return List.of(
                generateOffer(loanApplicationRequest, false, false),
                generateOffer(loanApplicationRequest, false, true),
                generateOffer(loanApplicationRequest, true, false),
                generateOffer(loanApplicationRequest, true, true)
        );
    }

    private LoanOfferDTO generateOffer(LoanApplicationRequestDTO loanApplicationRequest,
                                       Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        log.info("Start generate offer for {} with isInsuranceEnabled - {} and isSalaryClient - {}",
                loanApplicationRequest.getFirstName(), isInsuranceEnabled, isSalaryClient);
        BigDecimal amount = loanApplicationRequest.getAmount();
        Integer term = loanApplicationRequest.getTerm();
        BigDecimal rate = scoringService.scoringPerson(isInsuranceEnabled, isSalaryClient);
        BigDecimal totalAmount = creditCalculationService.calculateTotalAmount(amount, isInsuranceEnabled);

        LoanOfferDTO loanOfferDTO = new LoanOfferDTO()
                .setApplicationId(null)
                .setRequestedAmount(amount)
                .setTotalAmount(totalAmount)
                .setTerm(term)
                .setMonthlyPayment(creditCalculationService.calculateMonthlyPayment(totalAmount, rate, term))
                .setRate(rate)
                .setIsInsuranceEnabled(isInsuranceEnabled)
                .setIsSalaryClient(isSalaryClient);
        log.info("Offer is ready. " +
                        "Calculated data: id - {}, requestedAmount - {}, totalAmount - {}, term - {}, " +
                        "monthlyPayment - {}, rate - {}, isInsuranceEnabled - {}, isSalaryClient - {}",
                loanOfferDTO.getApplicationId(), loanOfferDTO.getRequestedAmount(),
                loanOfferDTO.getTotalAmount(), loanOfferDTO.getTerm(), loanOfferDTO.getMonthlyPayment(),
                loanOfferDTO.getRate(), loanOfferDTO.getIsInsuranceEnabled(), loanOfferDTO.getIsSalaryClient());
        return loanOfferDTO;
    }


}

package ru.taratonov.conveyor.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.taratonov.conveyor.dto.LoanApplicationRequestDTO;
import ru.taratonov.conveyor.dto.LoanOfferDTO;
import ru.taratonov.conveyor.dto.ScoringDataDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ConveyorServiceTest {

    @InjectMocks
    private ConveyorService conveyorService;

    @Mock
    private ScoringService scoringService;

    @Mock
    private CreditCalculationService creditCalculationService;

    @Mock
    private OfferService offerService;

    @Test
    void getOffers() {
        Mockito.when(offerService.createOffers(Mockito.any())).thenReturn(List.of(new LoanOfferDTO(), new LoanOfferDTO()));
        LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO();
        List<LoanOfferDTO> offers = conveyorService.getOffers(loanApplicationRequestDTO);

        assertEquals(2, offers.size());

        Mockito.verify(offerService, Mockito.times(1)).createOffers(loanApplicationRequestDTO);
    }

    @Test
    void calculateParameters() {
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();
        conveyorService.calculateParameters(scoringDataDTO);

        Mockito.verify(creditCalculationService, Mockito.times(1))
                .calculateLoanParameters(Mockito.eq(scoringDataDTO), Mockito.any());
        Mockito.verify(scoringService, Mockito.times(1)).scoringPerson(scoringDataDTO);
    }
}
package ru.taratonov.conveyor.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.taratonov.conveyor.dto.CreditDTO;
import ru.taratonov.conveyor.dto.LoanApplicationRequestDTO;
import ru.taratonov.conveyor.dto.LoanOfferDTO;
import ru.taratonov.conveyor.dto.ScoringDataDTO;
import ru.taratonov.conveyor.service.ConveyorService;

import java.util.List;


@RestController

@RequestMapping("/conveyor")
public class ConveyorController {

    private final ConveyorService conveyorService;

    @Autowired
    public ConveyorController(ConveyorService conveyorService) {
        this.conveyorService = conveyorService;
    }

    @PostMapping("/offers")
    public List<LoanOfferDTO> getPossibleLoanOffers(@RequestBody @Valid LoanApplicationRequestDTO loanApplicationRequest) {
        return conveyorService.getOffers(loanApplicationRequest);
    }

    @PostMapping("/calculation")
    public CreditDTO calculateLoanParameters(@RequestBody @Valid ScoringDataDTO scoringData) {
        return conveyorService.calculateParameters(scoringData);
    }
}

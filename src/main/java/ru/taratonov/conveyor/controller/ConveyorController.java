package ru.taratonov.conveyor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.taratonov.conveyor.dto.CreditDTO;
import ru.taratonov.conveyor.dto.ErrorDTO;
import ru.taratonov.conveyor.dto.LoanApplicationRequestDTO;
import ru.taratonov.conveyor.dto.LoanOfferDTO;
import ru.taratonov.conveyor.dto.ScoringDataDTO;
import ru.taratonov.conveyor.service.ConveyorService;

import java.util.List;


@RestController

@RequestMapping("/conveyor")
@Tag(name = "Conveyor Controller", description = "Managing loan offers")
public class ConveyorController {

    private final ConveyorService conveyorService;

    @Autowired
    public ConveyorController(ConveyorService conveyorService) {
        this.conveyorService = conveyorService;
    }

    @PostMapping("/offers")
    @Operation(summary = "Get loan offers", description = "Allows to get 4 loan offers for person")
    @ApiResponse(
            responseCode = "200",
            description = "List of offers received!",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoanOfferDTO.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Prescoring failed",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorDTO.class)))
    public List<LoanOfferDTO> getPossibleLoanOffers(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Loan request",
            content = @Content(schema = @Schema(implementation = LoanApplicationRequestDTO.class)))
                                                    @RequestBody
                                                    @Valid LoanApplicationRequestDTO loanApplicationRequest) {
        return conveyorService.getOffers(loanApplicationRequest);
    }

    @PostMapping("/calculation")
    @Operation(summary = "Get loan parameters", description = "Allows to get all parameters for credit")
    @ApiResponse(
            responseCode = "200",
            description = "Parameters received!",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CreditDTO.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Scoring failed",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorDTO.class)))
    public CreditDTO calculateLoanParameters(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "All information about user for calculation loan parameters",
            content = @Content(schema = @Schema(implementation = ScoringDataDTO.class)))
                                             @RequestBody
                                             @Valid ScoringDataDTO scoringData) {
        return conveyorService.calculateParameters(scoringData);
    }
}

package com.billing.invoice.controllers.APIs;

import com.billing.invoice.domain.dto.invoice_dto.ResponseMessageDto;
import com.billing.invoice.domain.dto.payment_dto.InvoiceRemainingBalanceResponseDto;
import com.billing.invoice.exception_handler.exceptions.dto.ValidationErrorsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Invoice controller", description = "Controller for invoice operations")
@RequestMapping("/v1/invoice")
public interface InvoiceAPI {

    @Operation(summary = "Get remaining balance of invoice",
            description = "This method get remaining balance of invoice by invoice id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Remaining balance get successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InvoiceRemainingBalanceResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Request is wrong",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    oneOf = {
                                            ValidationErrorsDto.class
                                    }
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Validation Errors",
                                            value = "{\n" +
                                                    "  \"errors\": [\n" +
                                                    "    {\n" +
                                                    "      \"field\": \"Id\",\n" +
                                                    "      \"message\": \"Id must be great of 0\",\n" +
                                                    "      \"rejectedValue\": \"rt\"\n" +
                                                    "    }\n" +
                                                    "  ]\n" +
                                                    "}"
                                    )
                            })),
            @ApiResponse(responseCode = "404",
                    description = "Invoice not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class)
                    )),
            @ApiResponse(responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class)
                    )),
    })
    @GetMapping("{id}/balance")
    ResponseEntity<InvoiceRemainingBalanceResponseDto> getRemainingBalance(
            @PathVariable
            @Parameter(description = "Id of invoice", example = "124")
            @NotNull(message = "Id can not be null")
            @Min(value = 1, message = "Id must be great of 0")
            Long id
    );
}

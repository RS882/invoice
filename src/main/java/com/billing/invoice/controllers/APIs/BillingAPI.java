package com.billing.invoice.controllers.APIs;

import com.billing.invoice.domain.dto.invoice_dto.InvoiceResponseDto;
import com.billing.invoice.domain.dto.invoice_dto.ResponseMessageDto;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Billing Controller", description = "Controller for billing of invoice")
@RequestMapping("/v1/billing")
public interface BillingAPI {

    @Operation(summary = "Get new invoice",
            description = "This method create and get new invoice for customer."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice get successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InvoiceResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Request is wrong",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    oneOf = {
                                            ValidationErrorsDto.class,
                                            ResponseMessageDto.class
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
                                    ),
                                    @ExampleObject(
                                            name = "Wrong invoice parameter",
                                            value = "{\"message\": \"The invoice was already issued\"}"
                                    )
                            })),
            @ApiResponse(responseCode = "404",
                    description = "Customer not found",
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

    @GetMapping
    ResponseEntity<InvoiceResponseDto> getInvoiceForCustomer(
            @RequestParam
            @Parameter(description = "Id of customer that will billing", example = "124")
            @NotNull(message = "Id can not be null")
            @Min(value = 1, message = "Id must be great of 0")
            Long id
    );
}

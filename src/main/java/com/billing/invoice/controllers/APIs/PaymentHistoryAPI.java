package com.billing.invoice.controllers.APIs;

import com.billing.invoice.domain.dto.invoice_dto.ResponseMessageDto;
import com.billing.invoice.domain.dto.payment_dto.PaymentRequestDto;
import com.billing.invoice.exception_handler.exceptions.dto.ValidationErrorsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Payment history controller", description = "Controller for payment history of invoice")
@RequestMapping("/v1/payment")
public interface PaymentHistoryAPI {


    @Operation(summary = "Create new payment history",
            description = "This method get new payment data and create new payment history for invoice.",
            requestBody = @RequestBody(
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PaymentRequestDto.class)))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment history created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseMessageDto.class))),
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
                                                    "      \"field\": \"PaymentRequestDto.amount\",\n" +
                                                    "      \"message\": \"Amount of payment cannot be null\",\n" +
                                                    "      \"rejectedValue\": \"rt\"\n" +
                                                    "    }\n" +
                                                    "  ]\n" +
                                                    "}"
                                    ),
                                    @ExampleObject(
                                            name = "Invalid payment method",
                                            value = "{\"message\": \"Invalid payment method\"}"
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
                    ))
    })
    @PostMapping()
    ResponseEntity<ResponseMessageDto> createNewPaymentHistory(
            @org.springframework.web.bind.annotation.RequestBody
            @Valid
            PaymentRequestDto dto
    );
}

package com.billing.invoice.domain.dto.payment_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "Request DTO with payment data")
public class PaymentRequestDto {

    @Schema(description = "Id of invoice", example = "263")
    @NotNull(message = "Id of invoice cannot be null")
    @Min(value = 1, message = "Id of invoice must be great of 0")
    Long invoiceId;

    @Schema(description = "Amount of payment", example = "5108.09")
    @NotNull(message = "Amount of payment cannot be null")
    @Min(value = 0, message = "Amount of payment must be great of 0")
    Double amount;

    @Schema(description = "Method of payment", examples = {"CREDIT_CARD", "PAYPAL", "BANK_TRANSFER"})
    @NotNull(message = "Method of payment cannot be null")
    String method;
}

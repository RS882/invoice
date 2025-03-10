package com.billing.invoice.domain.dto.payment_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true)
@Schema(description = "Dto for invoice remaining balance")
public class InvoiceRemainingBalanceResponseDto {

    @Schema(description = "Id of invoice", example = "263")
    private final Long invoiceId;

    @Schema(description = "Remaining balance of invoice", example = "34.23")
    private final BigDecimal remainingBalance;

    public static InvoiceRemainingBalanceResponseDto from(Long invoiceId, BigDecimal amount) {
        return new InvoiceRemainingBalanceResponseDto(
                invoiceId,
                amount
        );
    }
}


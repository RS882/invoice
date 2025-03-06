package com.billing.invoice.domain.dto.invoice_dto;

import com.billing.invoice.constant.InvoiceStatus;
import com.billing.invoice.domain.entity.Invoice;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true)
@Schema(description = "Dto for invoice's parameter")
public class InvoiceResponseDto {

    @Schema(description = "Id of invoice", example = "263")
    private final Long invoiceId;

    @Schema(description = "Id of customer", example = "14")
    private final Long customerId;

    @Schema(description = "Total amount of invoice", example = "5108.09")
    private final BigDecimal amount;

    @Schema(description = "Date of invoice", example = "24.08.25")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate billingDate;

    @Schema(description = "Path for loading invoice file", example = "http://seit/invoice/kjjj/23")
    private final String invoiceFilePath;

    @Schema(description = "Status of invoice", examples = {"PENDING", "PARTIALLY_PAID", "PAID"})
    private final InvoiceStatus status;

    public static InvoiceResponseDto from(Invoice invoice) {
        return new InvoiceResponseDto(
                invoice.getId(),
                invoice.getCustomer().getId(),
                invoice.getAmount(),
                invoice.getBillingDate(),
                invoice.getInvoiceFilePath(),
                invoice.getStatus()
        );
    }
}

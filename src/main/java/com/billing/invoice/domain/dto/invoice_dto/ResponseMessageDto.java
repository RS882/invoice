package com.billing.invoice.domain.dto.invoice_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO with some message")
public class ResponseMessageDto {
    @Schema(description = "Message ", example = "Some text message")
    private String message;
}

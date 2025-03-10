package com.billing.invoice.controllers;

import com.billing.invoice.constant.InvoiceStatus;
import com.billing.invoice.constant.PaymentMethod;
import com.billing.invoice.domain.dto.payment_dto.PaymentRequestDto;
import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.domain.entity.Invoice;
import com.billing.invoice.domain.entity.PaymentHistory;
import com.billing.invoice.repositories.CustomerRepository;
import com.billing.invoice.repositories.InvoiceRepository;
import com.billing.invoice.repositories.PaymentHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("Payment controller integration tests: ")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
@Transactional
@Rollback
class PaymentHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    private ObjectMapper mapper = new ObjectMapper();

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        Customer customer = Customer.builder()
                .name("Test")
                .monthsSubscribed(10)
                .dataUsedGB(40)
                .build();
        testCustomer = customerRepository.save(customer);
    }

    @Nested
    @DisplayName("POST /v1/payment")
    public class CreateNewPaymentHistoryTests {

        private final String URL = "/v1/payment";

        private Invoice savedInvoice;

        private final double INVOICE_AMOUNT= 120;

        @BeforeEach
        void nestedSetUp() {
            Invoice invoice = Invoice.builder()
                    .customer(testCustomer)
                    .amount(BigDecimal.valueOf(INVOICE_AMOUNT))
                    .billingDate(LocalDate.now())
                    .status(InvoiceStatus.PARTIALLY_PAID)
                    .build();

            savedInvoice = invoiceRepository.save(invoice);
        }

        @ParameterizedTest
        @CsvSource({
                "20.00, PARTIALLY_PAID",
                "120.00, PAID",
                "28837.98, OVERPAID"
        })
        public void create_new_payment_history_status_201(Double amount, String invoiceStatus) throws Exception {

            PaymentRequestDto paymentRequestDto = PaymentRequestDto.builder()
                    .invoiceId(savedInvoice.getId())
                    .amount(amount)
                    .method(PaymentMethod.CREDIT_CARD.name())
                    .build();

            String dtoJson = mapper.writeValueAsString(paymentRequestDto);

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(dtoJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));

            List<PaymentHistory> paymentHistoryList =
                    paymentHistoryRepository.findAllByInvoiceId(savedInvoice.getId());

            assertEquals(1, paymentHistoryList.size());

            PaymentHistory savedPaymentHistory = paymentHistoryList.get(0);
            assertEquals(0, BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP)
                    .compareTo(savedPaymentHistory.getAmountPaid().setScale(2, RoundingMode.HALF_UP)));
            assertEquals(PaymentMethod.CREDIT_CARD, savedPaymentHistory.getPaymentMethod());

            Invoice updateInvoice = invoiceRepository.findById(savedInvoice.getId()).get();

            assertEquals(InvoiceStatus.valueOf(invoiceStatus), updateInvoice.getStatus());
        }

        @ParameterizedTest
        @MethodSource("incorrectDtoValues")
        public void create_new_payment_history_status_400_payment_data_is_incorrect(
                PaymentRequestDto dto) throws Exception {

            String dtoJson = mapper.writeValueAsString(dto);

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(dtoJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors").isArray());
        }

        private static Stream<Arguments> incorrectDtoValues() {
            String method = "PAYPAL";
            Long invoiceId = 1L;
            Double amount = 20.34;

            PaymentRequestDto dto1 = PaymentRequestDto.builder()
                    .invoiceId(0L)
                    .amount(amount)
                    .method(method)
                    .build();

            PaymentRequestDto dto2 = PaymentRequestDto.builder()
                    .invoiceId(-238L)
                    .amount(amount)
                    .method(method)
                    .build();

            PaymentRequestDto dto3 = PaymentRequestDto.builder()
                    .amount(amount)
                    .method(method)
                    .build();

            PaymentRequestDto dto4 = PaymentRequestDto.builder()
                    .invoiceId(invoiceId)
                    .amount(0.00)
                    .method(method)
                    .build();

            PaymentRequestDto dto5 = PaymentRequestDto.builder()
                    .invoiceId(invoiceId)
                    .amount(-3938.09)
                    .method(method)
                    .build();

            PaymentRequestDto dto6 = PaymentRequestDto.builder()
                    .invoiceId(invoiceId)
                    .method(method)
                    .build();

            PaymentRequestDto dto7 = PaymentRequestDto.builder()
                    .invoiceId(invoiceId)
                    .amount(amount)
                    .build();

            return Stream.of(
                    Arguments.of(dto1),
                    Arguments.of(dto2),
                    Arguments.of(dto3),
                    Arguments.of(dto4),
                    Arguments.of(dto5),
                    Arguments.of(dto6),
                    Arguments.of(dto7)
            );
        }

        @Test
        public void create_new_payment_history_status_404_when_payment_method_is_not_found() throws Exception {

            String method = "TEST_MeThod!!14";
            Long invoiceId = 1L;
            Double amount = 20.34;

            PaymentRequestDto dto = PaymentRequestDto.builder()
                    .invoiceId(invoiceId)
                    .amount(amount)
                    .method(method)
                    .build();
            String dtoJson = mapper.writeValueAsString(dto);

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(dtoJson))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }

        @Test
        public void create_new_payment_history_status_404_when_invoice_is_not_found() throws Exception {
            String method = "PAYPAL";
            Long invoiceId = 167583982987L;
            Double amount = 20.34;

            PaymentRequestDto dto = PaymentRequestDto.builder()
                    .invoiceId(invoiceId)
                    .amount(amount)
                    .method(method)
                    .build();
            String dtoJson = mapper.writeValueAsString(dto);

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(dtoJson))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }
    }
}
package com.billing.invoice.controllers;

import com.billing.invoice.constant.InvoiceStatus;
import com.billing.invoice.constant.PaymentMethod;
import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.domain.entity.Invoice;
import com.billing.invoice.repositories.CustomerRepository;
import com.billing.invoice.repositories.InvoiceRepository;
import com.billing.invoice.services.PaymentServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("Invoice controller integration tests: ")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
@Transactional
@Rollback
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;


    @Autowired
    private PaymentServiceImpl paymentService;

    private Customer testCustomer;

    private ObjectMapper mapper = new ObjectMapper();

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
    @DisplayName("GET /v1/invoice/{id}/balance")
    public class CalculateRemainingBalanceTests {

        private final String URL_WITHOUT_ID = getURL("");
        private final String URL = getURL("{id}");;

        private final double INVOICE_AMOUNT = 120;

        private Invoice savedInvoice;

        private String getURL(String part){
            return String.format("/v1/invoice/%s/balance", part);
        }

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

        @Test
        public void calculate_remaining_balance_status_200_when_invoice_status_is_PENDING() throws Exception {

            mockMvc.perform(get(URL, savedInvoice.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.invoiceId").value(savedInvoice.getId()))
                    .andExpect(jsonPath("$.remainingBalance").value(INVOICE_AMOUNT));
        }

        @ParameterizedTest
        @CsvSource({
                "20.00, 100,00",
                "120,00, 0.00",
                "1234.00, -1114.00"
        })
        public void calculate_remaining_balance_status_200_when_invoice_status_is_not_PENDING(
                double paymentAmount, double balance
        ) throws Exception {

            BigDecimal amount = BigDecimal.valueOf(paymentAmount);
            paymentService.addPayment(savedInvoice.getId(), amount, PaymentMethod.BANK_TRANSFER);

            mockMvc.perform(get(URL, savedInvoice.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.invoiceId").value(savedInvoice.getId()))
                    .andExpect(jsonPath("$.remainingBalance").value(balance));
        }

        @ParameterizedTest
        @CsvSource({
                "0",
                "-23"})
        public void calculate_remaining_balance_status_400_invoice_id_is_incorrect(Long invoiceId) throws Exception {
            mockMvc.perform(get(URL, invoiceId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors").isArray());
        }

        @Test
        public void calculate_remaining_balance_status_404_when_invoice_id_is_null() throws Exception {
            mockMvc.perform(get(URL_WITHOUT_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        public void calculate_remaining_balance_status_404_when_invoice_is_not_found() throws Exception {
            mockMvc.perform(get(URL, 182737636)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }
    }
}
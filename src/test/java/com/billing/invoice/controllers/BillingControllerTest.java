package com.billing.invoice.controllers;

import com.billing.invoice.constant.InvoiceStatus;
import com.billing.invoice.constant.PlanType;
import com.billing.invoice.domain.dto.invoice_dto.InvoiceResponseDto;
import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.domain.entity.DataUsageHistory;
import com.billing.invoice.domain.entity.Invoice;
import com.billing.invoice.repositories.CustomerRepository;
import com.billing.invoice.repositories.DataUsageHistoryRepository;
import com.billing.invoice.repositories.InvoiceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.minio.*;
import io.minio.messages.Item;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("Billing  controller integration tests: ")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
@Transactional
@Rollback
class BillingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DataUsageHistoryRepository dataUsageHistoryRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private MinioClient minioClient;

    private Customer testCustomer;

    private ObjectMapper mapper = new ObjectMapper();

    @Value("${bucket.name}")
    private String bucketName;

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

    @AfterEach
    void clean() throws Exception {

        boolean isBucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());

        if (isBucketExists) {

            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(String.format("%d/", testCustomer.getId()))
                            .recursive(true)
                            .build());

            for (Result<Item> result : results) {
                Item item = result.get();
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(item.objectName())
                                .build());
            }
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        }
    }

    @Nested
    @DisplayName("GET /v1/billing/invoice/customer/{id}")
    public class GetInvoiceForCustomerTests {

        private final String URL_WITHOUT_ID = "/v1/billing/invoice/customer";
        private final String URL = URL_WITHOUT_ID+"/{id}";
        @Test
        public void get_invoice_for_customer_status_200() throws Exception {
            Long testCustomerId = testCustomer.getId();
            int testCustomerMonthsSubscribed = testCustomer.getMonthsSubscribed();

            MvcResult mResult = mockMvc.perform(get(URL, testCustomerId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.invoiceId").isNumber())
                    .andExpect(jsonPath("$.customerId").value(testCustomerId))
                    .andExpect(jsonPath("$.amount", is(35.7)))
                    .andExpect(jsonPath("$.billingDate", is(LocalDate.now().toString())))
                    .andExpect(jsonPath("$.invoiceFilePath").isString())
                    .andExpect(jsonPath("$.status", is(InvoiceStatus.PENDING.name())))
                    .andReturn();

            String jsonResponse = mResult.getResponse().getContentAsString();
            InvoiceResponseDto responseDto = mapper.readValue(jsonResponse, InvoiceResponseDto.class);

            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(String.format("%d/%d/", testCustomerId, responseDto.getInvoiceId()))
                            .recursive(true)
                            .build());

            int count = 0;
            for (Result<Item> result : results) {
                Item item = result.get();
                count++;
                assertTrue(item.objectName().endsWith(".pdf"));
            }
            assertEquals(1, count);

            List<DataUsageHistory> dataUsageHistoryList = dataUsageHistoryRepository.findAllByCustomerId(testCustomerId);
            assertEquals(1, dataUsageHistoryList.size());
            DataUsageHistory dataUsageHistory = dataUsageHistoryList.get(0);
            assertEquals(PlanType.BASIC, dataUsageHistory.getPlanType());

            int nowMonthValue = LocalDate.now().getMonthValue();
            assertEquals(dataUsageHistory.getStartDate().getMonthValue(), nowMonthValue - 1);
            assertEquals(dataUsageHistory.getEndDate().getMonthValue(), nowMonthValue - 1);

            Customer updatedCustomer = customerRepository.findById(testCustomerId).orElse(null);
            assertNotNull(updatedCustomer);
            assertEquals(0, updatedCustomer.getDataUsedGB());
            assertEquals(updatedCustomer.getMonthsSubscribed(), testCustomerMonthsSubscribed + 1);
        }

        @ParameterizedTest
        @CsvSource({
                "0",
                "-23"})
        public void get_invoice_for_customer_status_400_customer_id_is_incorrect(Long customerId) throws Exception {
            mockMvc.perform(get(URL, customerId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors").isArray());
        }

        @Test
        public void get_invoice_for_customer_status_404_when_customer_id_is_null() throws Exception {
            mockMvc.perform(get(URL_WITHOUT_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        public void get_invoice_for_customer_status_404_customer_not_found() throws Exception {
            mockMvc.perform(get(URL, 12345)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }

        @Test
        public void get_invoice_for_customer_status_400_if_invoice_has_already_been_inserted() throws Exception {

            Invoice invoice = Invoice.builder()
                    .customer(testCustomer)
                    .amount(BigDecimal.valueOf(testCustomer.getDataUsedGB() * 2))
                    .billingDate(LocalDate.now())
                    .status(InvoiceStatus.PARTIALLY_PAID)
                    .build();

            invoiceRepository.save(invoice);

            mockMvc.perform(get(URL, testCustomer.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }

        @Test
        public void get_invoice_for_customer_status_400_if_data_usage_history_is_already_exists() throws Exception {

            DataUsageHistory newDataUsageHistory = DataUsageHistory.builder()
                    .startDate(LocalDate.now().minusMonths(1))
                    .endDate(LocalDate.now().minusMonths(1))
                    .planType(testCustomer.getPlanType())
                    .dataUsedGB(testCustomer.getDataUsedGB())
                    .customer(testCustomer)
                    .build();

            dataUsageHistoryRepository.save(newDataUsageHistory);

            mockMvc.perform(get(URL, testCustomer.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.message", isA(String.class)));
        }
    }
}

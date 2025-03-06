package com.billing.invoice.controllers;

import com.billing.invoice.constant.InvoiceStatus;
import com.billing.invoice.domain.dto.invoice_dto.InvoiceResponseDto;
import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.repositories.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import org.junit.jupiter.api.*;
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

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Nested
    @DisplayName("GET /v1/billing")
    public class GetInvoiceForCustomerTests {

        private String URL = "/v1/billing";

        @Test
        public void get_invoice_for_customer_status_200() throws Exception {

            Long testCustomerId = testCustomer.getId();
            MvcResult mResult = mockMvc.perform(get(URL)
                            .param("id", String.valueOf(testCustomerId))
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

            for (Result<Item> result : results) {
                Item item = result.get();
                assertTrue(item.objectName().endsWith(".pdf"));
            }
        }
    }
}

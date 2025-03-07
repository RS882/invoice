package com.billing.invoice.configs;

import com.billing.invoice.constant.InvoiceStatus;
import com.billing.invoice.constant.PaymentMethod;
import com.billing.invoice.constant.PlanType;
import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.domain.entity.DataUsageHistory;
import com.billing.invoice.domain.entity.Invoice;
import com.billing.invoice.domain.entity.PaymentHistory;
import com.billing.invoice.repositories.CustomerRepository;
import com.billing.invoice.repositories.DataUsageHistoryRepository;
import com.billing.invoice.repositories.InvoiceRepository;
import com.billing.invoice.repositories.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.billing.invoice.utilities.DataTimeUtilities.getFirstDayOfLastMonth;
import static com.billing.invoice.utilities.DataTimeUtilities.getLastDayOfLastMonth;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataBaseTestDataConfig implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final DataUsageHistoryRepository dataUsageHistoryRepository;

    @Override
    public void run(String... args) throws Exception {

        List<Customer> customerList = new ArrayList<>();
        Customer customerBasis1 = Customer.builder().name("John").monthsSubscribed(10).dataUsedGB(40).build();
        Customer customerBasis2 = Customer.builder().name("John2").monthsSubscribed(15).dataUsedGB(70).build();
        Customer customerPremium1 = Customer.builder().name("John3").planType(PlanType.PREMIUM).monthsSubscribed(26).dataUsedGB(90).build();
        Customer customerPremium2 = Customer.builder().name("John4").planType(PlanType.PREMIUM).monthsSubscribed(4).dataUsedGB(240).build();
        Customer customerBusiness1 = Customer.builder().name("John5").planType(PlanType.BUSINESS).monthsSubscribed(18).dataUsedGB(430).build();
        Customer customerBusiness2 = Customer.builder().name("John6").planType(PlanType.BUSINESS).monthsSubscribed(27).dataUsedGB(800).build();

        customerList.add(customerBasis1);
        customerList.add(customerBasis2);
        customerList.add(customerPremium1);
        customerList.add(customerPremium2);
        customerList.add(customerBusiness1);
        customerList.add(customerBusiness2);

        customerRepository.saveAll(customerList);
        log.info("✅ Customers saved successful.");

        List<Invoice> invoiceList = new ArrayList<>();
        for (Customer customer : customerList) {
            Invoice invoice = Invoice.builder()
                    .customer(customer)
                    .amount(BigDecimal.valueOf(customer.getDataUsedGB() * 2))
                    .billingDate(LocalDate.now().minusMonths(2))
                    .status(InvoiceStatus.PARTIALLY_PAID)
                    .build();
            invoiceList.add(invoice);
        }
        invoiceRepository.saveAll(invoiceList);
        log.info("✅ Invoices saved successful.");

        List<PaymentHistory> paymentHistoryList = new ArrayList<>();
        for (Invoice invoice : invoiceList) {
            PaymentHistory paymentHistory = PaymentHistory.builder()
                    .invoice(invoice)
                    .paymentDate(LocalDateTime.now().minusDays(2))
                    .amountPaid(invoice.getAmount().multiply(BigDecimal.valueOf(0.5)))
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();
            paymentHistoryList.add(paymentHistory);
        }
        paymentHistoryRepository.saveAll(paymentHistoryList);
        log.info("✅ Payment history saved successful.");

             List<DataUsageHistory> dataUsageHistoryList = new ArrayList<>();
             LocalDate basic = LocalDate.now().minusMonths(2);
        for (Customer customer : customerList) {
            DataUsageHistory dataUsageHistory = DataUsageHistory.builder()
                    .customer(customer)
                    .startDate(getFirstDayOfLastMonth(basic))
                    .endDate(getLastDayOfLastMonth(basic))
                    .planType(PlanType.BUSINESS)
                    .dataUsedGB(customer.getDataUsedGB() / 2)
                    .build();
            dataUsageHistoryList.add(dataUsageHistory);
        }
        dataUsageHistoryRepository.saveAll(dataUsageHistoryList);
        log.info("✅ Data Usage History saved successful.");
    }
}




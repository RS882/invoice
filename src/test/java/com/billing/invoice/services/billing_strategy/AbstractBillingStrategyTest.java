package com.billing.invoice.services.billing_strategy;

import com.billing.invoice.constant.PlanType;
import com.billing.invoice.domain.entity.Customer;
import com.billing.invoice.domain.model.BillData;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AbstractBillingStrategyTest {

    private AbstractBillingStrategy strategy;
    private PlanType testPlan;
    private Customer testCustomer;

    @BeforeEach
    void setup() {

        strategy = new BasicPlanStrategy();
        testPlan = PlanType.BASIC;
        testCustomer = Customer.builder()
                .monthsSubscribed(18)
                .dataUsedGB(150.0)
                .build();
        ReflectionTestUtils.setField(testCustomer, "id", 1L);
    }

    @ParameterizedTest
    @CsvSource({
            // dataUsedGB, monthsSubscribed, expectedTotal
            "50.0, 6, 35.7",
            "120.0, 13, 192.18",
            "200.0, 25, 353.43",
            "0.0, 1, 35.70",
            "300.0, 36, 567.63"
    })
    void testCalculateBill_CorrectCalculation(double dataUsedGB, int monthsSubscribed, String expectedTotalStr) {

        BigDecimal expectedTotal = new BigDecimal(expectedTotalStr);

        Customer customer = Customer.builder()
                .monthsSubscribed(monthsSubscribed)
                .dataUsedGB(dataUsedGB)
                .build();
        ReflectionTestUtils.setField(customer, "id", 1L);

        BillData billData = strategy.calculateBill(customer);
        BigDecimal totalBill = billData.getTotal();
        assertNotNull(totalBill);
        assertEquals(2, totalBill.scale(), "There must be 2 decimal places.");
        assertTrue(totalBill.compareTo(BigDecimal.ZERO) > 0, "Sum must be great of 0.");
        assertEquals(0, totalBill.compareTo(expectedTotal), "The expected amount should match the calculated one.");
    }

    @ParameterizedTest
    @CsvSource({
            // dataUsedGB, monthsSubscribed, sizeOfError
            "-10.0, -6, 2",
            "-2.0, 0, 1",
            "4.0, -82, 1",
    })
    void testCalculateBill_InCorrectCalculation(double dataUsedGB, int monthsSubscribed, int sizeOfError) {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Customer customer = Customer.builder()
                .name("John")
                .monthsSubscribed(monthsSubscribed)
                .dataUsedGB(dataUsedGB)
                .build();

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertEquals(violations.size(), sizeOfError);
    }

    @Test
    void testCalculateVat_CorrectCalculation() throws Exception {
        var method = AbstractBillingStrategy.class.getDeclaredMethod("calculateVat", BigDecimal.class);
        method.setAccessible(true);
        BigDecimal totalAfterDiscount = new BigDecimal("100.00");

        BigDecimal vat = (BigDecimal) method.invoke(strategy, totalAfterDiscount);

        assertEquals(new BigDecimal("19.00"), vat, "VAT must be 19%.");
    }

    @Test
    void testCalculateOverageCharge_CorrectCalculation() throws Exception {
        var method = AbstractBillingStrategy.class.getDeclaredMethod("calculateOverageCharge", double.class);
        method.setAccessible(true);
        BigDecimal overageCharge = (BigDecimal) method.invoke(strategy, 60.0);

        assertEquals(new BigDecimal("20.00"), overageCharge, "The overrun must be calculated correctly..");
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0.00",
            "3, 0.00",
            "12, 0.00",
            "15, 5.00",
            "24, 5.00",
            "34, 10.00",
            "182273, 10.00"

    })
    void testGetDiscountRate_CorrectCalculation(int monthsSubscribed, String discount) throws Exception {
        var method = AbstractBillingStrategy.class.getDeclaredMethod("getDiscountRate", int.class);
        method.setAccessible(true);
        BigDecimal discountRate = (BigDecimal) method.invoke(strategy, monthsSubscribed);

        assertEquals(new BigDecimal(discount), discountRate, "Discount must be " + monthsSubscribed + "%");
    }

    @Test
    void testGetDiscount_CorrectCalculation() throws Exception {
        var method = AbstractBillingStrategy.class.getDeclaredMethod("getDiscount", BigDecimal.class, BigDecimal.class);
        method.setAccessible(true);
        BigDecimal discount = (BigDecimal) method.invoke(strategy, new BigDecimal("5.00"), new BigDecimal("200.00"));

        assertEquals(new BigDecimal("10.00"), discount, "5% of 200 should be 10.");
    }

    @Test
    void testCalculateBill_WithNullCustomer_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> strategy.calculateBill(null),
                "Expected NullPointerException when passing null.");
    }
}

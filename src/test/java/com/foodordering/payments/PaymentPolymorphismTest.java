package com.foodordering.payments;

import com.foodordering.interfaces.Orderable;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class PaymentPolymorphismTest {

    @Test
    void testCreditCardPayment() {
        Orderable payment = new CreditCardPayment("4111 1111 1111 1111", "Ahmed", "12/28", "123");
        assertTrue(payment.process(150.0));
        assertFalse(payment.process(0));
    }

    @Test
    void testDebitCardPayment() {
        Orderable payment = new DebitCardPayment("5000 0000 0000", "Ahmed");
        assertTrue(payment.process(99.9));
        assertFalse(payment.process(-10));
    }

    @Test
    void testCashPayment() {
        Orderable payment = new CashPayment("Cashier");
        assertTrue(payment.process(10));
        assertFalse(payment.process(0));
    }

    @Test
    void testPolymorphicBehavior() {
        List<Orderable> methods = Arrays.asList(
                new CreditCardPayment("4111 1111 1111 1111", "Ahmed", "12/28", "123"),
                new DebitCardPayment("5000 0000 0000", "Ahmed"),
                new CashPayment("Cashier")
        );

        long success = methods.stream().filter(m -> m.process(50.0)).count();
        assertEquals(3, success);
    }
}

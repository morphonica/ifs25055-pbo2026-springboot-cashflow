package org.delcom.ifs25055pbo2026springbootcashflow.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TransactionTest {

    @Test
    void storesAllFields() {
        LocalDateTime time = LocalDateTime.of(2026, 1, 2, 3, 4);
        Transaction t = new Transaction(7, "Gaji", new BigDecimal("5000000"), TransactionType.INCOME, time);

        assertEquals(7, t.getId());
        assertEquals("Gaji", t.getDescription());
        assertEquals(new BigDecimal("5000000"), t.getAmount());
        assertEquals(TransactionType.INCOME, t.getType());
        assertEquals(time, t.getCreatedAt());
    }

    @Test
    void defaultConstructorSetsCreationTime() {
        Transaction t = new Transaction(1, "Kopi", BigDecimal.TEN, TransactionType.EXPENSE);
        assertNotNull(t.getCreatedAt());
    }

    @Test
    void changeMethodsUpdateData() {
        Transaction t = new Transaction(1, "Kopi", BigDecimal.TEN, TransactionType.EXPENSE);
        t.changeDescription("Teh");
        t.changeAmount(BigDecimal.ONE);

        assertEquals("Teh", t.getDescription());
        assertEquals(BigDecimal.ONE, t.getAmount());
        assertEquals(1, t.getId());
    }

    @Test
    void incomeIncreasesAndExpenseDecreasesBalance() {
        Transaction income = new Transaction(1, "Gaji", new BigDecimal("100"), TransactionType.INCOME);
        Transaction expense = new Transaction(2, "Makan", new BigDecimal("40"), TransactionType.EXPENSE);

        assertTrue(income.isIncome());
        assertFalse(expense.isIncome());
        assertEquals(new BigDecimal("100"), income.signedAmount());
        assertEquals(new BigDecimal("-40"), expense.signedAmount());
    }
}

package org.delcom.ifs25055pbo2026springbootcashflow.adapter.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.delcom.ifs25055pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.entity.TransactionType;
import org.junit.jupiter.api.Test;

class TransactionRepositoryTest {

    private final TransactionRepository repository = new TransactionRepository();

    @Test
    void generatesSequentialIdsAndTypes() {
        Transaction income = repository.saveIncome("Gaji", new BigDecimal("100"));
        Transaction expense = repository.saveExpense("Makan", new BigDecimal("30"));

        assertEquals(1, income.getId());
        assertEquals(TransactionType.INCOME, income.getType());
        assertEquals(2, expense.getId());
        assertEquals(TransactionType.EXPENSE, expense.getType());
        assertEquals(2, repository.count());
    }

    @Test
    void idsAreNotReusedAfterDelete() {
        repository.saveIncome("a", BigDecimal.ONE);
        repository.deleteById(1);
        assertEquals(2, repository.saveIncome("b", BigDecimal.ONE).getId());
    }
}

package org.delcom.ifs25055pbo2026springbootcashflow.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class SortOptionTest {

    private static List<Transaction> sample() {
        return new ArrayList<>(List.of(
                new Transaction(1, "a", new BigDecimal("100"), TransactionType.INCOME),
                new Transaction(2, "b", new BigDecimal("50"), TransactionType.EXPENSE),
                new Transaction(3, "c", new BigDecimal("50"), TransactionType.INCOME),
                new Transaction(4, "d", new BigDecimal("100"), TransactionType.EXPENSE)));
    }

    private static List<Integer> idsSortedBy(SortOption option) {
        List<Transaction> list = sample();
        list.sort(option.getComparator());
        return list.stream().map(Transaction::getId).toList();
    }

    @Test
    void hasFourOptionsWithLabelsAndComparators() {
        assertEquals(4, SortOption.values().length);
        for (SortOption option : SortOption.values()) {
            assertNotNull(option.getLabel());
            assertNotNull(option.getComparator());
        }
        assertEquals("Jumlah terkecil", SortOption.AMOUNT_ASC.getLabel());
    }

    @Test
    void amountAscending() {
        assertEquals(List.of(2, 3, 1, 4), idsSortedBy(SortOption.AMOUNT_ASC));
    }

    @Test
    void amountDescending() {
        assertEquals(List.of(1, 4, 2, 3), idsSortedBy(SortOption.AMOUNT_DESC));
    }

    @Test
    void incomeFirst() {
        assertEquals(List.of(1, 3, 2, 4), idsSortedBy(SortOption.INCOME_FIRST));
    }

    @Test
    void expenseFirst() {
        assertEquals(List.of(2, 4, 1, 3), idsSortedBy(SortOption.EXPENSE_FIRST));
    }
}

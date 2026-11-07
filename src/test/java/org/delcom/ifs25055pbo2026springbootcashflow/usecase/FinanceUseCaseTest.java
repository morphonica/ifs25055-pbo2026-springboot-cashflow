package org.delcom.ifs25055pbo2026springbootcashflow.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.delcom.ifs25055pbo2026springbootcashflow.adapter.repository.TransactionRepository;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.exception.ValidationException;
import org.junit.jupiter.api.Test;

class FinanceUseCaseTest {

    private final FinanceUseCase useCase = new FinanceUseCase(new TransactionRepository());

    private static BigDecimal rp(String value) {
        return new BigDecimal(value);
    }

    // --- tambah ---------------------------------------------------------

    @Test
    void addIncomeAndExpense() throws Exception {
        Transaction income = useCase.addIncome("  Gaji  ", rp("5000000"));
        Transaction expense = useCase.addExpense("Makan", rp("25000"));

        assertEquals("Gaji", income.getDescription());
        assertEquals(TransactionType.INCOME, income.getType());
        assertEquals(TransactionType.EXPENSE, expense.getType());
        assertEquals(2, useCase.count());
        assertEquals(2, useCase.findAll().size());
    }

    @Test
    void rejectsBlankOrNullDescription() {
        assertThrows(ValidationException.class, () -> useCase.addIncome(null, rp("1")));
        assertThrows(ValidationException.class, () -> useCase.addIncome("   ", rp("1")));
        assertThrows(ValidationException.class, () -> useCase.addExpense("", rp("1")));
        assertEquals(0, useCase.count());
    }

    @Test
    void rejectsNullZeroOrNegativeAmount() {
        assertThrows(ValidationException.class, () -> useCase.addIncome("x", null));
        assertThrows(ValidationException.class, () -> useCase.addIncome("x", BigDecimal.ZERO));
        assertThrows(ValidationException.class, () -> useCase.addExpense("x", rp("-5")));
        assertEquals(0, useCase.count());
    }

    // --- parse jumlah ---------------------------------------------------

    @Test
    void parsesAmountToTwoDecimals() throws Exception {
        assertEquals(new BigDecimal("1000000.00"), useCase.parseAmount(" 1000000 "));
        assertEquals(new BigDecimal("12.35"), useCase.parseAmount("12.345"));
    }

    @Test
    void rejectsEmptyOrNonNumericAmountText() {
        assertThrows(ValidationException.class, () -> useCase.parseAmount(null));
        assertThrows(ValidationException.class, () -> useCase.parseAmount("  "));
        ValidationException e = assertThrows(ValidationException.class, () -> useCase.parseAmount("abc"));
        assertTrue(e.getMessage().contains("abc"));
    }

    // --- cari & urut ----------------------------------------------------

    private void seed() throws Exception {
        useCase.addIncome("Gaji Bulanan", rp("5000000"));   // id 1
        useCase.addExpense("Makan Siang", rp("25000"));     // id 2
        useCase.addExpense("Bensin", rp("100000"));         // id 3
        useCase.addIncome("Bonus", rp("1000000"));          // id 4
    }

    private static List<Integer> ids(List<Transaction> list) {
        return list.stream().map(Transaction::getId).toList();
    }

    @Test
    void searchIsCaseInsensitiveAndDoesNotChangeData() throws Exception {
        seed();

        assertEquals(List.of(1), ids(useCase.search("gaji")));
        assertEquals(List.of(2), ids(useCase.search("  MAKAN ")));
        assertEquals(List.of(), ids(useCase.search("tidak ada")));
        assertEquals(4, useCase.count());
    }

    @Test
    void emptyOrNullKeywordReturnsEverythingInInsertionOrder() throws Exception {
        seed();
        assertEquals(List.of(1, 2, 3, 4), ids(useCase.search("")));
        assertEquals(List.of(1, 2, 3, 4), ids(useCase.search(null)));
        assertEquals(List.of(1, 2, 3, 4), ids(useCase.search(null, null)));
    }

    @Test
    void sortsUsingSortOption() throws Exception {
        seed();

        assertEquals(List.of(2, 3, 4, 1), ids(useCase.sort(SortOption.AMOUNT_ASC)));
        assertEquals(List.of(1, 4, 3, 2), ids(useCase.sort(SortOption.AMOUNT_DESC)));
        assertEquals(List.of(1, 4, 2, 3), ids(useCase.sort(SortOption.INCOME_FIRST)));
        assertEquals(List.of(2, 3, 1, 4), ids(useCase.sort(SortOption.EXPENSE_FIRST)));
    }

    @Test
    void searchCanBeCombinedWithSort() throws Exception {
        seed();
        assertEquals(List.of(4, 3, 2), ids(useCase.search("s", SortOption.AMOUNT_DESC)));
    }

    // --- hapus ----------------------------------------------------------

    @Test
    void deleteRemovesTransactionAndReturnsIt() throws Exception {
        seed();
        Transaction deleted = useCase.delete(2);

        assertEquals("Makan Siang", deleted.getDescription());
        assertEquals(List.of(1, 3, 4), ids(useCase.findAll()));
    }

    @Test
    void deleteRejectsInvalidId() {
        assertThrows(ValidationException.class, () -> useCase.delete(0));
        assertThrows(ValidationException.class, () -> useCase.delete(-3));
    }

    @Test
    void deleteRejectsUnknownId() throws Exception {
        seed();
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> useCase.delete(99));
        assertEquals("Transaksi dengan ID 99 tidak ditemukan", e.getMessage());
        assertEquals(4, useCase.count());
    }

    // --- saldo ----------------------------------------------------------

    @Test
    void balanceIsIncomeMinusExpenseAndStaysConsistent() throws Exception {
        assertEquals(BigDecimal.ZERO, useCase.getBalance());

        seed();
        assertEquals(rp("6000000"), useCase.getTotalIncome());
        assertEquals(rp("125000"), useCase.getTotalExpense());
        assertEquals(rp("5875000"), useCase.getBalance());

        useCase.delete(1); // hapus pemasukan 5.000.000
        assertEquals(rp("1000000"), useCase.getTotalIncome());
        assertEquals(rp("875000"), useCase.getBalance());

        useCase.addExpense("Listrik", rp("1000000"));
        assertEquals(rp("-125000"), useCase.getBalance());
    }
}

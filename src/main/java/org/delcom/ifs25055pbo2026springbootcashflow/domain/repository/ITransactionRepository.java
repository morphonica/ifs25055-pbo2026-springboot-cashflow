package org.delcom.ifs25055pbo2026springbootcashflow.domain.repository;

import java.math.BigDecimal;

import org.delcom.ifs25055pbo2026springbootcashflow.domain.entity.Transaction;

/**
 * Port penyimpanan transaksi. ID dibuat otomatis oleh implementasi.
 */
public interface ITransactionRepository extends IRepository<Transaction, Integer> {

    /** Membuat dan menyimpan transaksi pemasukan baru. */
    Transaction saveIncome(String description, BigDecimal amount);

    /** Membuat dan menyimpan transaksi pengeluaran baru. */
    Transaction saveExpense(String description, BigDecimal amount);
}

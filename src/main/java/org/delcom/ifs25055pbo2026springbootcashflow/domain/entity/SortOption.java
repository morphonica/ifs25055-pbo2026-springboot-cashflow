package org.delcom.ifs25055pbo2026springbootcashflow.domain.entity;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * Pilihan pengurutan transaksi. Setiap opsi membawa Comparator-nya sendiri
 * sehingga aturan urut tidak tersebar di layer lain. Seri diselesaikan
 * dengan ID agar urutan selalu stabil.
 */
public enum SortOption {

    AMOUNT_ASC("Jumlah terkecil",
            Comparator.<Transaction, BigDecimal>comparing(Transaction::getAmount)
                    .thenComparingInt(Transaction::getId)),

    AMOUNT_DESC("Jumlah terbesar",
            Comparator.<Transaction, BigDecimal>comparing(Transaction::getAmount, Comparator.reverseOrder())
                    .thenComparingInt(Transaction::getId)),

    INCOME_FIRST("Pemasukan dulu",
            Comparator.<Transaction, TransactionType>comparing(Transaction::getType)
                    .thenComparingInt(Transaction::getId)),

    EXPENSE_FIRST("Pengeluaran dulu",
            Comparator.<Transaction, TransactionType>comparing(Transaction::getType, Comparator.reverseOrder())
                    .thenComparingInt(Transaction::getId));

    private final String label;
    private final Comparator<Transaction> comparator;

    SortOption(String label, Comparator<Transaction> comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    public String getLabel() {
        return label;
    }

    public Comparator<Transaction> getComparator() {
        return comparator;
    }
}

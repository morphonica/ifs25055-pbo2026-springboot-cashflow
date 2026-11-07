package org.delcom.ifs25055pbo2026springbootcashflow.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity inti yang merepresentasikan satu transaksi keuangan.
 * Berada di layer domain: bebas dari urusan tampilan maupun penyimpanan.
 */
public class Transaction {

    /** ID unik transaksi, tidak boleh diubah setelah dibuat. */
    private final int id;

    private String description;

    private BigDecimal amount;

    private final TransactionType type;

    /** Waktu transaksi dicatat. */
    private final LocalDateTime createdAt;

    /** Membuat transaksi yang dicatat pada waktu sekarang. */
    public Transaction(int id, String description, BigDecimal amount, TransactionType type) {
        this(id, description, amount, type, LocalDateTime.now());
    }

    /** Membuat transaksi dengan waktu pencatatan yang sudah ditentukan. */
    public Transaction(int id, String description, BigDecimal amount, TransactionType type,
                       LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** Mengganti keterangan transaksi. */
    public void changeDescription(String newDescription) {
        this.description = newDescription;
    }

    /** Mengganti jumlah transaksi. */
    public void changeAmount(BigDecimal newAmount) {
        this.amount = newAmount;
    }

    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    /**
     * Pengaruh transaksi terhadap saldo: positif untuk pemasukan,
     * negatif untuk pengeluaran.
     */
    public BigDecimal signedAmount() {
        return isIncome() ? amount : amount.negate();
    }
}

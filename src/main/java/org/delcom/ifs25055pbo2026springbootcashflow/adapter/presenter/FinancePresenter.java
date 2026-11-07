package org.delcom.ifs25055pbo2026springbootcashflow.adapter.presenter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.delcom.ifs25055pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.exception.DomainException;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.exception.ValidationException;

/**
 * Mengubah data domain menjadi teks siap tampil. Format tampilan
 * sengaja dipisah dari entity.
 */
public class FinancePresenter {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    /** Contoh: 1000000 menjadi "Rp 1,000,000.00". */
    public String formatMoney(BigDecimal amount) {
        return String.format(Locale.US, "Rp %,.2f", amount);
    }

    public String formatType(TransactionType type) {
        return type == TransactionType.INCOME ? "Pemasukan" : "Pengeluaran";
    }

    /** Contoh: "[1] Gaji | Rp 5,000,000.00 | [Pemasukan]". */
    public String formatTransaction(Transaction transaction) {
        return "[" + transaction.getId() + "] " + transaction.getDescription()
                + " | " + formatMoney(transaction.getAmount())
                + " | [" + formatType(transaction.getType()) + "]";
    }

    /** Contoh: "Saldo: Rp 4,975,000.00". */
    public String formatBalance(BigDecimal balance) {
        return "Saldo: " + formatMoney(balance);
    }

    public String formatDateTime(LocalDateTime dateTime) {
        return DATE_TIME.format(dateTime);
    }

    public String messageAdded(Transaction transaction) {
        return formatType(transaction.getType()) + " berhasil ditambahkan: " + formatTransaction(transaction);
    }

    public String messageDeleted(Transaction transaction) {
        return "Transaksi berhasil dihapus: " + formatTransaction(transaction);
    }

    public String confirmDeleteMessage(Transaction transaction) {
        return "Hapus transaksi " + formatTransaction(transaction) + "? Tindakan ini tidak dapat dibatalkan.";
    }

    /** Pesan galat yang jelas untuk pengguna, sesuai jenis exception domain. */
    public String formatError(DomainException exception) {
        if (exception instanceof ValidationException) {
            return "Validasi gagal: " + exception.getMessage();
        }
        if (exception instanceof EntityNotFoundException) {
            return "Data tidak ditemukan: " + exception.getMessage();
        }
        return "Terjadi kesalahan: " + exception.getMessage();
    }
}

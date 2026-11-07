package org.delcom.ifs25055pbo2026springbootcashflow.adapter.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.delcom.ifs25055pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.exception.DomainException;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.exception.ValidationException;
import org.junit.jupiter.api.Test;

class FinancePresenterTest {

    private final FinancePresenter presenter = new FinancePresenter();

    private static Transaction income() {
        return new Transaction(1, "Gaji", new BigDecimal("1000000"), TransactionType.INCOME);
    }

    private static Transaction expense() {
        return new Transaction(2, "Makan", new BigDecimal("25000.5"), TransactionType.EXPENSE);
    }

    @Test
    void formatsMoneyWithThousandsSeparatorAndTwoDecimals() {
        assertEquals("Rp 1,000,000.00", presenter.formatMoney(new BigDecimal("1000000")));
        assertEquals("Rp 25,000.50", presenter.formatMoney(new BigDecimal("25000.5")));
        assertEquals("Rp 0.00", presenter.formatMoney(BigDecimal.ZERO));
    }

    @Test
    void formatsType() {
        assertEquals("Pemasukan", presenter.formatType(TransactionType.INCOME));
        assertEquals("Pengeluaran", presenter.formatType(TransactionType.EXPENSE));
    }

    @Test
    void formatsTransactionLine() {
        assertEquals("[1] Gaji | Rp 1,000,000.00 | [Pemasukan]", presenter.formatTransaction(income()));
        assertEquals("[2] Makan | Rp 25,000.50 | [Pengeluaran]", presenter.formatTransaction(expense()));
    }

    @Test
    void formatsBalance() {
        assertEquals("Saldo: Rp 4,975,000.00", presenter.formatBalance(new BigDecimal("4975000")));
    }

    @Test
    void formatsDateTime() {
        assertEquals("02-01-2026 03:04", presenter.formatDateTime(LocalDateTime.of(2026, 1, 2, 3, 4)));
    }

    @Test
    void buildsSuccessAndConfirmationMessages() {
        assertEquals("Pemasukan berhasil ditambahkan: [1] Gaji | Rp 1,000,000.00 | [Pemasukan]",
                presenter.messageAdded(income()));
        assertEquals("Transaksi berhasil dihapus: [2] Makan | Rp 25,000.50 | [Pengeluaran]",
                presenter.messageDeleted(expense()));
        assertEquals("Hapus transaksi [1] Gaji | Rp 1,000,000.00 | [Pemasukan]? Tindakan ini tidak dapat dibatalkan.",
                presenter.confirmDeleteMessage(income()));
    }

    @Test
    void formatsErrorsByType() {
        assertEquals("Validasi gagal: x", presenter.formatError(new ValidationException("x")));
        assertEquals("Data tidak ditemukan: Transaksi dengan ID 9 tidak ditemukan",
                presenter.formatError(new EntityNotFoundException("Transaksi", 9)));
        assertEquals("Terjadi kesalahan: y", presenter.formatError(new DomainException("y")));
    }
}

package org.delcom.ifs25055pbo2026springbootcashflow.usecase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.delcom.ifs25055pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.repository.ITransactionRepository;

/**
 * Logika aplikasi pencatat keuangan. Tidak mengandung komponen UI
 * maupun System.out; kegagalan dilaporkan lewat checked domain exception.
 */
public class FinanceUseCase {

    private final ITransactionRepository repository;

    public FinanceUseCase(ITransactionRepository repository) {
        this.repository = repository;
    }

    /** Mengubah teks jumlah menjadi angka dengan dua digit desimal. */
    public BigDecimal parseAmount(String text) throws ValidationException {
        if (text == null || text.isBlank()) {
            throw new ValidationException("Jumlah tidak boleh kosong");
        }
        try {
            return new BigDecimal(text.trim()).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new ValidationException("Jumlah harus berupa angka: \"" + text.trim() + "\"");
        }
    }

    public Transaction addIncome(String description, BigDecimal amount) throws ValidationException {
        validate(description, amount);
        return repository.saveIncome(description.trim(), amount);
    }

    public Transaction addExpense(String description, BigDecimal amount) throws ValidationException {
        validate(description, amount);
        return repository.saveExpense(description.trim(), amount);
    }

    /** Seluruh transaksi sesuai urutan pencatatan. */
    public List<Transaction> findAll() {
        return repository.findAll();
    }

    /** Mencari berdasarkan keterangan (tanpa membedakan huruf besar/kecil). */
    public List<Transaction> search(String keyword) {
        return search(keyword, null);
    }

    /**
     * Mencari berdasarkan keterangan lalu mengurutkan hasilnya.
     * Data di repository tidak berubah.
     *
     * @param sortOption boleh null bila tidak perlu diurutkan
     */
    public List<Transaction> search(String keyword, SortOption sortOption) {
        String needle = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : repository.findAll()) {
            if (transaction.getDescription().toLowerCase(Locale.ROOT).contains(needle)) {
                result.add(transaction);
            }
        }
        if (sortOption != null) {
            result.sort(sortOption.getComparator());
        }
        return result;
    }

    /** Seluruh transaksi yang diurutkan sesuai opsi. */
    public List<Transaction> sort(SortOption sortOption) {
        return search("", sortOption);
    }

    /** Menghapus transaksi dan mengembalikan transaksi yang dihapus. */
    public Transaction delete(int id) throws ValidationException, EntityNotFoundException {
        if (id <= 0) {
            throw new ValidationException("ID tidak valid: " + id + " (harus bilangan bulat positif)");
        }
        Transaction transaction = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaksi", id));
        repository.deleteById(id);
        return transaction;
    }

    public int count() {
        return repository.count();
    }

    public BigDecimal getTotalIncome() {
        return totalOf(TransactionType.INCOME);
    }

    public BigDecimal getTotalExpense() {
        return totalOf(TransactionType.EXPENSE);
    }

    /** Saldo = total pemasukan - total pengeluaran. */
    public BigDecimal getBalance() {
        BigDecimal balance = BigDecimal.ZERO;
        for (Transaction transaction : repository.findAll()) {
            balance = balance.add(transaction.signedAmount());
        }
        return balance;
    }

    private BigDecimal totalOf(TransactionType type) {
        BigDecimal total = BigDecimal.ZERO;
        for (Transaction transaction : repository.findAll()) {
            if (transaction.getType() == type) {
                total = total.add(transaction.getAmount());
            }
        }
        return total;
    }

    private void validate(String description, BigDecimal amount) throws ValidationException {
        if (description == null || description.isBlank()) {
            throw new ValidationException("Keterangan tidak boleh kosong");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new ValidationException("Jumlah harus lebih besar dari nol");
        }
    }
}

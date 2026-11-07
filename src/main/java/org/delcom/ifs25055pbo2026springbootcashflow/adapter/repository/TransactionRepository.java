package org.delcom.ifs25055pbo2026springbootcashflow.adapter.repository;

import java.math.BigDecimal;

import org.delcom.ifs25055pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.repository.ITransactionRepository;

/**
 * Penyimpanan transaksi di memori dengan ID otomatis (1, 2, 3, ...).
 */
public class TransactionRepository extends InMemoryRepository<Transaction, Integer>
        implements ITransactionRepository {

    private int nextId = 1;

    public TransactionRepository() {
        super(Transaction::getId);
    }

    @Override
    public Transaction saveIncome(String description, BigDecimal amount) {
        return create(description, amount, TransactionType.INCOME);
    }

    @Override
    public Transaction saveExpense(String description, BigDecimal amount) {
        return create(description, amount, TransactionType.EXPENSE);
    }

    private Transaction create(String description, BigDecimal amount, TransactionType type) {
        return save(new Transaction(nextId++, description, amount, type));
    }
}

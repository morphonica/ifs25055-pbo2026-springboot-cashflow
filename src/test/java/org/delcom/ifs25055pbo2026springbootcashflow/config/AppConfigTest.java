package org.delcom.ifs25055pbo2026springbootcashflow.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.delcom.ifs25055pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25055pbo2026springbootcashflow.adapter.repository.TransactionRepository;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.repository.ITransactionRepository;
import org.delcom.ifs25055pbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.Test;

class AppConfigTest {

    private final AppConfig config = new AppConfig();

    @Test
    void createsRepositoryUseCaseAndPresenter() {
        ITransactionRepository repository = config.transactionRepository();
        assertTrue(repository instanceof TransactionRepository);

        FinanceUseCase useCase = config.financeUseCase(repository);
        assertNotNull(useCase);

        FinancePresenter presenter = config.financePresenter();
        assertNotNull(presenter);
    }
}

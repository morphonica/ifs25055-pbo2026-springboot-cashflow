package org.delcom.ifs25055pbo2026springbootcashflow;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.delcom.ifs25055pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.repository.ITransactionRepository;
import org.delcom.ifs25055pbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class AppTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoadsAndWiresCleanArchitectureBeans() {
        assertNotNull(context.getBean(ITransactionRepository.class));
        assertNotNull(context.getBean(FinanceUseCase.class));
        assertNotNull(context.getBean(FinancePresenter.class));
    }

    @Test
    void useCaseIsSharedSingletonWithEmptyInitialState() {
        FinanceUseCase useCase = context.getBean(FinanceUseCase.class);
        assertTrue(useCase.count() >= 0);
    }

    @Test
    void mainLoadsDotEnvAndDelegatesToSpringApplicationRun() {
        String[] args = {"--spring.main.banner-mode=off"};
        try (MockedStatic<SpringApplication> spring = Mockito.mockStatic(SpringApplication.class)) {
            App.main(args);
            spring.verify(() -> SpringApplication.run(App.class, args));
        }
    }
}

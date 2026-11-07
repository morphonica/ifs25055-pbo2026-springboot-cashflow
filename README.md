# ifs25055-pbo2026-springboot-cashflow

Aplikasi web pencatat keuangan pribadi (Cashflow) dengan Clean Architecture, Java Generics,
checked domain exception, Spring Boot 4.1.2, dan Vaadin 25.3.4. Data disimpan di memori
(hilang saat aplikasi di-restart).

Package: `org.delcom.ifs25055pbo2026springbootcashflow`

## Menjalankan
```
.\mvnw.cmd spring-boot:run          # Windows
./mvnw spring-boot:run              # Linux / macOS
```
Buka http://localhost:8080 (port dari `APP_PORT` di `.env`, default 8080).

Build JAR produksi: `.\mvnw.cmd clean package -Pproduction` lalu
`java -jar target\ifs25055-pbo2026-springboot-cashflow-0.0.1-SNAPSHOT.jar`.

Tes + Jacoco: `.\mvnw.cmd clean verify` (laporan: `target/site/jacoco/index.html`).

## Struktur
- `domain/entity` Transaction, TransactionType, SortOption
- `domain/exception` DomainException, EntityNotFoundException, ValidationException
- `domain/repository` IRepository<T, ID>, ITransactionRepository
- `usecase` FinanceUseCase
- `adapter/repository` InMemoryRepository<T, ID>, TransactionRepository
- `adapter/presenter` FinancePresenter
- `framework/view` FinanceView (Vaadin, `@Route("")`)
- `config` AppConfig (composition root)

package org.delcom.ifs25055pbo2026springbootcashflow.framework.view;

import java.math.BigDecimal;
import java.util.List;

import org.delcom.ifs25055pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25055pbo2026springbootcashflow.domain.exception.DomainException;
import org.delcom.ifs25055pbo2026springbootcashflow.usecase.FinanceUseCase;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Tampilan web Cashflow (Vaadin). Hanya mengurus komponen UI: seluruh aturan
 * bisnis ada di {@link FinanceUseCase} dan seluruh format teks di
 * {@link FinancePresenter}. Exception domain ditangkap di sini lalu
 * ditampilkan sebagai notifikasi.
 */
@Route("")
@PageTitle("Cashflow - Catatan Keuangan")
public class FinanceView extends VerticalLayout {

    private static final String SUCCESS_COLOR = "#166534";
    private static final String ERROR_COLOR = "#b91c1c";
    private static final String SECONDARY_COLOR = "#4b5563";
    private static final String WHITE = "#ffffff";

    private final FinanceUseCase useCase;
    private final FinancePresenter presenter;

    private final Grid<Transaction> grid = new Grid<>();
    private final TextField searchField = new TextField();
    private final ComboBox<SortOption> sortSelect = new ComboBox<>();

    private final Span countValue = new Span();
    private final Span incomeValue = new Span();
    private final Span expenseValue = new Span();
    private final Span balanceValue = new Span();

    public FinanceView(FinanceUseCase useCase, FinancePresenter presenter) {
        this.useCase = useCase;
        this.presenter = presenter;

        setSizeFull();
        // Landmark utama + bahasa dokumen (aksesibilitas / Lighthouse)
        getElement().setAttribute("role", "main");
        addAttachListener(event -> event.getUI().getPage()
                .executeJs("document.documentElement.lang = 'id';"));
        setPadding(true);
        setSpacing(true);

        add(new H1("Cashflow"), createSummaryCards(), createToolbar());
        configureGrid();
        add(grid);
        expand(grid);

        refresh();
    }

    // ------------------------------------------------------------------
    // Susunan komponen
    // ------------------------------------------------------------------

    private HorizontalLayout createSummaryCards() {
        incomeValue.getStyle().set("color", SUCCESS_COLOR);
        expenseValue.getStyle().set("color", ERROR_COLOR);

        HorizontalLayout cards = new HorizontalLayout(
                createCard("Total Transaksi", countValue),
                createCard("Total Pemasukan", incomeValue),
                createCard("Total Pengeluaran", expenseValue),
                createCard("Saldo Saat Ini", balanceValue));
        cards.setWidthFull();
        cards.getElement().setAttribute("role", "group");
        cards.getElement().setAttribute("aria-label", "Ringkasan keuangan");
        cards.getStyle().set("flex-wrap", "wrap");
        return cards;
    }

    private Div createCard(String title, Span value) {
        Span titleSpan = new Span(title);
        titleSpan.getStyle()
                .set("font-size", "var(--lumo-font-size-s, 0.875rem)")
                .set("color", SECONDARY_COLOR);
        value.getStyle()
                .set("font-size", "var(--lumo-font-size-xl, 1.5rem)")
                .set("font-weight", "600");

        Div card = new Div(titleSpan, value);
        card.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("flex", "1 1 200px")
                .set("padding", "var(--lumo-space-m, 1rem)")
                .set("border-radius", "var(--lumo-border-radius-l, 0.5rem)")
                .set("background", "var(--lumo-contrast-5pct, #f3f4f6)");
        return card;
    }

    private HorizontalLayout createToolbar() {
        searchField.setPlaceholder("Cari keterangan...");
        searchField.setAriaLabel("Cari keterangan transaksi");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(event -> refresh());

        sortSelect.setPlaceholder("Urutkan...");
        sortSelect.setAriaLabel("Urutkan transaksi");
        sortSelect.setItems(SortOption.values());
        sortSelect.setItemLabelGenerator(SortOption::getLabel);
        sortSelect.setClearButtonVisible(true);
        sortSelect.addValueChangeListener(event -> refresh());

        Button reload = new Button("Muat Ulang", VaadinIcon.REFRESH.create(), event -> refresh());

        Button addIncome = new Button("Tambah Pemasukan", VaadinIcon.PLUS.create(),
                event -> openAddDialog(TransactionType.INCOME));
        addIncome.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        addIncome.getStyle().set("background", SUCCESS_COLOR).set("color", WHITE);

        Button addExpense = new Button("Tambah Pengeluaran", VaadinIcon.MINUS.create(),
                event -> openAddDialog(TransactionType.EXPENSE));
        addExpense.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        addExpense.getStyle().set("background", ERROR_COLOR).set("color", WHITE);

        HorizontalLayout toolbar = new HorizontalLayout(searchField, sortSelect, reload, addIncome, addExpense);
        toolbar.setWidthFull();
        toolbar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        toolbar.getStyle().set("flex-wrap", "wrap");
        return toolbar;
    }

    private void configureGrid() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.getElement().setAttribute("aria-label", "Daftar transaksi");

        grid.addColumn(Transaction::getId)
                .setHeader("ID")
                .setSortable(true)
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(Transaction::getDescription)
                .setHeader("Keterangan")
                .setSortable(true)
                .setFlexGrow(1);

        grid.addColumn(transaction -> presenter.formatMoney(transaction.getAmount()))
                .setHeader("Jumlah")
                .setComparator(Transaction::getAmount)
                .setTextAlign(ColumnTextAlign.END)
                .setAutoWidth(true);

        grid.addComponentColumn(this::createTypeBadge)
                .setHeader("Tipe")
                .setComparator(Transaction::getType)
                .setAutoWidth(true);

        grid.addColumn(transaction -> presenter.formatDateTime(transaction.getCreatedAt()))
                .setHeader("Waktu")
                .setComparator(Transaction::getCreatedAt)
                .setAutoWidth(true);

        grid.addComponentColumn(this::createDeleteButton)
                .setHeader("Aksi")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.setSizeFull();
    }

    private Span createTypeBadge(Transaction transaction) {
        Span badge = new Span(presenter.formatType(transaction.getType()));
        badge.getStyle()
                .set("font-weight", "600")
                .set("color", transaction.isIncome() ? SUCCESS_COLOR : ERROR_COLOR);
        return badge;
    }

    private Button createDeleteButton(Transaction transaction) {
        Button delete = new Button(VaadinIcon.TRASH.create(), event -> confirmDelete(transaction));
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        delete.setAriaLabel("Hapus transaksi " + transaction.getId());
        return delete;
    }

    // ------------------------------------------------------------------
    // Aksi pengguna
    // ------------------------------------------------------------------

    private void openAddDialog(TransactionType type) {
        boolean income = type == TransactionType.INCOME;

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(income ? "Tambah Pemasukan" : "Tambah Pengeluaran");

        TextField description = new TextField("Keterangan");
        description.setWidthFull();
        description.setAutofocus(true);

        TextField amount = new TextField("Jumlah (Rp)");
        amount.setPlaceholder("contoh: 1000000");
        amount.setWidthFull();

        VerticalLayout form = new VerticalLayout(description, amount);
        form.setPadding(false);
        form.setMinWidth("320px");
        dialog.add(form);

        Button cancel = new Button("Batal", event -> dialog.close());
        Button save = new Button("Simpan", event -> {
            if (submit(type, description.getValue(), amount.getValue())) {
                dialog.close();
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        dialog.getFooter().add(cancel, save);

        dialog.open();
    }

    /** @return true bila transaksi berhasil disimpan. */
    private boolean submit(TransactionType type, String description, String amountText) {
        try {
            BigDecimal amount = useCase.parseAmount(amountText);
            Transaction saved = type == TransactionType.INCOME
                    ? useCase.addIncome(description, amount)
                    : useCase.addExpense(description, amount);
            showSuccess(presenter.messageAdded(saved) + ". " + currentBalanceText());
            refresh();
            return true;
        } catch (DomainException e) {
            showError(presenter.formatError(e));
            return false;
        }
    }

    private void confirmDelete(Transaction transaction) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Hapus Transaksi");
        dialog.setText(presenter.confirmDeleteMessage(transaction));
        dialog.setCancelable(true);
        dialog.setCancelText("Batal");
        dialog.setConfirmText("Hapus");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(event -> delete(transaction.getId()));
        dialog.open();
    }

    private void delete(int id) {
        try {
            Transaction deleted = useCase.delete(id);
            showSuccess(presenter.messageDeleted(deleted) + ". " + currentBalanceText());
        } catch (DomainException e) {
            showError(presenter.formatError(e));
        }
        refresh();
    }

    // ------------------------------------------------------------------
    // Penyegaran data
    // ------------------------------------------------------------------

    /** Memuat ulang grid (sesuai pencarian & urutan) dan kartu ringkasan. */
    private void refresh() {
        List<Transaction> items = useCase.search(searchField.getValue(), sortSelect.getValue());
        grid.setItems(items);

        countValue.setText(String.valueOf(useCase.count()));
        incomeValue.setText(presenter.formatMoney(useCase.getTotalIncome()));
        expenseValue.setText(presenter.formatMoney(useCase.getTotalExpense()));
        balanceValue.setText(presenter.formatMoney(useCase.getBalance()));
    }

    private String currentBalanceText() {
        return presenter.formatBalance(useCase.getBalance());
    }

    private void showSuccess(String message) {
        Notification notification = Notification.show(message, 4000, Notification.Position.BOTTOM_START);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.BOTTOM_START);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}

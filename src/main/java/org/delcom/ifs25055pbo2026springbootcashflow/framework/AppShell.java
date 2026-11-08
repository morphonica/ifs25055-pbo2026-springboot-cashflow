package org.delcom.ifs25055pbo2026springbootcashflow.framework;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Meta;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * Konfigurasi halaman (app shell) Vaadin: memuat tema Lumo (UI memakai
 * variabel --lumo-*) dan menambahkan meta description untuk SEO. Berada di
 * package framework sehingga dikecualikan dari Jacoco.
 */
@StyleSheet(Lumo.STYLESHEET)
@Meta(name = "description",
        content = "Cashflow: aplikasi web pencatat pemasukan dan pengeluaran keuangan pribadi beserta saldo saat ini.")
public class AppShell implements AppShellConfigurator {
}

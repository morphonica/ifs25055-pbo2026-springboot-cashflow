package org.delcom.ifs25055pbo2026springbootcashflow;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Memuat berkas .env (baris KEY=VALUE) ke system properties sebelum Spring
 * Boot dijalankan, sehingga placeholder seperti ${APP_PORT:8080} terisi.
 * Nilai yang sudah diset lewat system property / environment variable
 * tidak ditimpa.
 */
public final class DotEnv {

    private DotEnv() {
    }

    /** Memuat .env dari direktori kerja; tidak melakukan apa-apa bila berkas tidak ada. */
    public static void load() {
        load(Path.of(".env"));
    }

    static void load(Path file) {
        if (!Files.isRegularFile(file)) {
            return;
        }
        try {
            for (String line : Files.readAllLines(file)) {
                apply(line);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Gagal membaca " + file, e);
        }
    }

    static void apply(String rawLine) {
        String line = rawLine.trim();
        if (line.isEmpty() || line.startsWith("#")) {
            return;
        }
        int separator = line.indexOf('=');
        String key = separator < 0 ? "" : line.substring(0, separator).trim();
        if (key.isEmpty()) {
            return;
        }
        String value = unquote(line.substring(separator + 1).trim());
        if (System.getProperty(key) == null && System.getenv(key) == null) {
            System.setProperty(key, value);
        }
    }

    static String unquote(String value) {
        if (value.length() < 2) {
            return value;
        }
        char first = value.charAt(0);
        char last = value.charAt(value.length() - 1);
        boolean quoted = first == last && (first == '"' || first == '\'');
        return quoted ? value.substring(1, value.length() - 1) : value;
    }
}

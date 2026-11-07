package org.delcom.ifs25055pbo2026springbootcashflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DotEnvTest {

    private static final String[] KEYS = {"CF_TEST_A", "CF_TEST_B", "CF_TEST_C", "CF_TEST_D", "CF_TEST_E",
            "CF_TEST_F", "CF_TEST_G", "CF_TEST_H"};

    @AfterEach
    void clearProperties() {
        for (String key : KEYS) {
            System.clearProperty(key);
        }
    }

    @Test
    void loadsKeyValuePairsIgnoringCommentsAndBlankLines(@TempDir Path dir) throws IOException {
        Path env = dir.resolve(".env");
        Files.writeString(env, String.join("\n",
                "# komentar",
                "",
                "CF_TEST_A=8081",
                "  CF_TEST_B = spasi  ",
                "NO_SEPARATOR_LINE",
                "=tanpa-kunci",
                "CF_TEST_C=\"dalam kutip\"",
                "CF_TEST_D='kutip tunggal'"));

        DotEnv.load(env);

        assertEquals("8081", System.getProperty("CF_TEST_A"));
        assertEquals("spasi", System.getProperty("CF_TEST_B"));
        assertEquals("dalam kutip", System.getProperty("CF_TEST_C"));
        assertEquals("kutip tunggal", System.getProperty("CF_TEST_D"));
        assertNull(System.getProperty("NO_SEPARATOR_LINE"));
    }

    @Test
    void doesNotOverrideExistingSystemProperty(@TempDir Path dir) throws IOException {
        System.setProperty("CF_TEST_E", "asli");
        Path env = dir.resolve(".env");
        Files.writeString(env, "CF_TEST_E=baru\n");

        DotEnv.load(env);

        assertEquals("asli", System.getProperty("CF_TEST_E"));
    }

    @Test
    void doesNotOverrideEnvironmentVariable() {
        // PATH selalu ada sebagai environment variable
        String before = System.getProperty("PATH");
        DotEnv.apply("PATH=nilai-palsu");
        assertEquals(before, System.getProperty("PATH"));
    }

    @Test
    void missingFileIsIgnored(@TempDir Path dir) {
        DotEnv.load(dir.resolve("tidak-ada.env"));
        assertNull(System.getProperty("CF_TEST_F"));
    }

    @Test
    void unreadableFileThrowsUncheckedIOException(@TempDir Path dir) throws IOException {
        Path env = dir.resolve(".env");
        Files.write(env, new byte[] {(byte) 0xC3, (byte) 0x28, (byte) 0xFF, (byte) 0xFE});

        assertThrows(UncheckedIOException.class, () -> DotEnv.load(env));
    }

    @Test
    void defaultLoadUsesWorkingDirectoryAndDoesNotFail() {
        // .env boleh ada atau tidak; yang penting tidak melempar exception
        assertDoesNotThrow(() -> {
            DotEnv.load();
        });
    }

    @Test
    void unquoteHandlesEdgeCases() {
        assertEquals("", DotEnv.unquote(""));
        assertEquals("x", DotEnv.unquote("x"));
        assertEquals("aa", DotEnv.unquote("aa"));
        assertEquals("plain", DotEnv.unquote("plain"));
        assertEquals("x", DotEnv.unquote("\"x\""));
        assertEquals("y", DotEnv.unquote("'y'"));
        assertEquals("\"x'", DotEnv.unquote("\"x'"));
    }
}

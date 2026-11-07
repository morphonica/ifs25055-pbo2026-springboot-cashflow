package org.delcom.ifs25055pbo2026springbootcashflow.adapter.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class InMemoryRepositoryTest {

    private final InMemoryRepository<String, Integer> repository = new InMemoryRepository<>(String::length);

    @Test
    void saveAndFindById() {
        assertSame("abc", repository.save("abc"));
        assertEquals("abc", repository.findById(3).orElseThrow());
        assertTrue(repository.findById(99).isEmpty());
    }

    @Test
    void saveWithSameIdReplacesEntity() {
        repository.save("abc");
        repository.save("xyz");
        assertEquals(1, repository.count());
        assertEquals("xyz", repository.findById(3).orElseThrow());
    }

    @Test
    void findAllReturnsDefensiveCopyInInsertionOrder() {
        repository.save("a");
        repository.save("bb");

        List<String> copy = repository.findAll();
        assertEquals(List.of("a", "bb"), copy);

        copy.clear();
        assertEquals(2, repository.findAll().size());
    }

    @Test
    void deleteById() {
        repository.save("a");
        assertTrue(repository.deleteById(1));
        assertFalse(repository.deleteById(1));
        assertEquals(0, repository.count());
    }
}

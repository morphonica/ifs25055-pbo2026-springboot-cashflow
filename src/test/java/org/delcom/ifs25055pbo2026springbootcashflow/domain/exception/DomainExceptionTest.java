package org.delcom.ifs25055pbo2026springbootcashflow.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

class DomainExceptionTest {

    @Test
    void domainExceptionKeepsMessage() {
        assertEquals("boom", new DomainException("boom").getMessage());
    }

    @Test
    void validationExceptionIsDomainException() {
        ValidationException e = new ValidationException("salah");
        assertInstanceOf(DomainException.class, e);
        assertEquals("salah", e.getMessage());
    }

    @Test
    void entityNotFoundExceptionBuildsMessageFromIdAndName() {
        EntityNotFoundException e = new EntityNotFoundException("Transaksi", 5);
        assertInstanceOf(DomainException.class, e);
        assertEquals("Transaksi dengan ID 5 tidak ditemukan", e.getMessage());
    }
}

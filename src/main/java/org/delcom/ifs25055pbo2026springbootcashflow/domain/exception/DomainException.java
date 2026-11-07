package org.delcom.ifs25055pbo2026springbootcashflow.domain.exception;

/**
 * Checked exception dasar untuk seluruh kegagalan aturan domain.
 */
public class DomainException extends Exception {

    public DomainException(String message) {
        super(message);
    }
}

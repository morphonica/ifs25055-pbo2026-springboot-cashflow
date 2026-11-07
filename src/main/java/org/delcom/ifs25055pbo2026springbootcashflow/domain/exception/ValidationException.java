package org.delcom.ifs25055pbo2026springbootcashflow.domain.exception;

/**
 * Dilempar ketika input melanggar aturan validasi bisnis.
 */
public class ValidationException extends DomainException {

    public ValidationException(String message) {
        super(message);
    }
}

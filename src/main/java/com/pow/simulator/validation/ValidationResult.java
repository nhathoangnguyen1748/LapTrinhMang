package com.pow.simulator.validation;

import java.util.Collections;
import java.util.List;

/**
 * Result record containing comprehensive audit diagnostics of blockchain validation.
 *
 * @param isValid           true if blockchain satisfies all cryptographic and linkage rules
 * @param failingBlockIndex zero-based index of the first invalid block (-1 if valid)
 * @param failureReason     human-readable diagnosis of failure (empty if valid)
 * @param auditLogs         detailed step-by-step verification traces
 */
public record ValidationResult(
        boolean isValid,
        int failingBlockIndex,
        String failureReason,
        List<String> auditLogs
) {
    public ValidationResult {
        auditLogs = (auditLogs == null) ? List.of() : List.copyOf(auditLogs);
    }

    public static ValidationResult valid(List<String> logs) {
        return new ValidationResult(true, -1, "All cryptographic checks passed successfully", logs);
    }

    public static ValidationResult invalid(int blockIndex, String reason, List<String> logs) {
        return new ValidationResult(false, blockIndex, reason, logs);
    }

    @Override
    public String toString() {
        if (isValid) {
            return "[VALIDATION PASS] Blockchain is cryptographically sound and unbroken.";
        } else {
            return String.format("[VALIDATION FAILED] At Block #%d: %s", failingBlockIndex, failureReason);
        }
    }
}

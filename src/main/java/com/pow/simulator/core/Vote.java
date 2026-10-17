package com.pow.simulator.core;

import com.pow.simulator.crypto.HashUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Phiếu bầu cử trong hệ thống E-Voting Blockchain.
 * Lưu trữ trong Mempool khi vừa bầu, và được đóng vào Sổ Cái (Ledger).
 */
public record Vote(
        String voteId,
        String voterId,      // Mã / Tên cử tri (VD: "SV001 - Nguyễn Hoàng Nhật")
        String candidate,    // Ứng cử viên được bầu (VD: "Ứng cử viên 1 - Lê Quang Minh")
        long timestamp
) {
    public Vote {
        Objects.requireNonNull(voterId, "Mã cử tri không được để trống");
        Objects.requireNonNull(candidate, "Ứng cử viên không được để trống");
        voterId = voterId.trim();
        candidate = candidate.trim();
        if (voteId == null || voteId.isBlank()) {
            voteId = HashUtil.sha256Hex(voterId + ":" + candidate + ":" + timestamp).substring(0, 8);
        }
    }

    public Vote(String voterId, String candidate) {
        this("", voterId, candidate, System.currentTimeMillis());
    }

    public String timeFormatted() {
        return DateTimeFormatter.ofPattern("HH:mm:ss dd/MM")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(timestamp));
    }

    public String toText() {
        return String.format("Cử tri [%s] bầu cho [%s]", voterId, candidate);
    }
}

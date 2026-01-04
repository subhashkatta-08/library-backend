package com.nit.dto;

import lombok.Data;

@Data
public class AdminStatsDto {
    private long totalBooks;
    private long totalIssued;
    private long totalPending;
    private long totalOverdue;
}


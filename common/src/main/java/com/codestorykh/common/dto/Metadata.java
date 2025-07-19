package com.codestorykh.common.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Metadata {
    private boolean hasNext;
    private long totalUsers;
    private boolean hasPrevious;
    private int currentPage;
    private int pageSize;
}
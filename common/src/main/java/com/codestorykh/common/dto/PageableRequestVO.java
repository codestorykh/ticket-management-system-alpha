package com.codestorykh.common.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.domain.Sort;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageableRequestVO {


    @Min(value = 0, message = "Page number must be greater than or equal to 0")
    private int pageNumber;

    @Min(value = 1, message = "Page size must be greater than 0")
    private int pageSize;

    private String sortBy;
    private boolean desc;

    public int getOffset() {
        return pageNumber * pageSize;
    }

    public boolean hasSorting() {
        return sortBy != null && !sortBy.trim().isEmpty();
    }

    public Sort.Direction getSortDirection() {
        return desc ? Sort.Direction.DESC : Sort.Direction.ASC;
    }

    public Sort getSort() {
        if (!hasSorting()) {
            return Sort.unsorted();
        }
        return Sort.by(getSortDirection(), sortBy);
    }

    public static PageableRequestVO of(int pageNumber, int pageSize) {
        return PageableRequestVO.builder()
            .pageNumber(pageNumber)
            .pageSize(pageSize)
            .build();
    }

    public static PageableRequestVO of(int pageNumber, int pageSize, String sortBy, boolean desc) {
        return PageableRequestVO.builder()
            .pageNumber(pageNumber)
            .pageSize(pageSize)
            .sortBy(sortBy)
            .desc(desc)
            .build();
    }

    public static PageableRequestVO firstPage(int pageSize) {
        return of(0, pageSize);
    }

    public static PageableRequestVO firstPage(int pageSize, String sortBy, boolean desc) {
        return of(0, pageSize, sortBy, desc);
    }

    public PageableRequestVO nextPage() {
        return PageableRequestVO.builder()
            .pageNumber(pageNumber + 1)
            .pageSize(pageSize)
            .sortBy(sortBy)
            .desc(desc)
            .build();
    }

    public PageableRequestVO previousPage() {
        if (pageNumber <= 0) {
            return this;
        }
        return PageableRequestVO.builder()
            .pageNumber(pageNumber - 1)
            .pageSize(pageSize)
            .sortBy(sortBy)
            .desc(desc)
            .build();
    }

    public PageableRequestVO withSort(String sortBy, boolean desc) {
        return PageableRequestVO.builder()
            .pageNumber(pageNumber)
            .pageSize(pageSize)
            .sortBy(sortBy)
            .desc(desc)
            .build();
    }

    public PageableRequestVO withPageSize(int newPageSize) {
        return PageableRequestVO.builder()
            .pageNumber(pageNumber)
            .pageSize(newPageSize)
            .sortBy(sortBy)
            .desc(desc)
            .build();
    }
}

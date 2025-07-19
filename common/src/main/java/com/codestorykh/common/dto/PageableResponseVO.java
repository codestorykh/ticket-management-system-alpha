package com.codestorykh.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageableResponseVO<T> {
    private int totalElements;
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean empty;
    private Metadata metadata;

    public static <T> PageableResponseVO<T> of(List<T> content, int totalElements, int pageNumber, int pageSize) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        boolean isLast = pageNumber >= totalPages - 1;
        boolean isFirst = pageNumber == 0;
        
        Metadata metadata = Metadata.builder()
            .hasNext(!isLast && !content.isEmpty())
            .totalUsers(totalElements)
            .hasPrevious(!isFirst && !content.isEmpty())
            .currentPage(pageNumber)
            .pageSize(pageSize)
            .build();

        return PageableResponseVO.<T>builder()
            .totalElements(totalElements)
            .content(content)
            .pageNumber(pageNumber)
            .pageSize(pageSize)
            .totalPages(totalPages)
            .first(isFirst)
            .last(isLast)
            .empty(content == null || content.isEmpty())
            .metadata(metadata)
            .build();
    }

    public static <T> PageableResponseVO<T> empty() {
        Metadata metadata = Metadata.builder()
            .hasNext(false)
            .totalUsers(0)
            .hasPrevious(false)
            .currentPage(0)
            .pageSize(0)
            .build();

        return PageableResponseVO.<T>builder()
            .totalElements(0)
            .content(List.of())
            .pageNumber(0)
            .pageSize(0)
            .totalPages(0)
            .first(true)
            .last(true)
            .empty(true)
            .metadata(metadata)
            .build();
    }

    public boolean hasNext() {
        return !last && !empty;
    }

    public boolean hasPrevious() {
        return !first && !empty;
    }

    public int getNextPageNumber() {
        return hasNext() ? pageNumber + 1 : pageNumber;
    }

    public int getPreviousPageNumber() {
        return hasPrevious() ? pageNumber - 1 : pageNumber;
    }

    public int getNumberOfElements() {
        return content != null ? content.size() : 0;
    }

    public boolean hasContent() {
        return !empty;
    }
}




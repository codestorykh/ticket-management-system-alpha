package com.codestorykh.user.service.handler;

import com.codestorykh.common.dto.PageableResponseVO;
import com.codestorykh.common.dto.PaginationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageableResponseHandlerService {

    public <T> PaginationResponse handlePaginationResponse(PageableResponseVO<T> pageResponse) {
        try {
            if (pageResponse == null) {
                log.warn("PageableResponseVO is null");
                return new PaginationResponse();
            }

            PaginationResponse paginationResponse = new PaginationResponse();
            paginationResponse.setPaginationResponse(
                pageResponse.getTotalPages(),
                pageResponse.getPageNumber(),
                pageResponse.getPageSize(),
                pageResponse.getTotalElements(),
                pageResponse.getNumberOfElements()
            );

            return paginationResponse;
        } catch (Exception e) {
            log.error("Error handling pagination response: {}", e.getMessage());
            throw e;
        }
    }

    public PaginationResponse handlePaginationResponse(int totalElements, int pageNumber, int pageSize) {
        try {
            int totalPages = calculateTotalPages(totalElements, pageSize);
            
            PaginationResponse paginationResponse = new PaginationResponse();
            paginationResponse.setPaginationResponse(
                totalPages,
                pageNumber,
                pageSize,
                totalElements,
                Math.min(pageSize, totalElements)
            );

            return paginationResponse;
        } catch (Exception e) {
            log.error("Error handling pagination response: {}", e.getMessage());
            throw e;
        }
    }

    private int calculateTotalPages(int totalElements, int pageSize) {
        if (pageSize <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalElements / pageSize);
    }
}

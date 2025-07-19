package com.codestorykh.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;

@Setter
public class PaginationResponse {

    @JsonProperty("total_pages")
    private int totalPage;

    @JsonProperty("current")
    private int current;

    @JsonProperty("size")
    private int size;

    @JsonProperty("records")
    private long records;

    @JsonProperty("number_of_element")
    private int numberOfElement;

    public void setPaginationResponse(int totalPage, int current, int size, long records, int numberOfElement) {
        this.totalPage = totalPage;
        this.current = current;
        this.size = size;
        this.records = records;
        this.numberOfElement = numberOfElement;
    }
}

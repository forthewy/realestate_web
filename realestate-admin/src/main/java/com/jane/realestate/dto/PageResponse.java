package com.jane.realestate.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PageResponse<T> {

    private List<T> items;
    private int pageNo;
    private int pageSize;
    private long totalCount;
}
package com.findit.dto;

import org.springframework.data.domain.Page;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PageResponse<T> {
    private final List<T> data;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public <S> PageResponse(Page<S> source, Function<S, T> mapper) {
        data = source.getContent().stream().map(mapper).collect(Collectors.toList());
        page = source.getNumber();
        size = source.getSize();
        totalElements = source.getTotalElements();
        totalPages = source.getTotalPages();
    }

    public List<T> getData() { return data; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
}

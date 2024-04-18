package org.daemawiki.utils;

public record PagingInfo(
        String sortBy,
        Integer sortDirection,
        Integer page,
        Integer size
) {

    public PagingInfo(String sortBy, String sortDirection, Integer page, Integer size) {
        this(sortBy, sortDirection.equalsIgnoreCase(SortDirection.DESC.name()) ? -1 : 1, page, size);
    }

    enum SortDirection {
        DESC,
        ASC
    }

}

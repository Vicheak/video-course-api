package com.vicheak.coreapp.pagination;

import lombok.Builder;

@Builder
public record PaginationDto(int pageNumber,
                            int pageSize,
                            int totalPage,
                            long totalElement,
                            int numberOfElement,
                            boolean first,
                            boolean last,
                            boolean empty) {
}
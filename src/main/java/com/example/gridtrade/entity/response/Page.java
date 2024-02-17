package com.example.gridtrade.entity.response;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;


@Data
public class Page<DATA> {
    private Integer showCount;
    private Integer totalPage;
    private Integer totalResult;
    private Integer currentPage;
    private Integer currentResult;
    private List<DATA> resultList;

    public boolean hasMore() {
        return currentPage < totalPage;
    }

    public Integer endOffset() {
        return (currentPage - 1) * showCount + (CollectionUtils.isNotEmpty(resultList) ? resultList.size() : 0);
    }
}

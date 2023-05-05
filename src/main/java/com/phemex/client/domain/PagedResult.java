package com.phemex.client.domain;

import lombok.Data;

import java.util.List;

@Data
public class PagedResult<T> {

    private long total;

    private List<T> rows;

}

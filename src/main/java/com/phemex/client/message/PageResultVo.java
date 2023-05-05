package com.phemex.client.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResultVo {

    private Object rows;

    private String nextPageArg;

    static public PageResultVo of(Object arr, String pageArg) {
        return PageResultVo.builder()
                .rows(arr)
                .nextPageArg(pageArg)
                .build();
    }
}

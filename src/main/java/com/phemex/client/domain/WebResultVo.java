package com.phemex.client.domain;

import lombok.Data;

import java.util.List;

@Data
public class WebResultVo {
    private List<OrderModelVo> rows;
}

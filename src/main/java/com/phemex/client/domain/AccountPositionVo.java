package com.phemex.client.domain;

import lombok.Data;

import java.util.List;

@Data
public class AccountPositionVo {

    private AccountVo account;

    private List<PositionVo> positions;

}

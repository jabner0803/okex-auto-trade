package com.phemex.client.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GAccountModelVo {

    private long userID;

    private long accountId;

    private String currency;

    private String accountBalanceRv;

    private String totalUsedBalanceRv;

    private String bonusBalanceRv;
}

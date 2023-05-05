package com.phemex.client.domain.market.response;

import com.phemex.client.constant.PhemexServerEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeBaseResponse {

    private PhemexServerEventType eventType;

    private Object data;

}

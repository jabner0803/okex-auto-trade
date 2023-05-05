package com.phemex.client.service;

import com.phemex.client.service.ctx.GrayTradingContext;

public interface PositionService {

    void closePosition(GrayTradingContext context, int moveDirection);

    void adjustPositions(GrayTradingContext context);
}

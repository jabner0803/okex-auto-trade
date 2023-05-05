package com.phemex.client.service;

import com.phemex.client.constant.OrdType;
import com.phemex.client.constant.Side;
import com.phemex.client.domain.GNewOrderModelVo;
import com.phemex.client.domain.GOrderModelVo;
import com.phemex.client.service.ctx.GrayTradingContext;

import java.util.List;

public interface OrderService {

    void checkAndCreateOrder(GrayTradingContext context, long price, long orderQty, Side side, String posSide, OrdType ordType, boolean closeOnTrigger);

    void cancelOrder(GOrderModelVo order);

    List<GOrderModelVo> searchAndDropOverdueOrders(String symbol, long startTimeNs);

    List<GOrderModelVo> queryAllActiveOrders(String symbol);

    List<GNewOrderModelVo> queryFilledOrders(String symbol, String baseCurrency, int ordStatus, long startTimeMills, long latestUpdateTimeMills);
}

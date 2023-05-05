package com.phemex.client.service.impl;

import com.phemex.client.constant.OrdType;
import com.phemex.client.constant.PosSide;
import com.phemex.client.constant.Side;
import com.phemex.client.domain.GAccountPositionVo;
import com.phemex.client.domain.GPositionVo;
import com.phemex.client.prop.GrayProp;
import com.phemex.client.service.OrderService;
import com.phemex.client.service.PhemexClient;
import com.phemex.client.service.PositionService;
import com.phemex.client.service.ctx.GrayTradingContext;
import com.phemex.client.utils.ConvertorUtils;
import com.phemex.client.utils.PriceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.phemex.client.utils.PriceUtils.PRICE_MOVEMENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PhemexClient phemexClient;

    private final GrayProp grayProp;

    private final OrderService orderService;

    /**
     * 关闭仓位
     * if moveDirection < 0 , 价格下跌，关闭买多的position
     * if moveDirection > 0 , 价格上涨，关闭看空的position
     * @param context
     * @param moveDirection
     */
    @Override
    public void closePosition(GrayTradingContext context, int moveDirection) {
        GAccountPositionVo positions = phemexClient.queryGAccountPositions(grayProp.getBaseCurrency(), grayProp.getContract());
        if (positions == null || CollectionUtils.isEmpty(positions.getPositions())) {
            return;
        }
        PosSide posSide = moveDirection < 0 ? PosSide.Long : PosSide.Short;
        positions.getPositions().stream()
                .filter(position -> !position.getSide().equalsIgnoreCase("None"))
                .filter(position -> position.getPosSide().equals(posSide.name())).forEach(position -> {
            log.info("Starting to close position: {}", position);
            long priceEv = ConvertorUtils.convertToScaleValue(position.getPositionMarginRv(), context.getScaleRatio()).longValue();
            long ordQtyEv = ConvertorUtils.convertToScaleValue(position.getValueRv(), context.getScaleRatio()).longValue();
            orderService.checkAndCreateOrder(context, priceEv, ordQtyEv,
                    Side.valueOf(position.getSide()) == Side.Sell ? Side.Buy : Side.Sell, position.getPosSide(), OrdType.Market, true);
        });
    }

    @Override
    public void adjustPositions(GrayTradingContext context) {
        GAccountPositionVo positionVo = phemexClient.queryGAccountPositions(grayProp.getBaseCurrency(), context.getSymbol());
        if (CollectionUtils.isEmpty(positionVo.getPositions())) {
            log.warn("Skip empty position");
        } else if (CollectionUtils.isEmpty(context.getPositions())) {
            positionVo.getPositions().stream().filter(position -> !position.getSide().equalsIgnoreCase("None")).forEach(position -> {
                int direction = position.getSide().equalsIgnoreCase(Side.Sell.name()) ? -1 : 1;
                long newPriceEv = PriceUtils.calculatePrice(ConvertorUtils.convertToScaleValue(position.getAvgEntryPriceRp(), context.getScaleRatio()).longValue(),
                        context.getStepRatioEv(), direction, context.getScaleRatio());
                long ordQtyEv = ConvertorUtils.convertToScaleValue(position.getValueRv(), context.getScaleRatio()).longValue();
                orderService.checkAndCreateOrder(context, newPriceEv, ordQtyEv, Side.valueOf(position.getSide()) == Side.Buy ? Side.Sell : Side.Buy, position.getPosSide(), OrdType.Limit, true);
            });
        } else {
            positionVo.getPositions().stream().filter(position -> !position.getSide().equalsIgnoreCase("None")).forEach(position -> {
                Optional<GPositionVo> prePosition = context.getPositions().stream().filter(x -> x.getSide().equalsIgnoreCase(position.getSide()))
                        .filter(y -> ConvertorUtils.isClosePrice(new BigDecimal(position.getAvgEntryPriceRp()), new BigDecimal(y.getAvgEntryPriceRp()), PRICE_MOVEMENT))
                        .findAny();
                if (!prePosition.isPresent()) {
                    int direction = position.getSide().equalsIgnoreCase(Side.Sell.name()) ? -1 : 1;
                    long newPriceEv = PriceUtils.calculatePrice(ConvertorUtils.convertToScaleValue(position.getAvgEntryPriceRp(), context.getScaleRatio()).longValue(),
                            context.getStepRatioEv(), direction, context.getScaleRatio());
                    long ordQtyEv = ConvertorUtils.convertToScaleValue(position.getValueRv(), context.getScaleRatio()).longValue();
                    orderService.checkAndCreateOrder(context, newPriceEv, ordQtyEv, Side.valueOf(position.getSide()) == Side.Buy ? Side.Sell : Side.Buy, position.getPosSide(), OrdType.Limit, true);
                }
            });
        }
        context.setPositions(positionVo.getPositions().stream().filter(position -> !position.getSide().equalsIgnoreCase("None")).collect(Collectors.toList()));
    }
}

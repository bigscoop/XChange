package org.knowm.xchange.bybit.service;

import static org.knowm.xchange.bybit.BybitAdapters.adaptBybitOrderDetails;
import static org.knowm.xchange.bybit.BybitAdapters.convertToBybitSymbol;
import static org.knowm.xchange.bybit.BybitAdapters.getSideString;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.bybit.dto.BybitResult;
import org.knowm.xchange.bybit.dto.trade.BybitLinearOrderDetails;
import org.knowm.xchange.bybit.dto.trade.BybitOrderDetails;
import org.knowm.xchange.bybit.dto.trade.BybitOrderRequest;
import org.knowm.xchange.bybit.dto.trade.BybitPosition;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.meta.InstrumentMetaData;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.utils.OrderValuesHelper;

public class BybitTradeService extends BybitTradeServiceRaw implements TradeService {

    public BybitTradeService(Exchange exchange) {
        super(exchange);
    }

    @Override
    public String placeMarketOrder(MarketOrder marketOrder) throws IOException {
        BybitResult<BybitOrderRequest> order = placeOrder(
                convertToBybitSymbol(marketOrder.getInstrument().toString()),
                marketOrder.getOriginalAmount().longValue(),
                getSideString(marketOrder.getType()),
                "MARKET");

        return order.getResult().getOrderId();
    }

    public BybitLinearOrderDetails placeLinearOrder(Instrument instrument, BigDecimal qty, BigDecimal limitPrice, String side, BigDecimal stopLoss, BigDecimal takeProfit) throws IOException {
        InstrumentMetaData instrumentMetaData = exchange.getExchangeMetaData().getInstruments().get(instrument);
        OrderValuesHelper helper = new OrderValuesHelper(instrumentMetaData);
        OrderType orderType = "sell".equalsIgnoreCase(side) ? OrderType.ASK : OrderType.BID;
        BigDecimal adjustedQty = helper.adjustAmount(qty);
        BigDecimal adjustedStopLoss = helper.adjustPrice(stopLoss, orderType);
        BigDecimal adjustedTakeProfit = helper.adjustPrice(takeProfit, orderType.getOpposite());
        BigDecimal adjustedLimitPrice = helper.adjustPrice(limitPrice, orderType);
        String type = adjustedLimitPrice == null ? "Market" : "Limit";
        BybitResult<BybitLinearOrderDetails> order = placeLinealOrder(
            convertToBybitSymbol(instrument.toString()), adjustedQty, adjustedLimitPrice, type,
            side, adjustedStopLoss, adjustedTakeProfit);

        return order.getResult();
    }

    public List<BybitLinearOrderDetails> getLinearOrders(String symbol) throws IOException {
        BybitResult<List<BybitLinearOrderDetails>> order = getLinealOrders(symbol);

        return order.getResult();
    }


    public List<BybitPosition> getPositions(String symbol) throws IOException {
        BybitResult<List<BybitPosition>> order = getLinearPositions(symbol);

        return order.getResult();
    }


    public BybitPosition getPosition(String symbol) throws IOException {
        return getLinearPositions(symbol).getResult().get(0);
    }


    public void setLeverage(Instrument instrument, BigDecimal buyLeverage, BigDecimal sellLeverage) throws IOException {
        setLeverage(convertToBybitSymbol(instrument.toString()), buyLeverage, sellLeverage);
    }

    @Override
    public Collection<Order> getOrder(String... orderIds) throws IOException {
        List<Order> results = new ArrayList<>();

        for (String orderId : orderIds) {
            BybitResult<BybitOrderDetails> bybitOrder = getBybitOrder(orderId);
            BybitOrderDetails bybitOrderResult = bybitOrder.getResult();
            Order order = adaptBybitOrderDetails(bybitOrderResult);
            results.add(order);
        }

        return results;
    }

}

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
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
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

    public BybitLinearOrderDetails placeLinearOrder(Instrument instrument, BigDecimal qty, String side, BigDecimal stopLoss, BigDecimal takeProfit) throws IOException {
        CurrencyPairMetaData currencyPairMetaData = exchange.getExchangeMetaData().getCurrencyPairs().get(instrument);
        OrderType orderType = "sell".equalsIgnoreCase(side) ? OrderType.ASK : OrderType.BID;
        BigDecimal adjustedQty = OrderValuesHelper.adjustAmount(qty, currencyPairMetaData);
        BigDecimal adjustedStopLoss = OrderValuesHelper.adjustPrice(stopLoss, orderType, currencyPairMetaData);
        BigDecimal adjustedTakeProfit = OrderValuesHelper.adjustPrice(takeProfit, orderType.getOpposite(), currencyPairMetaData);
        BybitResult<BybitLinearOrderDetails> order = placeLinealOrder(
            convertToBybitSymbol(instrument.toString()), adjustedQty,
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

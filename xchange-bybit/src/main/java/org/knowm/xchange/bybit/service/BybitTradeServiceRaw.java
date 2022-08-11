package org.knowm.xchange.bybit.service;

import static org.knowm.xchange.bybit.BybitAdapters.createBybitExceptionFromResult;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.bybit.dto.BybitResult;
import org.knowm.xchange.bybit.dto.trade.BybitLinearOrderDetails;
import org.knowm.xchange.bybit.dto.trade.BybitOrderDetails;
import org.knowm.xchange.bybit.dto.trade.BybitOrderRequest;
import org.knowm.xchange.bybit.dto.trade.BybitPosition;

public class BybitTradeServiceRaw extends BybitBaseService {

  public BybitTradeServiceRaw(Exchange exchange) {
    super(exchange);
  }

  public BybitResult<BybitOrderDetails> getBybitOrder(String orderId) throws IOException {
    BybitResult<BybitOrderDetails> order = bybitAuthenticated.getOrder(apiKey, orderId, nonceFactory, signatureCreator);
    if (!order.isSuccess()) {
      throw createBybitExceptionFromResult(order);
    }
    return order;
  }

  public BybitResult<BybitOrderRequest> placeOrder(String symbol, long qty, String side, String type) throws IOException {
    BybitResult<BybitOrderRequest> placeOrder = bybitAuthenticated.placeOrder(
            apiKey,
            symbol,
            qty,
            side,
            type,
            nonceFactory,
            signatureCreator
    );
    if (!placeOrder.isSuccess()) {
      throw createBybitExceptionFromResult(placeOrder);
    }
    return placeOrder;
  }

  public BybitResult<BybitLinearOrderDetails> placeLinealOrder(String symbol, BigDecimal qty, BigDecimal price,
      String type, String side, BigDecimal stopLoss, BigDecimal takeProfit) throws IOException {
    BybitResult<BybitLinearOrderDetails> placeOrder = bybitAuthenticated.placeLinearOrder(apiKey,
        false, type, 0, qty, price, false, side, stopLoss, symbol, takeProfit,
        "ImmediateOrCancel", nonceFactory, signatureCreator
    );
    if (!placeOrder.isSuccess()) {
      throw createBybitExceptionFromResult(placeOrder);
    }
    return placeOrder;
  }

  public BybitResult<List<BybitLinearOrderDetails>> getLinealOrders(String symbol) throws IOException {
    BybitResult<List<BybitLinearOrderDetails>> placeOrder = bybitAuthenticated.getLinearOrders(apiKey, symbol, nonceFactory,
        signatureCreator);
    if (!placeOrder.isSuccess()) {
      throw createBybitExceptionFromResult(placeOrder);
    }
    return placeOrder;
  }


  public BybitResult<List<BybitPosition>> getLinearPositions(String symbol) throws IOException {
    BybitResult<List<BybitPosition>> placeOrder = bybitAuthenticated.getLinearPositions(apiKey, symbol, nonceFactory, signatureCreator);
    if (!placeOrder.isSuccess()) {
      throw createBybitExceptionFromResult(placeOrder);
    }
    return placeOrder;
  }


  public BybitResult<Object> setLeverage(String symbol, BigDecimal buyLeverage, BigDecimal sellLeverage) throws IOException {
    BigDecimal adjustedBuyLeverage = buyLeverage.divide(BigDecimal.TEN, MathContext.DECIMAL32);
    BigDecimal adjustedSellLeverage = sellLeverage.divide(BigDecimal.TEN, MathContext.DECIMAL32);
    BybitResult<Object> result = bybitAuthenticated.setLeverage(apiKey, symbol, adjustedBuyLeverage, adjustedSellLeverage, nonceFactory, signatureCreator);
    if (!result.isSuccess()) {
      throw createBybitExceptionFromResult(result);
    }
    return result;
  }


}

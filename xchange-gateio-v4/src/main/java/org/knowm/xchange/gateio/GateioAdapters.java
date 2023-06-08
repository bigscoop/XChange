package org.knowm.xchange.gateio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderStatus;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.dto.marketdata.Trades.TradeSortType;
import org.knowm.xchange.dto.meta.InstrumentMetaData;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.gateio.dto.GateioOrderType;
import org.knowm.xchange.gateio.dto.account.GateioOrder;
import org.knowm.xchange.gateio.dto.account.GateioWithdrawalRequest;
import org.knowm.xchange.gateio.dto.marketdata.GateioCurrencyPairDetails;
import org.knowm.xchange.gateio.dto.marketdata.GateioOrderBook;
import org.knowm.xchange.gateio.dto.marketdata.GateioPublicOrder;
import org.knowm.xchange.gateio.dto.marketdata.GateioTicker;
import org.knowm.xchange.gateio.dto.marketdata.GateioTradeHistory;
import org.knowm.xchange.gateio.service.params.DefaultGateioWithdrawFundsParams;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.utils.DateUtils;


@UtilityClass
public class GateioAdapters {

  public CurrencyPair adaptCurrencyPair(String pair) {
    final String[] currencies = pair.toUpperCase().split("_");
    return new CurrencyPair(currencies[0], currencies[1]);
  }


  public String toString(Currency currency) {
    return currency.getCurrencyCode();
  }


  public String toString(Instrument instrument) {
    return String.format("%s_%s",
            instrument.getBase().getCurrencyCode(),
            instrument.getCounter().getCurrencyCode())
        .toUpperCase(Locale.ROOT);
  }


  public Instrument toInstrument(String currencyCode) {
    var currencies = currencyCode.split("_");
    return new CurrencyPair(currencies[0], currencies[1]);
  }


  public Ticker adaptTicker(CurrencyPair currencyPair, GateioTicker gateioTicker) {

    BigDecimal ask = gateioTicker.getLowestAsk();
    BigDecimal bid = gateioTicker.getHighestBid();
    BigDecimal last = gateioTicker.getLast();
    BigDecimal low = gateioTicker.getLow24hr();
    BigDecimal high = gateioTicker.getHigh24hr();
    // Looks like gate.io vocabulary is inverted...
    BigDecimal baseVolume = gateioTicker.getQuoteVolume();
    BigDecimal quoteVolume = gateioTicker.getBaseVolume();
    BigDecimal percentageChange = gateioTicker.getPercentChange();

    return new Ticker.Builder()
        .currencyPair(currencyPair)
        .ask(ask)
        .bid(bid)
        .last(last)
        .low(low)
        .high(high)
        .volume(baseVolume)
        .quoteVolume(quoteVolume)
        .percentageChange(percentageChange)
        .build();
  }


  public OrderBook toOrderBook(GateioOrderBook gateioOrderBook, Instrument instrument) {
    List<LimitOrder> asks = gateioOrderBook.getAsks().stream()
        .map(priceSizeEntry -> new LimitOrder(OrderType.ASK, priceSizeEntry.getSize(), instrument, null, null, priceSizeEntry.getPrice()))
        .collect(Collectors.toList());


    List<LimitOrder> bids = gateioOrderBook.getBids().stream()
        .map(priceSizeEntry -> new LimitOrder(OrderType.BID, priceSizeEntry.getSize(), instrument, null, null, priceSizeEntry.getPrice()))
        .collect(Collectors.toList());

    return new OrderBook(Date.from(gateioOrderBook.getGeneratedAt()), asks, bids);
  }


  public LimitOrder adaptOrder(
      GateioPublicOrder order, CurrencyPair currencyPair, OrderType orderType) {

    return new LimitOrder(orderType, order.getAmount(), currencyPair, "", null, order.getPrice());
  }

  public List<LimitOrder> adaptOrders(
      List<GateioPublicOrder> orders, CurrencyPair currencyPair, OrderType orderType) {

    List<LimitOrder> limitOrders = new ArrayList<>();

    for (GateioPublicOrder bterOrder : orders) {
      limitOrders.add(adaptOrder(bterOrder, currencyPair, orderType));
    }

    return limitOrders;
  }

  public OrderType adaptOrderType(GateioOrderType cryptoTradeOrderType) {
    return (cryptoTradeOrderType.equals(GateioOrderType.BUY)) ? OrderType.BID : OrderType.ASK;
  }

  public Trade adaptTrade(
      GateioTradeHistory.GateioPublicTrade trade, CurrencyPair currencyPair) {

    OrderType orderType = adaptOrderType(trade.getType());
    Date timestamp = DateUtils.fromMillisUtc(trade.getDate() * 1000);

    return new Trade.Builder()
        .type(orderType)
        .originalAmount(trade.getAmount())
        .currencyPair(currencyPair)
        .price(trade.getPrice())
        .timestamp(timestamp)
        .id(trade.getTradeId())
        .build();
  }

  public Trades adaptTrades(GateioTradeHistory tradeHistory, CurrencyPair currencyPair) {

    List<Trade> tradeList = new ArrayList<>();
    long lastTradeId = 0;
    for (GateioTradeHistory.GateioPublicTrade trade : tradeHistory.getTrades()) {
      String tradeIdString = trade.getTradeId();
      if (!tradeIdString.isEmpty()) {
        long tradeId = Long.valueOf(tradeIdString);
        if (tradeId > lastTradeId) {
          lastTradeId = tradeId;
        }
      }
      Trade adaptedTrade = adaptTrade(trade, currencyPair);
      tradeList.add(adaptedTrade);
    }

    return new Trades(tradeList, lastTradeId, TradeSortType.SortByTimestamp);
  }


  public InstrumentMetaData toInstrumentMetaData(GateioCurrencyPairDetails gateioCurrencyPairDetails) {
    return new InstrumentMetaData.Builder()
        .tradingFee(gateioCurrencyPairDetails.getFee())
        .minimumAmount(gateioCurrencyPairDetails.getMinAssetAmount())
        .counterMinimumAmount(gateioCurrencyPairDetails.getMinQuoteAmount())
        .volumeScale(gateioCurrencyPairDetails.getAssetScale())
        .priceScale(gateioCurrencyPairDetails.getQuoteScale())
        .build();
  }


  public String toString(OrderStatus orderStatus) {
    switch (orderStatus) {
      case OPEN:
        return "open";
      case CLOSED:
        return "finished";
      default:
        throw new IllegalArgumentException("Can't map " + orderStatus);
    }
  }


  public OrderStatus toOrderStatus(String gateioOrderStatus) {
    switch (gateioOrderStatus) {
      case "open":
        return OrderStatus.OPEN;
      case "filled":
      case "closed":
        return OrderStatus.FILLED;
      case "cancelled":
      case "stp":
        return OrderStatus.CANCELED;
      default:
        throw new IllegalArgumentException("Can't map " + gateioOrderStatus);
    }
  }


  public GateioOrder toGateioOrder(MarketOrder marketOrder) {
    GateioOrder gateioOrder = GateioOrder.builder()
        .currencyPair(toString(marketOrder.getInstrument()))
        .side(toString(marketOrder.getType()))
        .clientOrderId(marketOrder.getUserReference())
        .account("spot")
        .type("market")
        .timeInForce("ioc")
        .amount(marketOrder.getOriginalAmount())
        .build();
    return gateioOrder;
  }


  public Order toOrder(GateioOrder gateioOrder) {
    Order.Builder order;
    Instrument instrument = toInstrument(gateioOrder.getCurrencyPair());
    OrderType orderType = toOrderType(gateioOrder.getSide());

    switch (gateioOrder.getType()) {
      case "market":
        order = new MarketOrder.Builder(orderType, instrument);
        break;
      case "limit":
        order = new LimitOrder.Builder(orderType, instrument);
        break;
      default:
        throw new IllegalArgumentException("Can't map " + gateioOrder.getType());
    }

    return order
        .id(gateioOrder.getId())
        .originalAmount(gateioOrder.getAmount())
        .userReference(gateioOrder.getClientOrderId())
        .timestamp(Date.from(gateioOrder.getCreatedAt()))
        .orderStatus(toOrderStatus(gateioOrder.getStatus()))
        .cumulativeAmount(gateioOrder.getFilledTotalQuote())
        .averagePrice(gateioOrder.getAvgDealPrice())
        .fee(gateioOrder.getFee())
        .build();
  }


  public String toString(OrderType orderType) {
    switch (orderType) {
      case BID:
        return "buy";
      case ASK:
        return "sell";
      default:
        throw new IllegalArgumentException("Can't map " + orderType);
    }
  }


  public OrderType toOrderType(String gateioOrderType) {
    switch (gateioOrderType) {
      case "buy":
        return OrderType.BID;
      case "sell":
        return OrderType.ASK;
      default:
        throw new IllegalArgumentException("Can't map " + gateioOrderType);
    }
  }


  public GateioWithdrawalRequest toGateioWithdrawalRequest(DefaultGateioWithdrawFundsParams p) {
    return GateioWithdrawalRequest.builder()
        .clientRecordId(p.getClientRecordId())
        .address(p.getAddress())
        .tag(p.getAddressTag())
        .chain(p.getChain())
        .amount(p.getAmount())
        .currency(toString(p.getCurrency()))
        .build();

  }
}

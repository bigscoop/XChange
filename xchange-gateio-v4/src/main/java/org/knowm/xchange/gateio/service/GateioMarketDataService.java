package org.knowm.xchange.gateio.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.dto.meta.InstrumentMetaData;
import org.knowm.xchange.gateio.GateioAdapters;
import org.knowm.xchange.gateio.GateioErrorAdapter;
import org.knowm.xchange.gateio.GateioExchange;
import org.knowm.xchange.gateio.dto.GateioException;
import org.knowm.xchange.gateio.dto.marketdata.GateioCurrencyPairDetails;
import org.knowm.xchange.gateio.dto.marketdata.GateioOrderBook;
import org.knowm.xchange.gateio.dto.marketdata.GateioTicker;
import org.knowm.xchange.gateio.dto.marketdata.GateioTradeHistory;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.marketdata.params.CurrencyPairsParam;
import org.knowm.xchange.service.marketdata.params.Params;

public class GateioMarketDataService extends GateioMarketDataServiceRaw
    implements MarketDataService {

  /**
   * Constructor
   *
   * @param exchange
   */
  public GateioMarketDataService(GateioExchange exchange) {

    super(exchange);
  }

  @Override
  public Ticker getTicker(CurrencyPair currencyPair, Object... args) throws IOException {

    GateioTicker ticker =
        super.getBTERTicker(
            currencyPair.base.getCurrencyCode(), currencyPair.counter.getCurrencyCode());

    return GateioAdapters.adaptTicker(currencyPair, ticker);
  }

  @Override
  public List<Ticker> getTickers(Params params) throws IOException {
    final List<CurrencyPair> currencyPairs = new ArrayList<>();
    if (params instanceof CurrencyPairsParam) {
      currencyPairs.addAll(((CurrencyPairsParam) params).getCurrencyPairs());
    }
    return getGateioTickers().values().stream()
        .filter(
            ticker -> currencyPairs.size() == 0 || currencyPairs.contains(ticker.getCurrencyPair()))
        .collect(Collectors.toList());
  }

  @Override
  public OrderBook getOrderBook(CurrencyPair currencyPair, Object... args) throws IOException {
    return getOrderBook((Instrument) currencyPair, args);
  }

  @Override
  public OrderBook getOrderBook(Instrument instrument, Object... args) throws IOException {

    GateioOrderBook gateioOrderBook = getGateioOrderBook(instrument);

    return GateioAdapters.toOrderBook(gateioOrderBook, instrument);

  }

  @Override
  public Trades getTrades(CurrencyPair currencyPair, Object... args) throws IOException {

    GateioTradeHistory tradeHistory =
        (args != null && args.length > 0 && args[0] != null && args[0] instanceof String)
            ? super.getBTERTradeHistorySince(
                currencyPair.base.getCurrencyCode(),
                currencyPair.counter.getCurrencyCode(),
                (String) args[0])
            : super.getBTERTradeHistory(
                currencyPair.base.getCurrencyCode(), currencyPair.counter.getCurrencyCode());

    return GateioAdapters.adaptTrades(tradeHistory, currencyPair);
  }


  public Map<Instrument, InstrumentMetaData> getMetaDataByInstrument() throws IOException {
    try {
      List<GateioCurrencyPairDetails> metadata = getCurrencyPairDetails();

      return metadata.stream()
          .collect(Collectors.toMap(
              gateioCurrencyPairDetails -> new CurrencyPair(gateioCurrencyPairDetails.getAsset(), gateioCurrencyPairDetails.getQuote()),
              GateioAdapters::toInstrumentMetaData
          ));
    }
    catch (GateioException e) {
      throw GateioErrorAdapter.adapt(e);
    }
  }


}

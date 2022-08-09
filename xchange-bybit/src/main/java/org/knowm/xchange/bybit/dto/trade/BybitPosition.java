package org.knowm.xchange.bybit.dto.trade;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Value
public class BybitPosition {

  @JsonProperty("user_id")
  Integer userId;

  @JsonProperty("symbol")
  String symbol;

  @JsonProperty("side")
  String side;

  @JsonProperty("size")
  BigDecimal size;

  @JsonProperty("position_value")
  BigDecimal positionValue;

  @JsonProperty("entry_price")
  BigDecimal entryPrice;

  @JsonProperty("liq_price")
  BigDecimal liqPrice;

  @JsonProperty("bust_price")
  BigDecimal bustPrice;

  @JsonProperty("leverage")
  Integer leverage;

  @JsonProperty("auto_add_margin")
  Integer autoAddMargin;

  @JsonProperty("is_isolated")
  Boolean isIsolated;

  @JsonProperty("position_margin")
  BigDecimal positionMargin;

  @JsonProperty("occ_closing_fee")
  BigDecimal occClosingFee;

  @JsonProperty("realised_pnl")
  BigDecimal realisedPnl;

  @JsonProperty("cum_realised_pnl")
  BigDecimal cumRealisedPnl;

  @JsonProperty("free_qty")
  BigDecimal freeQty;

  @JsonProperty("tp_sl_mode")
  String tpSlMode;

  @JsonProperty("unrealised_pnl")
  BigDecimal unrealisedPnl;

  @JsonProperty("deleverage_indicator")
  Integer deleverageIndicator;

  @JsonProperty("risk_id")
  Integer riskId;

  @JsonProperty("stop_loss")
  BigDecimal stopLoss;

  @JsonProperty("take_profit")
  BigDecimal takeProfit;

  @JsonProperty("trailing_stop")
  BigDecimal trailingStop;

  @JsonProperty("position_idx")
  Integer positionIdx;

  @JsonProperty("mode")
  String mode;

  public Boolean isOpen() {
    return !"none".equalsIgnoreCase(side);
  }

}

package org.knowm.xchange.bybit.dto.trade;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Value
public class BybitLinearOrderDetails {

    @JsonProperty("order_id")
    String orderId;

    @JsonProperty("user_id")
    Long userId;

    @JsonProperty("symbol")
    String symbol;

    @JsonProperty("side")
    String side;

    @JsonProperty("order_type")
    String orderType;

    @JsonProperty("price")
    BigDecimal price;

    @JsonProperty("qty")
    BigDecimal qty;

    @JsonProperty("time_in_force")
    String timeInForce;

    @JsonProperty("order_status")
    String orderStatus;

    @JsonProperty("last_exec_price")
    BigDecimal lastExecPrice;

    @JsonProperty("cum_exec_qty")
    BigDecimal cumExecQty;

    @JsonProperty("cum_exec_value")
    BigDecimal cumExecValue;

    @JsonProperty("cum_exec_fee")
    BigDecimal cumExecFee;

    @JsonProperty("reduce_only")
    Boolean reduceOnly;

    @JsonProperty("close_on_trigger")
    Boolean closeOnTrigger;

    @JsonProperty("order_link_id")
    String orderLinkId;

    @JsonProperty("created_time")
    Date createdTime;

    @JsonProperty("updated_time")
    Date updatedTime;

    @JsonProperty("take_profit")
    BigDecimal takeProfit;

    @JsonProperty("stop_loss")
    BigDecimal stopLoss;

    @JsonProperty("tp_trigger_by")
    String tpTriggerBy;

    @JsonProperty("sl_trigger_by")
    String slTriggerBy;

    @JsonProperty("position_idx")
    Integer positionIdx;

}

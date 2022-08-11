package org.knowm.xchange.bybit;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.knowm.xchange.bybit.dto.BybitResult;
import org.knowm.xchange.bybit.dto.account.BybitBalances;
import org.knowm.xchange.bybit.dto.trade.BybitLinearOrderDetails;
import org.knowm.xchange.bybit.dto.trade.BybitOrderDetails;
import org.knowm.xchange.bybit.dto.trade.BybitOrderRequest;
import org.knowm.xchange.bybit.dto.trade.BybitPosition;
import org.knowm.xchange.bybit.service.BybitException;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.SynchronizedValueFactory;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public interface BybitAuthenticated {

  @GET
  @Path("/spot/v1/account")
  BybitResult<BybitBalances> getWalletBalances(
      @QueryParam("api_key") String apiKey,
      @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @QueryParam("sign") ParamsDigest signature
  ) throws IOException, BybitException;

  @GET
  @Path("/spot/v1/order")
  BybitResult<BybitOrderDetails> getOrder(
      @QueryParam("api_key") String apiKey,
      @QueryParam("orderId") String orderId,
      @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @QueryParam("sign") ParamsDigest signature
  ) throws IOException, BybitException;

  @POST
  @Path("/spot/v1/order")
  BybitResult<BybitOrderRequest> placeOrder(
      @FormParam("api_key") String apiKey,
      @FormParam("symbol") String symbol,
      @FormParam("qty") long qty,
      @FormParam("side") String side,
      @FormParam("type") String type,
      @FormParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @FormParam("sign") ParamsDigest signature
  ) throws IOException, BybitException;

  @POST
  @Path("/private/linear/order/create")
  BybitResult<BybitLinearOrderDetails> placeLinearOrder(
      @FormParam("api_key") String apiKey,
      @FormParam("close_on_trigger") boolean closeOnTrigger,
      @FormParam("order_type") String type,
      @FormParam("position_idx") Integer positionIdx,
      @FormParam("qty") BigDecimal qty,
      @FormParam("price") BigDecimal price,
      @FormParam("reduce_only") boolean reduceOnly,
      @FormParam("side") String side,
      @FormParam("stop_loss") BigDecimal stopLoss,
      @FormParam("symbol") String symbol,
      @FormParam("take_profit") BigDecimal takeProfit,
      @FormParam("time_in_force") String timeInForce,
      @FormParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @FormParam("sign") ParamsDigest signature
  ) throws IOException, BybitException;


  @GET
  @Path("/private/linear/order/list")
  BybitResult<List<BybitLinearOrderDetails>> getLinearOrders(
      @QueryParam("api_key") String apiKey,
      @QueryParam("symbol") String symbol,
      @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @QueryParam("sign") ParamsDigest signature
  ) throws IOException, BybitException;


  @GET
  @Path("/private/linear/position/list")
  BybitResult<List<BybitPosition>> getLinearPositions(
      @QueryParam("api_key") String apiKey,
      @QueryParam("symbol") String symbol,
      @QueryParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @QueryParam("sign") ParamsDigest signature
  ) throws IOException, BybitException;


  @POST
  @Path("/private/linear/position/set-leverage")
  BybitResult<Object> setLeverage(
      @FormParam("api_key") String apiKey,
      @FormParam("symbol") String symbol,
      @FormParam("buy_leverage") BigDecimal buyLeverage,
      @FormParam("sell_leverage") BigDecimal sellLeverage,
      @FormParam("timestamp") SynchronizedValueFactory<Long> timestamp,
      @FormParam("sign") ParamsDigest signature
  ) throws IOException, BybitException;


}

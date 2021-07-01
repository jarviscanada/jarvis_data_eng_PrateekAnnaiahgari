package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * MarketDataDao is responsible for getting Quotes from IEX
 */
@Repository
public class MarketDataDao implements CrudRepository<IexQuote, String> {

  private static final String IEX_BATCH_PATH = "/stock/market/batch?symbols=%s&types=quote&token=";
  private final String IEX_BATCH_URL;
  public final String UNKNOWN_TICKER_RESPONSE="Unknown symbol";

  private Logger logger = LoggerFactory.getLogger(MarketDataDao.class);
  private HttpClientConnectionManager httpClientConnectionManager;

  @Autowired
  public MarketDataDao(HttpClientConnectionManager httpClientConnectionManager,
      MarketDataConfig marketDataConfig) {
    this.httpClientConnectionManager = httpClientConnectionManager;
    IEX_BATCH_URL = marketDataConfig.getHost() + IEX_BATCH_PATH + marketDataConfig.getToken();
  }

  /**
   * Get an IexQuote (helper method which calls finAllById)
   * @param ticker
   * @throws IllegalArgumentException If a given ticker is invalid
   * @throws DataRetrievalFailureException if HTTP request failed
   */
  @Override
  public Optional<IexQuote> findById(String ticker) {
    Optional<IexQuote> iexQuote;
    List<IexQuote> quotes = findAllById(Collections.singletonList(ticker));
    if(quotes.size() ==0){
      return Optional.empty();
    } else if (quotes.size()==1){
      iexQuote = Optional.of(quotes.get(0));
    } else {
      throw new DataRetrievalFailureException("Unexpected number of quotes");
    }
    return iexQuote;
  }


  /**
   * Get quotes from IEX
   * @param tickers is a list of tickers
   * @return a list of IexQuote object
   * @throws IllegalArgumentException if any ticker is invalid or tickers is empty
   * @throws DataRetrievalFailureException if HTTP request failed
   */
  @Override
  public List<IexQuote> findAllById(Iterable<String> tickers) {
    List<IexQuote> iexQuotes = new ArrayList<>();
    long numTickers = StreamSupport.stream(tickers.spliterator(), false).count();
    String url = String.format(IEX_BATCH_URL,String.join(",",tickers));
    ObjectMapper m = new ObjectMapper();
    try {
      JsonNode responseJsonNode = m.readTree(executeHttpGet(url).get());
      List<JsonNode> quotesJsonList = responseJsonNode.findValues("quote");
      if(numTickers!=quotesJsonList.size()){
        throw new IllegalArgumentException();
      }
      for(JsonNode qouteNode:quotesJsonList ){
        iexQuotes.add(m.treeToValue(qouteNode,IexQuote.class));
      }
    } catch (IOException e) {
      throw new DataRetrievalFailureException("Unable to parse JSON to Object");
    } catch (IllegalArgumentException e){
      throw  new IllegalArgumentException("Invalid ticker(s)");
    } catch (DataRetrievalFailureException e){
      throw new DataRetrievalFailureException("HTTP request failed: "+ e.getMessage());
    }
    return iexQuotes;
  }

  @Override
  public <S extends IexQuote> S save(S s) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public <S extends IexQuote> Iterable<S> saveAll(Iterable<S> iterable) {
    throw new UnsupportedOperationException("Not implemented");
  }


  @Override
  public boolean existsById(String s) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public Iterable<IexQuote> findAll() {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public long count() {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void deleteById(String s) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void delete(IexQuote iexQuote) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void deleteAll(Iterable<? extends IexQuote> iterable) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void deleteAll() {
    throw new UnsupportedOperationException("Not implemented");
  }

  /**
   * Execute a get and return http entity/body as a string
   * Uses EntityUtils.toString to process HTTP entity
   *
   * @param url resource URL
   * @return http response body or Optional.empty for 404 response
   * @throws DataRetrievalFailureException if Http failed or Status code is unexpected
   * @throws IllegalArgumentException if ticker(s) is/are invalid
   */
  private Optional<String> executeHttpGet(String url) {
    String responseBody;
    HttpClient httpClient = getHttpClient();
    URI uri = URI.create(url);
    logger.debug(url);
    HttpGet request = new HttpGet(uri);
    HttpResponse response;
    try {
      response=httpClient.execute(request);
      responseBody = EntityUtils.toString(response.getEntity());
    } catch (IOException e) {
      throw new DataRetrievalFailureException("Unable to retrieve data");
    }
    if(responseBody.equals(UNKNOWN_TICKER_RESPONSE)){
      throw new IllegalArgumentException("Invalid ticker(s)");
    }
    if(response.getStatusLine().getStatusCode()!=200){
      throw new DataRetrievalFailureException("Unexpected status code :" +
          response.getStatusLine().getStatusCode());
    }
    return Optional.of(responseBody);
  }

  /**
   * Borrow a HTTP client from the httpClientConnectionManager
   * @return
   */
  private CloseableHttpClient getHttpClient(){
    return HttpClients.custom()
        .setConnectionManager(httpClientConnectionManager)
        .setConnectionManagerShared(true)
        .build();
  }
}

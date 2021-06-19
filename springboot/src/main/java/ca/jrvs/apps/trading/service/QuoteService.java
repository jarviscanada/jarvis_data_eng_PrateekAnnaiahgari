package ca.jrvs.apps.trading.service;


import ca.jrvs.apps.trading.dao.MarketDataDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.Quote;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class QuoteService {

  public static final Logger logger = LoggerFactory.getLogger(QuoteService.class);

  private QuoteDao quoteDao;
  private MarketDataDao marketDataDao;

  @Autowired
  public QuoteService(QuoteDao quoteDao, MarketDataDao marketDataDao) {
    this.quoteDao=quoteDao;
    this.marketDataDao=marketDataDao;
  }

  /**
   * Update quote table against IEX source
   * -get all quotes from the db
   * - foreach ticker get iexQuote
   * - convert iexQuote to quote entity
   * - persist quote to db
   *
   * @throws org.springframework.dao.DataAccessException if unable to retrieve data
   * @throws IllegalArgumentException for invalid input
   */
  public void updateMarketData(){
    List<Quote> quotesList = quoteDao.findAll();
    List<IexQuote> iexQuotesList = quotesList.stream().map(quote ->
        findIexQuoteByTicker(quote.getTicker()))
        .collect(Collectors.toList());
    quoteDao.saveAll(iexQuotesList.stream()
        .map(QuoteService::buildQuoteFromIexQuote)
        .collect(Collectors.toList()));
  }

  /**
   * Helper method. Map a IexQuote to a Quote entity.
   * Note: `IexQuote.getLatestPrice() == null` if the stock market is closed.
   * Set a default value for number field(s).
   */
  protected static Quote buildQuoteFromIexQuote(IexQuote iexQuote){
    Quote quote = new Quote();
    quote.setId(iexQuote.getSymbol());
    quote.setLastPrice(iexQuote.getLatestPrice());
    quote.setAskPrice(Double.valueOf(iexQuote.getIexAskPrice()));
    quote.setBidPrice(Double.valueOf(iexQuote.getIexBidPrice()));
    quote.setAskSize((int) iexQuote.getIexAskSize());
    quote.setBidSize((int) iexQuote.getIexBidSize());
    return quote;
  }

  /**
   * Validate (against IEX) and save given tickers to quote table.
   * - Get IexQuote(s)
   * - convert each IexQuote to Quote entity
   * - persist the quote to db
   *
   * @param tickers a list of tickers/symbols
   * @throws IllegalArgumentException if ticker is not found from IEX
   */
  public List<Quote> saveQuotes(List<String> tickers){
    List<Quote> quotes =marketDataDao.findAllById(tickers).stream()
        .map(QuoteService::buildQuoteFromIexQuote).collect(Collectors.toList());
    return quoteDao.saveAll(quotes);
  }

  /**
   * Helper method
   */
  public Quote saveQuote(String ticker){
    Optional<IexQuote> iexQuote = marketDataDao.findById(ticker);
    return saveQuote(buildQuoteFromIexQuote(iexQuote.get()));
  }

  /**
   * Fina an IexQuote
   *
   * @param ticker id
   * @return IexQuote object
   * @throws IllegalArgumentException if ticker is invalid
   */
  public IexQuote findIexQuoteByTicker(String ticker){
    return marketDataDao.findById(ticker)
        .orElseThrow(() -> new IllegalArgumentException(ticker + " is invalid"));
  }

  /**
   * Update a given quote to quote table without validation
   * @param quote entity
   */
  public Quote saveQuote(Quote quote){
    return quoteDao.save(quote);
  }

  /**
   * Find all quotes from the quote table
   * @return a list of quotes
   */
  public List<Quote> findAllQuotes(){
    return  quoteDao.findAll();
  }


}

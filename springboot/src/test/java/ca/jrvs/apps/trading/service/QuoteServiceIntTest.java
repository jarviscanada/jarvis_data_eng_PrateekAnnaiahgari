package ca.jrvs.apps.trading.service;

import static org.junit.Assert.*;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.Quote;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
@Sql({"classpath:schema.sql"})
public class QuoteServiceIntTest {

  @Autowired
  private QuoteService quoteService;

  @Autowired
  private QuoteDao quoteDao;

  @Before
  public void setUp() throws Exception {
    quoteDao.deleteAll();
  }

  @Test
  public void findIexQuoteByTicker() {
    IexQuote iexQuote = quoteService.findIexQuoteByTicker("AAPL");
    assertEquals(iexQuote.getSymbol(), "AAPL");

    iexQuote = quoteService.findIexQuoteByTicker("MSFT");
    assertEquals(iexQuote.getSymbol(),"MSFT");
  }

  @Test
  public void updateMarketData() {
    Quote q1 = new Quote();
    q1.setAskPrice(5d);
    q1.setAskSize(5);
    q1.setBidPrice(5.2d);
    q1.setBidSize(5);
    q1.setTicker("FB");
    q1.setLastPrice(5.1d);
    Quote q2=quoteDao.save(q1);

    quoteService.updateMarketData();

    Quote testQ = quoteDao.findById(q1.getTicker()).get();

    assertEquals(q1.getTicker(),testQ.getTicker());
  }

  @Test
  public void saveQuotes() {
    List<Quote> quotes = quoteService.saveQuotes(Arrays.asList("AAPL","FB","MSFT"));
    assertEquals(3, quotes.size());
    assertEquals("AAPL",quotes.get(0).getTicker());
  }

  @Test
  public void saveQuote() {
    Quote quote = quoteService.saveQuote("AAPL");
    assertEquals("AAPL",quote.getTicker());
  }

  @Test
  public void findAllQuotes(){
    Quote q1 = new Quote();
    q1.setAskPrice(5d);
    q1.setAskSize(5);
    q1.setBidPrice(5.2d);
    q1.setBidSize(5);
    q1.setTicker("FB");
    q1.setLastPrice(5.1d);

    quoteService.saveQuote(q1);
    quoteService.updateMarketData();
    List<Quote> quotes = quoteService.findAllQuotes();
    assertEquals(1, quotes.size());
    assertEquals(quotes.get(0).getTicker(),"FB");
  }

}
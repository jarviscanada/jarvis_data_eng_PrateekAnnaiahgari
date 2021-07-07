package ca.jrvs.apps.trading.dao;

import static org.junit.Assert.*;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.model.domain.Quote;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.After;
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
public class QuoteDaoIntTest {

  @Autowired
  private QuoteDao quoteDao;

  private Quote savedQuote = new Quote();

  @Before
  public void insertOne() {
    savedQuote.setAskPrice(10d);
    savedQuote.setAskSize(10);
    savedQuote.setBidPrice(10.2d);
    savedQuote.setBidSize(10);
    savedQuote.setTicker("AAPL");
    savedQuote.setLastPrice(10.1d);
    quoteDao.save(savedQuote);
  }

  @After
  public void deleteOne(){
    quoteDao.deleteAll();
  }

  @Test
  public void save() {
    Quote q1 = new Quote();
    q1.setAskPrice(5d);
    q1.setAskSize(5);
    q1.setBidPrice(5.2d);
    q1.setBidSize(5);
    q1.setTicker("FB");
    q1.setLastPrice(5.1d);
    Quote test1 = quoteDao.save(q1);
    assertEquals(q1.getTicker(),test1.getTicker());
    assertEquals(q1.getAskPrice(),test1.getAskPrice());
    assertEquals(q1.getBidPrice(),test1.getBidPrice());
  }

  @Test
  public void saveAll() {
    Quote q1 = new Quote();
    q1.setAskPrice(5d);
    q1.setAskSize(5);
    q1.setBidPrice(5.2d);
    q1.setBidSize(5);
    q1.setTicker("FB");
    q1.setLastPrice(5.1d);
    Quote q2 = new Quote();
    q2.setAskPrice(6d);
    q2.setAskSize(6);
    q2.setBidPrice(6.2d);
    q2.setBidSize(6);
    q2.setTicker("MSFT");
    q2.setLastPrice(6.1d);
    List<Quote> quotes = quoteDao.saveAll(Arrays.asList(q1,q2));
    quotes.stream().forEach(quote -> System.out.println(quote.getTicker()));
    assertEquals(q1.getTicker(),quotes.get(0).getTicker());
    assertEquals(q1.getAskPrice(),quotes.get(0).getAskPrice());
    assertEquals(q1.getBidPrice(),quotes.get(0).getBidPrice());
    assertEquals(q2.getTicker(),quotes.get(1).getTicker());
    assertEquals(q2.getAskPrice(),quotes.get(1).getAskPrice());
    assertEquals(q2.getBidPrice(),quotes.get(1).getBidPrice());
  }

  @Test
  public void findById() {
    Optional<Quote> q = quoteDao.findById("AAPL");
    assertEquals(savedQuote.getTicker(),q.get().getTicker());
    assertEquals(savedQuote.getAskPrice(),q.get().getAskPrice());
    assertEquals(savedQuote.getBidPrice(),q.get().getBidPrice());
  }

  @Test
  public void existsById() {
    assertTrue(quoteDao.existsById("AAPL"));
    assertFalse(quoteDao.existsById("ABC"));
  }

  @Test
  public void findAll() {
    Quote q1 = new Quote();
    q1.setAskPrice(5d);
    q1.setAskSize(5);
    q1.setBidPrice(5.2d);
    q1.setBidSize(5);
    q1.setTicker("FB");
    q1.setLastPrice(5.1d);
    Quote q2 = new Quote();
    q2.setAskPrice(6d);
    q2.setAskSize(6);
    q2.setBidPrice(6.2d);
    q2.setBidSize(6);
    q2.setTicker("MSFT");
    q2.setLastPrice(6.1d);
    quoteDao.saveAll(Arrays.asList(q1,q2));
    List<Quote> quotes = quoteDao.findAll();
    assertEquals(savedQuote.getTicker(),quotes.get(0).getTicker());
    assertEquals(q1.getTicker(),quotes.get(1).getTicker());
    assertEquals(q2.getTicker(),quotes.get(2).getTicker());
  }

  @Test
  public void count() {
    Quote q1 = new Quote();
    q1.setAskPrice(5d);
    q1.setAskSize(5);
    q1.setBidPrice(5.2d);
    q1.setBidSize(5);
    q1.setTicker("FB");
    q1.setLastPrice(5.1d);
    Quote q2 = new Quote();
    q2.setAskPrice(6d);
    q2.setAskSize(6);
    q2.setBidPrice(6.2d);
    q2.setBidSize(6);
    q2.setTicker("MSFT");
    q2.setLastPrice(6.1d);
    quoteDao.saveAll(Arrays.asList(q1,q2));
    assertEquals(3,quoteDao.count());
  }

  @Test
  public void deleteById() {
    Quote q1 = new Quote();
    q1.setAskPrice(5d);
    q1.setAskSize(5);
    q1.setBidPrice(5.2d);
    q1.setBidSize(5);
    q1.setTicker("FB");
    q1.setLastPrice(5.1d);
    quoteDao.save(q1);
    assertEquals(2,quoteDao.count());
    quoteDao.deleteById("FB");
    assertEquals(1,quoteDao.count());
  }

  @Test
  public void deleteAll() {
    Quote q1 = new Quote();
    q1.setAskPrice(5d);
    q1.setAskSize(5);
    q1.setBidPrice(5.2d);
    q1.setBidSize(5);
    q1.setTicker("FB");
    q1.setLastPrice(5.1d);
    quoteDao.save(q1);
    assertEquals(2,quoteDao.count());
    quoteDao.deleteAll();;
    assertEquals(0,quoteDao.count());
  }
}
package ca.jrvs.apps.trading.dao;

import static org.junit.Assert.*;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Position;
import ca.jrvs.apps.trading.model.domain.Quote;
import ca.jrvs.apps.trading.model.domain.SecurityOrder;
import ca.jrvs.apps.trading.model.domain.Trader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestConfig.class})
@Sql({"classpath:schema.sql"})
public class PositionDaoTest {


  @Autowired
  private AccountDao accountDao;
  @Autowired
  private SecurityOrderDao securityOrderDao;
  @Autowired
  private TraderDao traderDao;
  @Autowired
  private QuoteDao quoteDao;
  @Autowired
  private PositionDao positionDao;

  private SecurityOrder sOrder1;
  private SecurityOrder sOrder2;

  @Before
  public void setUp() throws Exception {
    securityOrderDao.deleteAll();
    traderDao.deleteAll();
    accountDao.deleteAll();
    quoteDao.deleteAll();

    Quote quote1 = new Quote();
    quote1.setAskPrice(50d);
    quote1.setAskSize(15);
    quote1.setBidPrice(56d);
    quote1.setBidSize(16);
    quote1.setTicker("FB");
    quote1.setLastPrice(54d);
    quoteDao.save(quote1);

    Quote quote2 = new Quote();
    quote2.setAskPrice(60d);
    quote2.setAskSize(25);
    quote2.setBidPrice(66d);
    quote2.setBidSize(26);
    quote2.setTicker("MSFT");
    quote2.setLastPrice(64d);
    quoteDao.save(quote2);

    Trader trader1 = new Trader();
    trader1.setFirst_name("Sai Prateek");
    trader1.setLast_name("Annaiahgari");
    trader1.setEmail("saiprateek@email.com");
    trader1.setDob(new Date(1996,5,5));
    trader1.setCountry("Canada");

    Trader trader2 = new Trader();
    trader2.setFirst_name("Arpith");
    trader2.setLast_name("Arpi");
    trader2.setEmail("aa@email.com");
    trader2.setDob(new Date(1995,5,5));
    trader2.setCountry("Chile");

    traderDao.saveAll(Arrays.asList(trader1, trader2));

    Account account1 = new Account();
    account1.setAmount(15);
    account1.setTrader_id(trader1.getId());

    Account account2 = new Account();
    account2.setAmount(15.5);
    account2.setTrader_id(trader2.getId());

    accountDao.saveAll(Arrays.asList(account1,account2));

    sOrder1 = new SecurityOrder();
    sOrder1.setAccount_id(account1.getId());
    sOrder1.setStatus("FILLED");
    sOrder1.setTicker(quote1.getId());
    sOrder1.setSize(27);
    sOrder1.setPrice(50d);
    sOrder1.setNotes("Test row 1 notes");


    sOrder2 = new SecurityOrder();
    sOrder2.setAccount_id(account2.getId());
    sOrder2.setStatus("FILLED");
    sOrder2.setTicker(quote2.getId());
    sOrder2.setSize(18);
    sOrder2.setPrice(55d);
    sOrder2.setNotes("Test row 2 notes");

    securityOrderDao.saveAll(Arrays.asList(sOrder1,sOrder2));
  }

  @Test
  public void findById() {
    Position position1 = positionDao.findById(sOrder1.getAccount_id()).get();
    assertEquals(sOrder1.getSize(), position1.getPosition());
  }

  @Test
  public void existsById() {
    assertTrue(positionDao.existsById(sOrder1.getAccount_id()));
    assertTrue(positionDao.existsById(sOrder2.getAccount_id()));
    assertFalse(positionDao.existsById(-1));
  }

  @Test
  public void findAll() {
    List<Position> testPositions = positionDao.findAll();
    assertEquals(2, testPositions.size());
    assertEquals(sOrder1.getTicker(),testPositions.get(0).getTicker());
  }

  @Test
  public void count() {
    assertEquals(2,positionDao.count());
  }

  @Test
  public void findAllById() {
    List<Position> testPositions = positionDao.findAllById(
        Arrays.asList(sOrder1.getAccount_id(),sOrder2.getAccount_id()));
    assertEquals(2, testPositions.size());
    assertEquals(testPositions.get(0).getTicker(),sOrder1.getTicker());
  }
}
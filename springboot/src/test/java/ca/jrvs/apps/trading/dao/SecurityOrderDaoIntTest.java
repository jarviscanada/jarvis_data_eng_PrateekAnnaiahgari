package ca.jrvs.apps.trading.dao;

import static org.junit.Assert.*;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Quote;
import ca.jrvs.apps.trading.model.domain.SecurityOrder;
import ca.jrvs.apps.trading.model.domain.Trader;
import java.util.ArrayList;
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
public class SecurityOrderDaoIntTest {
  @Autowired
  private SecurityOrderDao securityOrderDao;
  @Autowired
  AccountDao accountDao;
  @Autowired
  TraderDao traderDao;
  @Autowired
  private QuoteDao quoteDao;

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
    sOrder1.setStatus("Active");
    sOrder1.setTicker(quote1.getId());
    sOrder1.setSize(27);
    sOrder1.setPrice(50d);
    sOrder1.setNotes("Test row 1 notes");


    sOrder2 = new SecurityOrder();
    sOrder2.setAccount_id(account1.getId());
    sOrder2.setStatus("Active");
    sOrder2.setTicker(quote2.getId());
    sOrder2.setSize(18);
    sOrder2.setPrice(55d);
    sOrder2.setNotes("Test row 2 notes");

    securityOrderDao.saveAll(Arrays.asList(sOrder1,sOrder2));
  }

  @Test
  public void save() {
    securityOrderDao.deleteAll();
    SecurityOrder testOrder1 = securityOrderDao.save(sOrder1);
    assertEquals(testOrder1.getTicker(),sOrder1.getTicker());
    assertEquals(testOrder1.getNotes(),sOrder1.getNotes());
    assertEquals(testOrder1.getSize(),sOrder1.getSize());
  }

  @Test
  public void findById() {
    SecurityOrder testOrder1 = securityOrderDao.findById(sOrder1.getId()).get();
    SecurityOrder testOrder2 = securityOrderDao.findById(sOrder2.getId()).get();
    assertEquals(testOrder1.getTicker(),sOrder1.getTicker());
    assertEquals(testOrder1.getNotes(),sOrder1.getNotes());
    assertEquals(testOrder1.getSize(),sOrder1.getSize());
    assertEquals(testOrder2.getTicker(),sOrder2.getTicker());
    assertEquals(testOrder2.getNotes(),sOrder2.getNotes());
    assertEquals(testOrder2.getSize(),sOrder2.getSize());
  }

  @Test
  public void existsById() {
    assertTrue(securityOrderDao.existsById(sOrder1.getId()));
    assertTrue(securityOrderDao.existsById(sOrder2.getId()));
    assertFalse(securityOrderDao.existsById(-1));
  }

  @Test
  public void findAll() {
    List<SecurityOrder> testOrders = securityOrderDao.findAll();
    assertEquals(2, testOrders.size());
    assertEquals(testOrders.get(0).getTicker(),sOrder1.getTicker());
  }

  @Test
  public void findAllById() {
    List<SecurityOrder> testOrders = securityOrderDao.findAllById(
        Arrays.asList(sOrder1.getId(),sOrder2.getId()));
    assertEquals(2, testOrders.size());
    assertEquals(testOrders.get(0).getTicker(),sOrder1.getTicker());
  }

  @Test
  public void deleteById() {
    assertEquals(2,securityOrderDao.count());
    securityOrderDao.deleteById(sOrder1.getId());
    assertEquals(1,securityOrderDao.count());
  }

  @Test
  public void count() {
    assertEquals(2,securityOrderDao.count());
  }

  @Test
  public void deleteAll() {
    assertEquals(2,securityOrderDao.count());
    securityOrderDao.deleteAll();
    assertEquals(0, securityOrderDao.count());
  }

  @Test
  public void saveAll() {
    securityOrderDao.deleteAll();
    List<SecurityOrder> testOrders = (List<SecurityOrder>) securityOrderDao.saveAll(Arrays.asList(sOrder1,sOrder2));
    assertEquals(2, testOrders.size());
    assertEquals(testOrders.get(0).getTicker(),sOrder1.getTicker());
  }

  @Test
  public void delete() {
    try{
      securityOrderDao.delete(new SecurityOrder());
      fail();
    } catch (UnsupportedOperationException e){
      assertTrue(true);
    }
  }

  @Test
  public void testDeleteAll() {
    try{
      securityOrderDao.deleteAll(new ArrayList<>());
      fail();
    } catch (UnsupportedOperationException e){
      assertTrue(true);
    }
  }
}
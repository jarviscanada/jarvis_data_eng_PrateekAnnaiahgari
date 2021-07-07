package ca.jrvs.apps.trading.service;

import static org.junit.Assert.*;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.TraderDao;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Trader;
import ca.jrvs.apps.trading.model.domain.TraderAccountView;
import java.util.Date;
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
public class TraderAccountServiceTest {

  private TraderAccountView savedView;
  @Autowired
  private  TraderAccountService traderAccountService;
  @Autowired
  private TraderDao traderDao;
  @Autowired
  private AccountDao accountDao;

  private Trader trader;

  @Before
  public void setUp() throws Exception{
    traderDao.deleteAll();
    accountDao.deleteAll();

    trader = new Trader();
    trader.setFirst_name("Sai Prateek");
    trader.setLast_name("Annaiahgari");
    trader.setEmail("saiprateek@email.com");
    trader.setDob(new Date(1996,5,5));
    trader.setCountry("India");
  }

  @Test
  public void createTraderAndAccount() {
    TraderAccountView testView = traderAccountService.createTraderAndAccount(trader);
    assertEquals(trader.getFirst_name(),testView.getTrader().getFirst_name());
    assertEquals(0,testView.getAccount().getAmount(),0);
    assertTrue(traderDao.existsById(testView.getTrader().getId()));
    assertTrue(accountDao.existsById(testView.getAccount().getId()));
  }

  @Test
  public void deleteTraderById() {
    try{
      traderAccountService.deleteTraderById(null);
      fail();
    } catch (IllegalArgumentException e){
      assertTrue(true);
    }
    TraderAccountView testView = traderAccountService.createTraderAndAccount(trader);
    assertTrue(traderDao.existsById(testView.getTrader().getId()));
    assertTrue(accountDao.existsById(testView.getAccount().getId()));
    traderAccountService.deleteTraderById(testView.getTrader().getId());
    assertFalse(traderDao.existsById(testView.getTrader().getId()));
    assertFalse(accountDao.existsById(testView.getAccount().getId()));

  }

  @Test
  public void deposit() {
    TraderAccountView testView = traderAccountService.createTraderAndAccount(trader);
    Account testAccount = traderAccountService.deposit(testView.getTrader().getId(),500.0);
    try{
      traderAccountService.deposit(testView.getTrader().getId(),-500.0);
      fail();
    } catch (IllegalArgumentException e){
      assertTrue(true);
    }
    try{
      traderAccountService.deposit(null,500.0);
      fail();
    } catch (IllegalArgumentException e){
      assertTrue(true);
    }
    assertEquals(500.0,testAccount.getAmount(),0);
  }

  @Test
  public void withdraw() {
    TraderAccountView testView = traderAccountService.createTraderAndAccount(trader);
    traderAccountService.deposit(testView.getTrader().getId(),500.0);
    Account testAccount = traderAccountService.withdraw(testView.getTrader().getId(),250.0);
    try{
      traderAccountService.withdraw(testView.getTrader().getId(),-500.0);
      fail();
    } catch (IllegalArgumentException e){
      assertTrue(true);
    }
    try{
      traderAccountService.withdraw(null,500.0);
      fail();
    } catch (IllegalArgumentException e){
      assertTrue(true);
    }
    try{
      traderAccountService.withdraw(testView.getTrader().getId(),251.0);
      fail();
    } catch (IllegalArgumentException e){
      assertTrue(true);
    }
    assertEquals(250.0,testAccount.getAmount(),0);
  }
}
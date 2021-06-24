package ca.jrvs.apps.trading.dao;

import static org.junit.Assert.*;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Trader;
import com.fasterxml.jackson.databind.util.ArrayIterator;
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
public class AccountDaoIntTest {

  @Autowired
  AccountDao accountDao;
  @Autowired
  TraderDao traderDao;

  private Account account1;
  private Account account2;
  private Account account3;

  @Before
  public void setUp() throws Exception {
    traderDao.deleteAll();
    accountDao.deleteAll();

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

    Trader trader3 = new Trader();
    trader3.setFirst_name("Raghu");
    trader3.setLast_name("Annaiahgari");
    trader3.setEmail("raghu@email.com");
    trader3.setDob(new Date(1998,5,5));
    trader3.setCountry("India");

    traderDao.saveAll(Arrays.asList(trader1, trader2, trader3));

    account1 = new Account();
    account1.setAmount(15);
    account1.setTrader_id(trader1.getId());

    account2 = new Account();
    account2.setAmount(15.5);
    account2.setTrader_id(trader2.getId());

    account3 = new Account();
    account3.setAmount(16);
    account3.setTrader_id(trader3.getId());

    accountDao.saveAll(Arrays.asList(account1,account2,account3));
  }

  @Test
  public void save() {
    accountDao.deleteAll();
    Account testAcc =accountDao.save(account1);
    assertEquals(testAcc.getId(),account1.getId());
    assertEquals(testAcc.getTrader_id(),account1.getTrader_id());
  }

  @Test
  public void findById() {
    Account testAcc1 = accountDao.findById(account1.getId()).get();
    assertEquals(account1.getId(),testAcc1.getId());
    assertEquals(account1.getTrader_id(),testAcc1.getTrader_id());

  }

  @Test
  public void existsById() {
    assertEquals(true,accountDao.existsById(account1.getId()));
    assertEquals(true,accountDao.existsById(account2.getId()));
    assertEquals(false,accountDao.existsById(-1));
  }

  @Test
  public void findAll() {
    List<Account> testAccs = accountDao.findAll();
    assertEquals(testAccs.size(),3);
    assertEquals(testAccs.get(0).getId(),account1.getId());
  }

  @Test
  public void findAllById() {
    List<Account> testAccs = accountDao.findAllById(Arrays.asList(account1.getId(),account2.getId()));
    assertEquals(testAccs.size(),2);
    assertEquals(testAccs.get(0).getId(),account1.getId());
  }

  @Test
  public void deleteById() {
    assertEquals(accountDao.count(),3);
    accountDao.deleteById(account1.getId());
    assertEquals(accountDao.count(),2);
  }

  @Test
  public void count() {
    assertEquals(3,accountDao.count());
  }

  @Test
  public void deleteAll() {
    assertEquals(3,accountDao.count());
    accountDao.deleteAll();
    assertEquals(0, accountDao.count());
  }

  @Test
  public void saveAll() {
    accountDao.deleteAll();
    List<Account> teasAccs = (List<Account>) accountDao.saveAll(Arrays.asList(account1,account2,account3));
    assertEquals(teasAccs.size(),3);
    assertEquals(teasAccs.get(2).getId(),account3.getId());
  }

  @Test
  public void delete() {
    try{
      accountDao.delete(new Account());
      fail();
    } catch (UnsupportedOperationException e){
      assertTrue(true);
    }
  }

  @Test
  public void testDeleteAll() {
    try{
      accountDao.deleteAll(new ArrayList<>());
      fail();
    } catch (UnsupportedOperationException e){
      assertTrue(true);
    }
  }

  @Test
  public  void findByTraderId(){
    Account testAcc1 = accountDao.findByTraderId(account1.getTrader_id()).get();
    assertEquals(account1.getId(),testAcc1.getId());
    assertEquals(account1.getTrader_id(),testAcc1.getTrader_id());
  }
}
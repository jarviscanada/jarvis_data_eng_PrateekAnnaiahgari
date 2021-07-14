package ca.jrvs.apps.trading.dao;

import static org.junit.Assert.*;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.model.domain.Account;
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
public class TraderDaoIntTest {

  @Autowired
  private TraderDao traderDao;

  private Trader trader1;
  private Trader trader2;
  private Trader trader3;

  @Before
  public void setUp() throws Exception {
    traderDao.deleteAll();

    trader1 = new Trader();
    trader1.setFirst_name("Sai Prateek");
    trader1.setLast_name("Annaiahgari");
    trader1.setEmail("saiprateek@email.com");
    trader1.setDob(new Date(1996,5,5));
    trader1.setCountry("Canada");

    trader2 = new Trader();
    trader2.setFirst_name("Arpith");
    trader2.setLast_name("Arpi");
    trader2.setEmail("aa@email.com");
    trader2.setDob(new Date(1995,5,5));
    trader2.setCountry("Chile");

    trader3 = new Trader();
    trader3.setFirst_name("Raghu");
    trader3.setLast_name("Annaiahgari");
    trader3.setEmail("raghu@email.com");
    trader3.setDob(new Date(1998,5,5));
    trader3.setCountry("India");

    traderDao.saveAll(Arrays.asList(trader1, trader2, trader3));
  }

  @Test
  public void save() {
    traderDao.deleteAll();
    Trader testT1 = traderDao.save(trader1);
    assertEquals(testT1.getCountry(),trader1.getCountry());
    assertEquals(testT1.getDob(),trader1.getDob());
  }

  @Test
  public void findById() {
    Trader testT1 = traderDao.findById(trader1.getId()).get();
    Trader testT2 = traderDao.findById(trader2.getId()).get();
    assertEquals(testT1.getCountry(),trader1.getCountry());
    assertEquals(testT1.getFirst_name(),trader1.getFirst_name());
    assertEquals(testT2.getCountry(),trader2.getCountry());
    assertEquals(testT2.getFirst_name(),trader2.getFirst_name());
  }

  @Test
  public void existsById() {
    assertTrue(traderDao.existsById(trader1.getId()));
    assertTrue(traderDao.existsById(trader3.getId()));
    assertFalse(traderDao.existsById(-1));
  }

  @Test
  public void findAll() {
    List<Trader> testTraders = traderDao.findAll();
    assertEquals(3,testTraders.size());
    assertEquals(testTraders.get(0).getEmail(),trader1.getEmail());
  }

  @Test
  public void findAllById() {
    List<Trader> testTraders = traderDao.findAllById(Arrays.asList(trader1.getId(),trader3.getId()));
    assertEquals(2, testTraders.size());
    assertEquals(testTraders.get(1).getEmail(),trader3.getEmail());
  }

  @Test
  public void deleteById() {
    assertEquals(3,traderDao.count());
    traderDao.deleteById(trader2.getId());
    assertEquals(2,traderDao.count());
  }

  @Test
  public void count() {
    assertEquals(3,traderDao.count());
  }

  @Test
  public void deleteAll() {
    assertEquals(3,traderDao.count());
    traderDao.deleteAll();
    assertEquals(0, traderDao.count());
  }

  @Test
  public void saveAll() {
    traderDao.deleteAll();
    List<Trader> testAccs = (List<Trader>) traderDao.saveAll(Arrays.asList(trader1,trader2,trader3));
    assertEquals(testAccs.size(),3);
    assertEquals(testAccs.get(2).getId(),trader3.getId());
  }

  @Test
  public void delete() {
    try{
      traderDao.delete(new Trader());
      fail();
    } catch (UnsupportedOperationException e){
      assertTrue(true);
    }
  }

  @Test
  public void testDeleteAll() {
    try{
      traderDao.deleteAll(new ArrayList<>());
      fail();
    } catch (UnsupportedOperationException e){
      assertTrue(true);
    }
  }
}
package ca.jrvs.apps.trading.model.domain;

public class Account implements Entity<Integer>{

  private int id;
  private int trader_id;
  private double amount;

  public int getTrader_id() {
    return trader_id;
  }

  public void setTrader_id(int trader_id) {
    this.trader_id = trader_id;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  @Override
  public Integer getId() {
    return id;
  }

  @Override
  public void setId(Integer id) {
    this.id=id;
  }
}

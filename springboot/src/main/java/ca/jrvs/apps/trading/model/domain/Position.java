package ca.jrvs.apps.trading.model.domain;

public class Position {

  private Integer account_id;
  private String ticker;
  private Integer position;

  public void setAccount_id(Integer account_id) {
    this.account_id = account_id;
  }

  public void setTicker(String ticker) {
    this.ticker = ticker;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  public int getAccount_id() {
    return account_id;
  }

  public String getTicker() {
    return ticker;
  }

  public int getPosition() {
    return position;
  }
}

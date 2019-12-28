import java.io.Serializable;

public class Papiren implements Serializable {
    private String name, ticker, price, cap_price;

    public Papiren(String name, String ticker, String price, String cap_price){
        this.name = name;
        this.ticker = ticker;
        this.price = price;
        this.cap_price = cap_price;
    }

    public String getName() {
        return name;
    }

    public String getTicker() {
        return ticker;
    }

    public String getPrice() {
        return price;
    }

    public String getCap_price() {
        return cap_price;
    }

    public String print(){
        return name + " " + ticker + " " + price + " " + cap_price;
    }

    @Override
    public String toString(){
        return name;
    }
}

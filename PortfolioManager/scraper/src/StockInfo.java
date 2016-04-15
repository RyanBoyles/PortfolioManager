/**
 * Created by jeffl_000 on 4/10/2016.
 */
public class StockInfo {
    String name;
    double price;
    double priceChange;
    double perChange;
    double open;
    double todayHigh;
    double todayLow;
    double weekHigh;
    double weekLow;
    double pe;
    double yield;


    public StockInfo(){
        price = perChange = priceChange = open = todayHigh = todayLow = weekLow = weekHigh = pe = yield = 0;
        name = "";
    }

    public StockInfo(double price, double priceChange, double perChange){
        this.price = price;
        this.priceChange = priceChange;
        this.perChange = perChange;
    }

    public StockInfo(double price, double priceChange, double perChange, double open, double todayHigh,
                     double todayLow, double weekHigh, double weekLow, double pe, double yield){
        this.price = price;
        this.priceChange = priceChange;
        this.perChange = perChange;
        this.open = open;
        this.todayHigh = todayHigh;
        this.todayLow = todayLow;
        this.weekHigh = weekHigh;
        this.weekLow = weekLow;
        this.pe = pe;
        this.yield = yield;
        this.name = "";
    }
}


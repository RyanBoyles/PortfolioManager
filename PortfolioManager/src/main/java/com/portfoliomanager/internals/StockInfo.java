package com.portfoliomanager.internals;

/**
 * Created by jeffl_000 on 4/10/2016.
 */
public class StockInfo {
    double price;
    double priceChange;
    double perChange;

    StockInfo(double price, double priceChange, double perChange){
        this.price = price;
        this.priceChange = priceChange;
        this.perChange = perChange;
    }
}

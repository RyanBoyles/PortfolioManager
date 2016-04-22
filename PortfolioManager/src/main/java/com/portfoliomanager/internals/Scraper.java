package com.portfoliomanager.internals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jeffl_000 on 4/6/2016.
 */
public class Scraper {
    static String google = "https://www.google.com/finance?q=";  // Add symbol
    static String yahoo = "http://finance.yahoo.com/q?s=";  // Just add symbol
    static String msn = "http://www.msn.com/en-us/money/stockdetails/fi-126.1.";  // Add (symbol).(exchange)
    static String cnn = "http://money.cnn.com/quote/quote.html?symb=";  //Add symbol

    // Scrape function taken from
    // http://stackoverflow.com/questions/238547/how-do-you-programmatically-download-a-webpage-in-java
    public static String scrape(String urlString){
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line, page = "";

        try {
            url = new URL(urlString);
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                page += line;
            }
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }
        return page;
    }

    public static StockInfo replaceComma(String price, String priceChange, String perChange, String open, String todayHigh,
                                    String todayLow, String weekHigh, String weekLow, String pe, String yield, String beta){

        price = price.replaceAll(",","");
        priceChange = priceChange.replaceAll(",","");
        perChange = perChange.replaceAll(",","");
        open = open.replaceAll(",","");
        todayHigh = todayHigh.replaceAll(",","");
        todayLow = todayLow.replaceAll(",","");
        weekHigh = weekHigh.replaceAll(",","");
        weekLow = weekLow.replaceAll(",","");
        pe = pe.replaceAll(",","");
        yield = yield.replaceAll(",","");
        beta = beta.replaceAll(",","");

        StockInfo stock = new StockInfo(Double.parseDouble(price), Double.parseDouble(priceChange),Double.parseDouble(perChange)
                ,Double.parseDouble(open),Double.parseDouble(todayHigh),Double.parseDouble(todayLow),Double.parseDouble(weekHigh)
                ,Double.parseDouble(weekLow),Double.parseDouble(pe),Double.parseDouble(yield));
        stock.beta = Double.parseDouble(beta);

        return stock;
    }

    public static StockInfo scrapeYahoo(String stock){
        String webpage = scrape(yahoo+stock);  // price search for id: "yfs_184_(symbol)"
        String id = "yfs_l84_" + stock.toLowerCase();
        int index = webpage.indexOf(id)+id.length()+2;
        String price = webpage.substring(index);
        price = price.substring(0,price.indexOf("<"));

        id = "yfs_c63_" + stock.toLowerCase();
        index = webpage.indexOf(id) + id.length();
        String priceChange = webpage.substring(index)+2;
        String dir = priceChange.substring(priceChange.indexOf("alt")+5);
        priceChange = dir.substring(dir.indexOf("\">")+2, dir.indexOf("</span>")).trim();
        dir = dir.substring(0,dir.indexOf(">")-1);

        id = "yfs_p43_" + stock.toLowerCase();
        index = webpage.indexOf(id) + id.length();
        String perChange = webpage.substring(index);
        perChange = perChange.substring(perChange.indexOf("(")+1, perChange.indexOf("%"));
        if(dir.equalsIgnoreCase("Down"))
            priceChange = "-"+priceChange;
            perChange = "-"+perChange;
//        System.out.print(perChange);

        index = webpage.indexOf("yfi_quote_summary_data");
        String table = webpage.substring(index);
        String open = table.substring(table.indexOf("Open:"));
        open = open.substring(open.indexOf("data1")+7, open.indexOf("</td>"));

        String todayLow = table.substring(table.indexOf("yfs_g53"));
        todayLow = todayLow.substring(todayLow.indexOf(">")+1,todayLow.indexOf("</span>"));

        String todayHigh = table.substring(table.indexOf("yfs_h53"));
        todayHigh = todayHigh.substring(todayHigh.indexOf(">")+1,todayHigh.indexOf("</span>"));

        table = table.substring(table.indexOf("52wk Range"));
        String weekLow = table.substring(table.indexOf("<span>")+6);
        String weekHigh = weekLow.substring(weekLow.indexOf("<span>")+6);
        weekLow = weekLow.substring(0, weekLow.indexOf("</span>"));
        weekHigh = weekHigh.substring(0, weekHigh.indexOf("</span>"));

        String pe = table.substring(table.indexOf("P/E"));
        pe = pe.substring(pe.indexOf("data1")+7, pe.indexOf("</td>"));
        if(pe.equals("N/A"))
            pe = "0";

        String yield = table.substring(table.indexOf("Yield"));
        yield = yield.substring(yield.indexOf("(")+1, yield.indexOf(")"));
        if(yield.equals("N/A"))
            yield = "0";
        else
            yield = yield.substring(0,yield.indexOf("%"));

        String beta = webpage.substring(webpage.indexOf("Beta"));
        beta = beta.substring(beta.indexOf("data1\">")+7,beta.indexOf("</td>"));
        if(beta.equals("N/A"))
            beta = "-1";

        return replaceComma(price,priceChange,perChange,open,todayHigh,todayLow,weekHigh,weekLow,pe,yield,beta);
    }
    public static StockInfo scrapeGoogle(String stock, String exchange){
        if(exchange.equals("NAS"))
            exchange = "NASDAQ";
        else
            exchange = "NYSE";
        String webpage = scrape(google+exchange+"%3A"+stock);
        String search = "class=\"pr\"";
        int index = webpage.indexOf(search) + search.length() + 1;
        String price = webpage.substring(index);
        price = price.substring(price.indexOf(">")+1, price.indexOf("</s"));

        search = "class=\"ch ";
        index = webpage.indexOf(search) + search.length();
        String section = webpage.substring(index);

        String priceChange = section.substring(section.indexOf("c\">")+3, section.indexOf("</s"));
        String perChange = section.substring(section.indexOf("(")+1, section.indexOf("%"));
//        System.out.println(price);
        String range = section.substring(section.indexOf("Range"));
        range = range.substring(range.indexOf("val")+5);
        range = range.substring(0,range.indexOf("</td>"));
        String[] rangeVals = range.split("-");
        String todayLow = rangeVals[0].trim();
        String todayHigh = rangeVals[1].trim();

        range = section.substring(section.indexOf("52 week"));
        range = range.substring(range.indexOf("val")+5);
        range = range.substring(0,range.indexOf("</td>"));
        rangeVals = range.split("-");
        String weekLow = rangeVals[0].trim();
        String weekHigh = rangeVals[1].trim();

        String open = section.substring(section.indexOf("Open"));
        open = open.substring(open.indexOf("val")+5);
        open = open.substring(0,open.indexOf("</td>"));

        String pe = section.substring(section.indexOf("P/E"));
        pe = pe.substring(pe.indexOf("val")+5);
        pe = pe.substring(0,pe.indexOf("</td>")).trim();
        if(pe.indexOf("-") != -1)
            pe = "0";

        String yield = section.substring(section.indexOf("Div/yield"));
        yield = yield.substring(yield.indexOf("val")+5);
        yield = yield.substring(0,yield.indexOf("</td>"));
        if(yield.indexOf("-") != -1)
            yield = "0";
        else
            yield = yield.substring(yield.indexOf("/")+1);

        return replaceComma(price,priceChange,perChange,open,todayHigh,todayLow,weekHigh,weekLow,pe,yield,"0");
    }
    public static StockInfo scrapeMSN(String stock, String exchange){
        String webpage = scrape(msn+stock+"."+exchange);
        String search = "\"currentvalue\"";
        int index = webpage.indexOf(search)+ search.length()+1;
        String section = webpage.substring(index);
        String price = section.substring(0, section.indexOf("</"));

        section = section.substring(section.indexOf("role=\"change\"")+14);
        String priceChange = section.substring(0, section.indexOf("</"));
        if(priceChange.charAt(0) == '+')
            priceChange = priceChange.substring(1);

        String perChange = section.substring(section.indexOf("role=\"percentchange\"")+21, section.indexOf("%"));
        if(perChange.charAt(0) == '+')
            perChange = perChange.substring(1);
//        System.out.print(perChange);

        section = section.substring(section.indexOf("title='Open'"));
        String open = section.substring(section.indexOf("truncated-string"));
        open = open.substring(open.indexOf(">")+1, open.indexOf("</p>"));

        String range = section.substring(section.indexOf("Range"));
        range = range.substring(range.indexOf("truncated-string"));
        range = range.substring(range.indexOf(">")+1, range.indexOf("</p>"));
        String[] rangeVals = range.split("-");
        String todayLow = rangeVals[0];
        String todayHigh = rangeVals[1];

        range = section.substring(section.indexOf("52Wk Range"));
        range = range.substring(range.indexOf("truncated-string"));
        range = range.substring(range.indexOf(">")+1, range.indexOf("</p>"));
        rangeVals = range.split("-");
        String weekLow = rangeVals[0];
        String weekHigh = rangeVals[1];

        String yield = section.substring(section.indexOf("Yield"));
        yield = yield.substring(yield.indexOf("truncated-string"));
        yield = yield.substring(yield.indexOf(">")+1, yield.indexOf("</p>"));
        if(yield.indexOf("-") != -1)
            yield = "0";
        else
            yield = yield.substring(yield.indexOf("(")+1, yield.indexOf("%"));

        String pe = section.substring(section.indexOf("P/E"));
        pe = pe.substring(pe.indexOf("truncated-string"));
        pe = pe.substring(pe.indexOf(">")+1, pe.indexOf("</p>"));
        if(pe.indexOf("-") != -1)
            pe = "0";
        else
            pe = pe.substring(0, pe.indexOf(" ("));

        return replaceComma(price,priceChange,perChange,open,todayHigh,todayLow,weekHigh,weekLow,pe,yield,"0");
    }
    public static StockInfo scrapeCNN(String stock){
        String webpage = scrape(cnn+stock+"&exHours=off");
        String search = "wsod_last";
        int index = webpage.indexOf(search)+search.length();
        String section = webpage.substring(index);
        section = section.substring(section.indexOf("streamFeed"));
        String price = section.substring(section.indexOf(">")+1, section.indexOf("</span>"));

        search = "stream=\"change";
        index = webpage.indexOf(search)+search.length();
        section = webpage.substring(index);
        section = section.substring(section.indexOf("Data")+6);
        String priceChange = section.substring(0, section.indexOf("</"));
        if(priceChange.charAt(0) == '+')
            priceChange = priceChange.substring(1);

        section = webpage.substring(webpage.indexOf("changePct"));
        section = section.substring(section.indexOf("Data")+6);
        String perChange = section.substring(0, section.indexOf("%"));
        if(perChange.charAt(0) == '+')
            perChange = perChange.substring(1);
//        System.out.print(priceChange);

        section = section.substring(section.indexOf("Previous close"));
        String open = section.substring(section.indexOf("open"));
        open = open.substring(open.indexOf("Point\">")+7);
        open = open.substring(0,open.indexOf("</td>"));

        String range = section.substring(section.indexOf("range"));
        range = range.substring(range.indexOf("Point\">")+7);
        range = range.substring(0,range.indexOf("</td>"));
        String[] rangeVals = range.split("-");
        String todayLow = rangeVals[0].trim();
        String todayHigh = rangeVals[1].trim();

        String yield = section.substring(section.indexOf("Dividend yield"));
        yield = yield.substring(yield.indexOf("Point\">")+7);
        yield = yield.substring(0,yield.indexOf("</td>"));
        if(yield.equals("--"))
            yield = "0";
        else
            yield = yield.substring(0,yield.indexOf("%"));

        String pe = section.substring(section.indexOf("P/E ratio"));
        pe = pe.substring(pe.indexOf("Point\">")+7);
        if(pe.indexOf("NM") != -1)
            pe = "0";
        else
            pe = pe.substring(0,pe.indexOf("</td>"));

        String weekLow = webpage.substring(webpage.indexOf("val lo"));
        weekLow = weekLow.substring(weekLow.indexOf(">")+1, weekLow.indexOf("</div>"));

        String weekHigh = webpage.substring(webpage.indexOf("val hi"));
        weekHigh = weekHigh.substring(weekHigh.indexOf(">")+1, weekHigh.indexOf("</div>"));

        StockInfo cnn = replaceComma(price,priceChange,perChange,open,todayHigh,todayLow,weekHigh,weekLow,pe,yield,"0");

        search = "companyName";
        String name = webpage.substring(webpage.indexOf(search)+search.length()+2);
        name = name.substring(name.indexOf(">")+1,name.indexOf("<span")).trim();
        cnn.name = name;

        return cnn;
    }

    public static StockInfo scrapeAll(String stock, String exchange){
        StockInfo google = scrapeGoogle(stock, exchange);
        StockInfo msn = scrapeMSN(stock, exchange);
        StockInfo cnn = scrapeCNN(stock);
        StockInfo yahoo = scrapeYahoo(stock);

        StockInfo avg = new StockInfo();

        avg.price = (google.price + msn.price + cnn.price + yahoo.price) / 4;
        avg.priceChange = (google.priceChange + msn.priceChange + cnn.priceChange + yahoo.priceChange) / 4;
        avg.perChange = (google.perChange + msn.perChange + cnn.perChange + yahoo.perChange) / 4;
        avg.open = (google.open + msn.open + cnn.open+ yahoo.open) / 4;
        avg.todayHigh = (google.todayHigh + msn.todayHigh + cnn.todayHigh + yahoo.todayHigh) / 4;
        avg.todayLow = (google.todayLow + msn.todayLow + cnn.todayLow + yahoo.todayLow) / 4;
        avg.weekHigh = (google.weekHigh + msn.weekHigh + cnn.weekHigh + yahoo.weekHigh) / 4;
        avg.weekLow = (google.weekLow + msn.weekLow + cnn.weekLow + yahoo.weekLow) / 4;
        avg.pe = (google.pe + msn.pe + cnn.pe + yahoo.pe) / 4;
        avg.yield = (google.yield + msn.yield + cnn.yield + yahoo.yield) / 4;
        avg.name = cnn.name;
        avg.beta = yahoo.beta;

        return avg;
    }
    //public static void main(String[] args){
    //    scrapeYahoo("PAGP");
    //}
}

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
    public static StockInfo scrapeYahoo(String stock){
        String webpage = scrape(yahoo+stock);  // price search for id: "yfs_184_(symbol)"
        String id = "yfs_l84_" + stock.toLowerCase();
        int index = webpage.indexOf(id)+id.length()+2;
        String price = webpage.substring(index);
        price = price.substring(0,price.indexOf("<"));

        id = "yfs_c63_" + stock.toLowerCase();
        index = webpage.indexOf(id)+id.length()+2;
        String priceChange = webpage.substring(index);
        String dir = priceChange.substring(priceChange.indexOf("alt")+5,priceChange.indexOf("\">"));
        priceChange= priceChange.substring(priceChange.indexOf("\">")+2,priceChange.indexOf("</span>")).trim();

        id = "yfs_p43_" + stock.toLowerCase();
        index = webpage.indexOf(id) + id.length() + 3;
        String perChange = webpage.substring(index);
        perChange = perChange.substring(0, perChange.indexOf("%"));
        if(dir.equalsIgnoreCase("Down"))
            priceChange = "-"+priceChange;
            perChange = "-"+perChange;
//        System.out.print(perChange);
        return new StockInfo(Double.parseDouble(price), Double.parseDouble(priceChange),Double.parseDouble(perChange));
    }
    public static StockInfo scrapeGoogle(String stock){
        String webpage = scrape(google+stock);
        String search = "class=\"pr\"";
        int index = webpage.indexOf(search) + search.length() + 1;
        String price = webpage.substring(index);
        price = price.substring(price.indexOf(">")+1, price.indexOf("</s"));

        search = "class=\"chr\"";
        index = webpage.indexOf(search) + search.length();
        String section = webpage.substring(index);

        String priceChange = section.substring(section.indexOf(">")+1, section.indexOf("</s"));
        String perChange = section.substring(section.indexOf("(")+1, section.indexOf("%"));
//        System.out.print(price);
        return new StockInfo(Double.parseDouble(price), Double.parseDouble(priceChange),Double.parseDouble(perChange));
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

        return new StockInfo(Double.parseDouble(price), Double.parseDouble(priceChange),Double.parseDouble(perChange));
    }
    public static StockInfo scrapeCNN(String stock){
        String webpage = scrape(cnn+stock);
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

        search = "ProfileModule";
        index = webpage.indexOf(search)+search.length();
        section = webpage.substring(index);
        index = section.indexOf("_bold") + 7;
        section = section.substring(index);
        String sector = section.substring(0, section.indexOf("</div>"));

        search = "Industry";
        index = section.indexOf(search)+search.length();
        section = section.substring(index);
        index = section.indexOf("_bold") + 7;
        section = section.substring(index);
        String industry = section.substring(0, section.indexOf("</div>"));

        return new StockInfo(Double.parseDouble(price), Double.parseDouble(priceChange),Double.parseDouble(perChange));
    }

    public static void scrapeAll(String stock, String exchange){
        StockInfo google = scrapeGoogle(stock);
        StockInfo msn = scrapeMSN(stock, exchange);
        StockInfo cnn = scrapeCNN(stock);
        StockInfo yahoo = scrapeYahoo(stock);

        double price = (google.price + msn.price + cnn.price + yahoo.price) / 4;
        double priceChange = (google.priceChange + msn.priceChange + cnn.priceChange + yahoo.priceChange) / 4;
        double perChange = (google.perChange + msn.perChange + cnn.perChange + yahoo.perChange) / 4;
        System.out.println(price);
        System.out.println(priceChange);
        System.out.print(perChange);
    }
    public static void main(String[] args){
        scrapeAll("GPRO","NAS");

    }
}

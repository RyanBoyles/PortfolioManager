package com.portfoliomanager.internals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.portfoliomanager.domain.Stock;
import com.portfoliomanager.repository.StockRepository;

public class SimilarStockFinder
{
	
	public static double getRank(String symbol, double price) throws IOException
	{
		URL url = new URL("http://www.nasdaq.com/symbol/"+symbol+"/historical");
        URLConnection con = url.openConnection();
        InputStream is =con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line = null;
 
        int count = 0;
        long week_volume = 0;
        double old_price = 0;
        boolean p_flag = false;
        boolean rest_flag = false;
        boolean vol = true;
        //parse volume data
        while ((line = br.readLine()) != null) {
        	if(line.contains("The closing daily official volumes"))
        		p_flag = true;
        	if(p_flag)
        	{
        		if(count == 23)
        		{
        			String digits = line.replaceAll("[^0-9.]", "");
        			//System.out.println("curr price "+digits);
        		}
        		else if(count == 26)
        		{
        			p_flag = false;
        			rest_flag = true;
        			count = -1;
        		}
        		count++;
        	}
        	if(rest_flag)
        	{
        		if(count != 0 && count%21 == 0)
        		{
        			//System.out.println(line);
        			String digits = line.replaceAll("[^0-9.]", "");
        			//System.out.println(digits);
        			if(digits.isEmpty())
        			{
        				vol = false;
        				break;
        			}
        			else
        				week_volume += Long.parseLong(digits);
        		}
        		else if(count == 291)
        		{
        			String digits = line.replaceAll("[^0-9.]", "");
        			if(digits.isEmpty())
        			{
        				vol = false;
        				break;
        			}
        			else
        				old_price = Double.parseDouble(digits);
        			
        		}
        		else if(count == 295)
        			break;
        		count++;
        	}
        }
        line = "";
        URL url1 = new URL("http://www.nasdaq.com/symbol/"+symbol);
        URLConnection con1 = url1.openConnection();
        InputStream is1 =con1.getInputStream();
        BufferedReader br1 = new BufferedReader(new InputStreamReader(is1));
        long avg_volume = 0;
        double one_year = 0;
        boolean avg_flag = false;
        boolean one_flag = false;
        int y_c = 0;
        count = 0;
        boolean dividends = false;
        int d_c = 0;
        boolean one_exist = true;
        while ((line = br1.readLine()) != null) {
        	//gets avg volume
            if(line.contains("This is the average share volume for the past 50 trading days"))
            {
            	avg_flag = true;
            }
            if(avg_flag)
            {
            	count ++;
            	if(count == 7)
                {
            		String num = line.replaceAll("[^0-9.]", "");
            		if(num.isEmpty())
            		{
            			vol = false;
            			avg_flag = false;
            		}
            		else
            		{
            			avg_volume = Long.parseLong(num);									// PARSING ERROR HERE FOR CSCO
            			//System.out.println("Avg volume per day: "+ avg_volume);
            			avg_flag = false;
            		}
                }
            }
            
            //gets 1 year target
            if(line.contains("1 Year Target"))
            {
            	one_flag = true;
            }
            if(one_flag)
            {
            	if(y_c == 1)
            	{
            		String num = line.replaceAll("[^0-9.]", "");
            		if(num.isEmpty())
            		{
            			one_exist = false;
            		}
            		else
            		{
            			one_year = Double.parseDouble(num);
            			//System.out.println("1 year target: "+ one_year);
            		}
            		one_flag = false;
            	}
            	y_c++;
            }
            
            //dividends
            if(line.contains("Indicated yield represents annual dividends divided by current stock price."))
            	dividends = true;
            if(dividends && d_c <= 6)
            {
            	if(d_c == 6)
            	{
            		String num = line.replaceAll("[^0-9.]", "");
            		if(num.isEmpty())
            		{
            			dividends = false;
            			break;
            		}
            		double div = Double.parseDouble(num);
            		if(div <= 0)
            			dividends = false;
            	}
            	d_c++;
            }
        }
        
        double sum = 0;
        
        //1 year target rated 1-5
        if(one_exist)
        {
        	double off_target = ((double) one_year / price) -1;
        	if(off_target > 0)
        	{
        		if(off_target > .3)
        			sum += 5;
        		else if(off_target > .2)
        			sum += 4.5;
        		else if(off_target > .1)
        			sum += 4;
        		else
        			sum += 3.5;
        	}
        	else
        	{
        		if(off_target > -.05)
        			sum += 3;
        		else if(off_target > -.1)
        			sum += 2;
        		else if(off_target > -.15)
        			sum += 1;
        	}
        }
        else
        {
        	sum += 2.5;
        }
        
        //volume rated 1-5
        if(vol)
        {
        	double percent_change = ((price - old_price) / old_price) * 100;
        	double vol_percentage = (((double) week_volume / (avg_volume * 14) ) - 1) * 100;
        	//System.out.println(percent_change);
        	//System.out.println(vol_percentage);
        	if(percent_change > 0)
        	{
        		if(vol_percentage < 0)
        		{
        			if(vol_percentage > -10)
        				sum += 3;
        			else if(vol_percentage > -15)
        				sum += 2.5;
        			else if(vol_percentage > -20)
        				sum += 2;
        			else
        				sum += 1;
        		}
        		else
        		{
        			if(vol_percentage < 5)
        				sum += 3.5;
        			else if(vol_percentage < 10)
        				sum += 4;
        			else if(vol_percentage < 15)
        				sum += 4.5;
        			else
        				sum += 5;
        		}	
        	}
        	else
        	{
        		if(vol_percentage > 0)
        		{
        			if(vol_percentage < 2.5)
        				sum += 3;
        			else if(vol_percentage < 5)
        				sum += 2.5;
        			else if(vol_percentage < 7.5)
        				sum += 2;
        			else if(vol_percentage < 10)
        				sum += 1;
        		}
        		else
        		{
        			if(vol_percentage > -5)
        				sum += 3.5;
        			else if(vol_percentage > -7.5)
        				sum += 4;
        			else if(vol_percentage > -10)
        				sum += 4.5;
        			else
        				sum += 5;
        		}
        	}
        }
        else
        {
        	sum += 2.5;
        }
        if(dividends)
        	sum += .5;
        return sum;
	}
	
	public static Stock[] getSimilarStocks(StockRepository stockRepo, String symbol, double beta, String exchange) throws NumberFormatException, IOException
	{
		try
		{
			double b_low = beta * .85;
			double b_high = beta * 1.15;
			List<Stock> similar = stockRepo.findSimilar(exchange, symbol, b_low, b_high);
			if(similar.isEmpty())
			{
				return new Stock[0];
			}
		
			Stock[] topFour = new Stock[4];
			double[] ranks = new double[4];
			ranks[0] = 0; ranks[1] = 0; ranks[2] = 0; ranks[3] = 0;
			int count = 0;
			for(Stock stock : similar)
			{
				StockID s_id = stock.getStockID();
				double rank = getRank(s_id.getSymbol(), Double.valueOf(stock.getLastPrice().toString()));
				for(int i = 0; i < 4; i++)
				{
					if(ranks[i] < rank)
					{
						if(count < 4)
							count++;
						//shift over so it stays in order
						for(int j = 3; j >= i; j--)
						{
							if(j != 3)
							{
								ranks[j+1] = ranks[j];
								topFour[j+1] = topFour[j];
							}
						}
						ranks[i] = rank;
						topFour[i] = stock;
						break;
					}
				}
			}
			Stock[] returnVal = new Stock[count];
			for(int i = 0; i < count; i++)
			{
				returnVal[i] = topFour[i];
			}
			return returnVal;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			return new Stock[0];
		}
	}
	
}

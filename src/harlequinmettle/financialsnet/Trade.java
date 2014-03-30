package harlequinmettle.financialsnet;

import java.io.Serializable;

public class Trade implements Serializable {

	long timeOf;
	String typeOfTrade;
	String ticker;
	int rating;

	public Trade(String typeOf, String ticker, int rating) {
		timeOf = System.currentTimeMillis();
		this.typeOfTrade = typeOf;
		this.ticker = ticker;
		this.rating = rating;
		buyOrSell();
		MemoryManager.saveSettings();
	}

	private void buyOrSell() {
		if (typeOfTrade.equalsIgnoreCase("buy")) {
			EarningsTest.programSettings.myPortfolio.put(ticker, timeOf);
		} else if (typeOfTrade.equalsIgnoreCase("sell")) {
			if (EarningsTest.programSettings.myPortfolio.containsKey(ticker)) { 
				EarningsTest.programSettings.myPortfolio.remove(ticker);
			}
		}

	}
}

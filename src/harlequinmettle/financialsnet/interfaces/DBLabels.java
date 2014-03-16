package harlequinmettle.financialsnet.interfaces;

public interface DBLabels {
	String[] priorityLabeling = {
			//a hack but im using if for now minimum rank 2 because first one is technical data
			"",// 
		 
			"Market Cap", // 0
			"Dividends",//
			"52-Week Change",// 30
			"50-Day Moving Average",// ///////-----------69
			"200-Day Moving Average",// 32 /-----------70
			"Avg Vol (3 month)",// ---------------------71
			"Avg Vol (10 day)",// ---------------------72
			"Trailing P/E",// 2 ///--------------40
			"Forward P/E",// 3 //////-----------41
			"PEG Ratio", // 4
			"Price/Sales", //
			"Price/Book",//
			"1-yr forcast",// cnn - 1
			"Payout Ratio", // 43
			"Beta",//
			"Enterprise Value", //

			"EPS Est 12 Mo Ago",// these 4 use
			"EPS Est 9 Mo Ago",// these 4 use
			"EPS Est 6 Mo Ago",// these 4 use
			"EPS Est 3 Mo Ago",// these 4 use
			"EPS Actual 12 Mo Ago", //
			"EPS Actual 9 Mo Ago", //
			"EPS Actual 6 Mo Ago", //
			"EPS Actual 3 Mo Ago", //
			"Difference 12 Mo Ago", //
			"Difference 9 Mo Ago", //
			"Difference 6 Mo Ago", //
			"Difference 3 Mo Ago", //
			"Surprise % 12 Mo Ago",//
			"Surprise % 9 Mo Ago",//
			"Surprise % 6 Mo Ago", ///
			"Surprise % 3 Mo Ago",// ///////-----37
			// /////////////////////////////
			"Avg. Estimate Current Qt",// these 5 use
			"Avg. Estimate Next Qt",// these 5 use
			"Avg. Estimate Current Yr",// these 5 use
			"Avg. Estimate Next Yr",// these 5 use
			"No. of Analysts Current Qt",//
			"No. of Analysts Next Qt",//
			"No. of Analysts Current Yr",//
			"No. of Analysts Next Yr",//
			"Low Estimate Current Qt",//
			"Low Estimate Next Qt",//
			"Low Estimate Current Yr",//
			"Low Estimate Next Yr",//
			"High Estimate Current Qt", // /
			"High Estimate Next Qt", // /
			"High Estimate Current Yr", // /
			"High Estimate Next Yr", // /
			"Year Ago EPS Next Yr", // after
			"Year Ago EPS Current Qt", // after
			"Year Ago EPS Current Yr", // after
			"Year Ago EPS Next Yr", // after
			// this
			// key
			// substring
			// to
			// Revenue
			// Est


			// ////////////////////////////
			"Enterprise Value/Revenue",// 7
			"Enterprise Value/EBITDA ",//
			"Profit Margin",//
			"Operating Margin",//
			"Return on Assets", //
			"Return on Equity",//
			"Revenue", // 13
			"Revenue Per Share",//
			"Qtrly Revenue Growth",//
			"Gross Profit",// 16
			"EBITDA",//
			"Net Income Avl to Common", //
			"Diluted EPS",// 19
			"Qtrly Earnings Growth",//
			"Total Cash",//
			"Total Cash Per Share",// 22
			"Total Debt",//
			"Total Debt/Equity",//
			"Current Ratio", // 25
			"Book Value Per Share",//
			"Operating Cash Flow",// 27
			"Levered Free Cash Flow", //
			"Shares Outstanding",// 35
			"Float",//
			"% Held by Insiders",//
			"% Held by Institutions",// 38
			"Shares Short (as of",//
			"Short Ratio (as of",// 40
			"Short % of Float (as of",//
			"Shares Short (prior month)",//
			"CNN analysts",// cnn - 0
			// ///////////////////////////////////
			"Split",//
			"Options",//
			// //////////////////////////// 
			};
	String[] labels = {
			"CNN analysts",// cnn - 0
			"1-yr forcast",// cnn - 1
			// /////////////////////////////
			"Avg. Estimate Current Qt",// these 5 use
			"Avg. Estimate Next Qt",// these 5 use
			"Avg. Estimate Current Yr",// these 5 use
			"Avg. Estimate Next Yr",// these 5 use
			"No. of Analysts Current Qt",//
			"No. of Analysts Next Qt",//
			"No. of Analysts Current Yr",//
			"No. of Analysts Next Yr",//
			"Low Estimate Current Qt",//
			"Low Estimate Next Qt",//
			"Low Estimate Current Yr",//
			"Low Estimate Next Yr",//
			"High Estimate Current Qt", // /
			"High Estimate Next Qt", // /
			"High Estimate Current Yr", // /
			"High Estimate Next Yr", // /
			"Year Ago EPS Next Yr", // after
			"Year Ago EPS Current Qt", // after
			"Year Ago EPS Current Yr", // after
			"Year Ago EPS Next Yr", // after
			// this
			// key
			// substring
			// to
			// Revenue
			// Est

			"EPS Est 12 Mo Ago",// these 4 use
			"EPS Est 9 Mo Ago",// these 4 use
			"EPS Est 6 Mo Ago",// these 4 use
			"EPS Est 3 Mo Ago",// these 4 use
			"EPS Actual 12 Mo Ago", //
			"EPS Actual 9 Mo Ago", //
			"EPS Actual 6 Mo Ago", //
			"EPS Actual 3 Mo Ago", //
			"Difference 12 Mo Ago", //
			"Difference 9 Mo Ago", //
			"Difference 6 Mo Ago", //
			"Difference 3 Mo Ago", //
			"Surprise % 12 Mo Ago",//
			"Surprise % 9 Mo Ago",//
			"Surprise % 6 Mo Ago", ///
			"Surprise % 3 Mo Ago",// ///////-----37

			// ////////////////////////////
			"Market Cap", // 0
			"Enterprise Value", //
			"Trailing P/E",// 2 ///--------------40
			"Forward P/E",// 3 //////-----------41
			"PEG Ratio", // 4
			"Price/Sales", //
			"Price/Book",//
			"Enterprise Value/Revenue",// 7
			"Enterprise Value/EBITDA ",//
			"Profit Margin",//
			"Operating Margin",//
			"Return on Assets", //
			"Return on Equity",//
			"Revenue", // 13
			"Revenue Per Share",//
			"Qtrly Revenue Growth",//
			"Gross Profit",// 16
			"EBITDA",//
			"Net Income Avl to Common", //
			"Diluted EPS",// 19
			"Qtrly Earnings Growth",//
			"Total Cash",//
			"Total Cash Per Share",// 22
			"Total Debt",//
			"Total Debt/Equity",//
			"Current Ratio", // 25
			"Book Value Per Share",//
			"Operating Cash Flow",// 27
			"Levered Free Cash Flow", //
			"Beta",//
			"52-Week Change",// 30
			"50-Day Moving Average",// ///////-----------69
			"200-Day Moving Average",// 32 /-----------70
			"Avg Vol (3 month)",// ---------------------71
			"Avg Vol (10 day)",// ---------------------72
			"Shares Outstanding",// 35
			"Float",//
			"% Held by Insiders",//
			"% Held by Institutions",// 38
			"Shares Short (as of",//
			"Short Ratio (as of",// 40
			"Short % of Float (as of",//
			"Shares Short (prior month)",//
			"Payout Ratio", // 43
			// ///////////////////////////////////
			"Dividends",//
			"Split",//
			"Options",//
			// //////////////////////////// 
			};
	String[] COMPUTED = {///
			"PEF/PET",//
			"50d/200d",//
			"v",//
			"vv",//
			"xxx",//
			"xxxx",//
			"xxxxx",//
			"zz",//
			"zzzz",//
			"zzzzzz",//
			
			
	};
			String[] TECHNICAL = {///
			"day(float)",//0
			"open",//1
			"high",//2
			"low",//3
			"close",//4
			"volume",//5
			"adjClose",//6

	};
}

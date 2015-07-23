# jptask
## Source file description ##

* Main.java	

	Simple multi-threaded program that uses the StockExchange class.

* StockTradeAction.java

	Class that describes a stock trade action, this class will be used in StockElement class for storing trade actions for that stock

* StockTradeActionTest.java

	JUnit test case for the StockTradeAction class
	
* StockElement.java

	Class that describes a stock, it contains all it's properties including a list of trades that were performed for that stock. This class will only be used in the StockExchange class below

* StockElementTest.java

	JUnit test case for the StockElement class

* StockExchange.java

	Base class for the stock exchange exercise program, this class doesn't have many properties, the most important of which is the table of the available stocks in this exchange

* StockExchangeTest.java
	
	JUnit test case for the StockElement class
import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

public class StockElementTest {
	
	StockElement myStock;
	
	@Before
	public void setup() {
		
		// setup the stock element to be used with all of the following test
		myStock = new StockElement("ABC", StockElement.StockTypes.COMMON, new BigDecimal(5), new BigDecimal(2), new BigDecimal(100));
	}
	
	@Test
	public void testGetStockSymbol() {
		
		// Check if the stock symbol was correctly set
		assertEquals("ABC", myStock.getStockSymbol());
		assertNotEquals("CBA", myStock.getStockSymbol());
	}

	@Test
	public void testGetAndSetStockType() {
		
		//Check if the initial stock type was correctly set and we can change it
		assertEquals("COMMON", myStock.getStockType().name());
		myStock.setStockType(StockElement.StockTypes.PREFERRED);
		assertEquals("PREFERRED", myStock.getStockType().name());
	}

	@Test
	public void testGetAndSetLastDividend() {
		
		// Check if the initial last dividend value was correctly set and we can change it
		assertEquals(new BigDecimal(5), myStock.getLastDividend());
		myStock.setLastDividend(new BigDecimal(8));
		assertEquals(new BigDecimal(8), myStock.getLastDividend());
	}

	@Test
	public void testGetAndSetFixedDividend() {
		
		// Check if the initial fixed dividend value was correctly set and we can change it
		assertEquals(new BigDecimal(2), myStock.getFixedDividend());
		myStock.setFixedDividend(new BigDecimal(5));
		assertEquals(new BigDecimal(5), myStock.getFixedDividend());
	}


	@Test
	public void testGetAndSetParValue() {
		
		// Check if the initial par value value was correctly set and we can change it
		assertEquals("Initial par value ", new BigDecimal(100), myStock.getParValue());
		myStock.setParValue(new BigDecimal(10));
		assertEquals("Par value after change ",new BigDecimal(10), myStock.getParValue());
	}
	
	@Test
	public void testAddAndGetTradeActions() {
		
		// first we ensure there are no actions recorded
		assertEquals("Initial action set size ", 0, myStock.getTradeActions().size());
		
		// we then add a BUY action
		myStock.addTradeAction(100, StockTradeAction.actionTypes.BUY, new BigDecimal("50.5"));
		
		// we will then ensure that now we have one transaction and test it's details to ensure they were correctly set
		assertEquals("Size after the BUY action ", 1, myStock.getTradeActions().size());
		
		assertEquals("Type of the trade performed ", StockTradeAction.actionTypes.BUY, myStock.getTradeActions().get(0).getAction());
		assertEquals("Quantity of shares ", 100, myStock.getTradeActions().get(0).getQuantity());
		assertEquals("Value of the transaction (per share) ", new BigDecimal("50.5"), myStock.getTradeActions().get(0).getValue());
		
		// now we sell some shares and will check if the operation was successful
		myStock.addTradeAction(500, StockTradeAction.actionTypes.SELL, new BigDecimal("60"));
		
		assertEquals("Size after the SELL action ", 2, myStock.getTradeActions().size());
		
		assertEquals("Type of the trade performed ", StockTradeAction.actionTypes.SELL, myStock.getTradeActions().get(1).getAction());
		assertEquals("Quantity of shares ", 500, myStock.getTradeActions().get(1).getQuantity());
		assertEquals("Value of the transaction (per share) ", new BigDecimal("60"), myStock.getTradeActions().get(1).getValue());
	}

	@Test
	public void testGetTickerPrice() {
		
		//Check the ticker value before and after a trade action
		assertEquals("Initial ticker value ", new BigDecimal(100), myStock.getTickerPrice());
		
		myStock.addTradeAction(100, StockTradeAction.actionTypes.BUY, new BigDecimal(50.5));
		
		assertEquals("New ticker value ", new BigDecimal(50.5), myStock.getTickerPrice());
	}

	@Test
	public void testGetDividendYield() {
		
		// Test for the initial value (lastDividend=5, tickerPrice=100)
		assertEquals("Initial dividend yield ", new BigDecimal("0.050"), myStock.getDividendYield());
		
		// change the stock type to preferred and so we will use the values (fixedDividend = 2%, parValue=100, tickerPrice=100)
		myStock.setStockType(StockElement.StockTypes.PREFERRED);
		
		assertEquals("Dividend yield after changing type ", new BigDecimal("0.020"), myStock.getDividendYield());
		
		myStock.addTradeAction(100, StockTradeAction.actionTypes.BUY, new BigDecimal("95.55"));
		
		// With the previous trade the ticker price decreased from 100 to 95.55
		assertEquals("Dividend yield after trade ", new BigDecimal("0.021"), myStock.getDividendYield());
	}

	@Test
	public void testGetPERatio() {
		
		// Test that we get the correct P/E Ratio for the initial values (tickerPrice=100, lastDividend=5)
		assertEquals("Initial P/E ratio ", new BigDecimal("20.000"), myStock.getPERatio());
		
		// Add a trade to change the ticker price
		myStock.addTradeAction(100, StockTradeAction.actionTypes.BUY, new BigDecimal("95.55"));
		
		// ensure we get the correct value
		assertEquals("New P/E ratio ", new BigDecimal("19.110"), myStock.getPERatio());
	}

	@Test
	public void testCalculateStockPrice() {
		
		myStock.addTradeAction(System.currentTimeMillis(), 100, StockTradeAction.actionTypes.BUY, new BigDecimal("95"));
		
		//in this test I am using the compareTo method of the BigDecimal class so I can compare the actual value and not having to write 95.000
		assertEquals("Stock value is correct with one transaction ", 0, myStock.calculateStockPrice().compareTo(new BigDecimal("95")));
		
		myStock.addTradeAction(System.currentTimeMillis(), 100, StockTradeAction.actionTypes.BUY, new BigDecimal("85"));
		
		//Here the value of the stock price has to be 90
		assertEquals("Stock value is correct with two transactions ", 0, myStock.calculateStockPrice().compareTo(new BigDecimal("90")));
		
		//if we add an action that is older than 15 minutes it should not be accounted for here we are setting a timestamp of the current time minus 20 minutes
		myStock.addTradeAction((System.currentTimeMillis()-1200000), 300, StockTradeAction.actionTypes.BUY, new BigDecimal("200"));
		
		//Here the value of the stock price should still be 90 as the last trade added will not be used in the calculation
		assertEquals("Stock value is correct with two transactions ", 0, myStock.calculateStockPrice().compareTo(new BigDecimal("90")));
	}

}

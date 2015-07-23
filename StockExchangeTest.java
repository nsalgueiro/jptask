import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

public class StockExchangeTest {

	@Test
	public void testAddGetAndRemoveStock() {
		
		// First we will create an instance of our stock exchange class
		StockExchange myStockExchange = new StockExchange();
		
		// The stock exchange has no stocks yet so this should return null
		assertNull(myStockExchange.getStock("TEA"));
		
		// Let's add one type of stock
		myStockExchange.addStock("TEA", StockElement.StockTypes.COMMON, new BigDecimal(0), new BigDecimal(0), new BigDecimal(100));
		
		// And now the stock must exist
		assertNotNull(myStockExchange.getStock("TEA"));
		
		// These two tests are just to ensure we are retrieving the right element, the test of ensuring all the values are correct when created is being done in the StockElementTest class
		assertEquals("Expecting the stock symbol of TEA to be correct ", "TEA", myStockExchange.getStock("TEA").getStockSymbol());
		assertEquals("Expecting the par value of TEA to be correct ", 0, myStockExchange.getStock("TEA").getParValue().compareTo(new BigDecimal("100")));
		
		myStockExchange.removeStock("TEA");
		
		// The TEA stock should no longer exist
		assertNull(myStockExchange.getStock("TEA"));		
	}

	@Test
	public void testCalculateGBCEAllShareIndex() {
		
		// First we will create an instance of our stock exchange class
		StockExchange myStockExchange = new StockExchange();
		
		// To start this test we will had the stocks from the example table
		myStockExchange.addStock("TEA", StockElement.StockTypes.COMMON, new BigDecimal(0), new BigDecimal(0), new BigDecimal(100));
		myStockExchange.addStock("POP", StockElement.StockTypes.COMMON, new BigDecimal(8), new BigDecimal(0), new BigDecimal(100));
		myStockExchange.addStock("ALE", StockElement.StockTypes.COMMON, new BigDecimal(23), new BigDecimal(0), new BigDecimal(60));
		myStockExchange.addStock("GIN", StockElement.StockTypes.PREFERRED, new BigDecimal(8), new BigDecimal(2), new BigDecimal(100));
		myStockExchange.addStock("JOE", StockElement.StockTypes.COMMON, new BigDecimal(13), new BigDecimal(0), new BigDecimal(250));
		
		// If there are no trades available we will receive null from the calculateGBCEAllShareIndex method, this is by design and can be changed
		assertNull("GBCE All share index is null if no trades are available", myStockExchange.calculateGBCEAllShareIndex());
		
		//now let's add some trades for each of them to set their value
		myStockExchange.getStock("TEA").addTradeAction(100, StockTradeAction.actionTypes.BUY, new BigDecimal("80"));
		myStockExchange.getStock("TEA").addTradeAction(100, StockTradeAction.actionTypes.BUY, new BigDecimal("90"));
		// if this is working well the stock value for TEA should be 85
		assertEquals("TEA stock price is correct ", 0, myStockExchange.getStock("TEA").calculateStockPrice().compareTo(new BigDecimal("85")));
		
		myStockExchange.getStock("POP").addTradeAction(200, StockTradeAction.actionTypes.BUY, new BigDecimal("1"));
		myStockExchange.getStock("POP").addTradeAction(200, StockTradeAction.actionTypes.BUY, new BigDecimal("7"));
		// if this is working well the stock value for POP should be 4
		assertEquals("POP stock price is correct ", 0, myStockExchange.getStock("POP").calculateStockPrice().compareTo(new BigDecimal("4")));
		
		assertEquals("GBCE All share index is for TEA and POP is correct ", 0, myStockExchange.calculateGBCEAllShareIndex().compareTo(new BigDecimal("18.439")));
		
		// now lets add some trades to the other stocks
		myStockExchange.getStock("ALE").addTradeAction(50, StockTradeAction.actionTypes.BUY, new BigDecimal("30"));
		myStockExchange.getStock("ALE").addTradeAction(150, StockTradeAction.actionTypes.BUY, new BigDecimal("90"));
		// and confirm this value is correct as well  
		assertEquals("ALE stock price is correct ", 0, myStockExchange.getStock("ALE").calculateStockPrice().compareTo(new BigDecimal("75")));
		
		// Since we only have one trade for each of the following stocks their value will be respectively GIN = 20 and JOE = 35
		myStockExchange.getStock("GIN").addTradeAction(200, StockTradeAction.actionTypes.BUY, new BigDecimal("20"));
		myStockExchange.getStock("JOE").addTradeAction(200, StockTradeAction.actionTypes.BUY, new BigDecimal("35"));

		// 5th root of 17850000 (85*35*20*75*4) = 28.205 so lets see if our calculations are correct
		assertEquals("GBCE All share index is correct ", new BigDecimal("28.205"), myStockExchange.calculateGBCEAllShareIndex());
	}

}

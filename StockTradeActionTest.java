import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;


public class StockTradeActionTest {

	@Test
	public void testContructorAndValueRetrieval() {
		
		StockTradeAction myAction = new StockTradeAction(1000, StockTradeAction.actionTypes.BUY, new BigDecimal(6.6585));
		
		// Test if we have the correct type
		assertEquals("Action is correct ", myAction.getAction().name(), "BUY");
		
		// Test if the quantity is correct
		assertEquals("Quantity is correct ", myAction.getQuantity(), 1000);

		// This sleep is only to ensure that some time has passed before the next test or else it would fail since the action of creating a new stock trade action takes less than 1 milisecond
		try {
			
			Thread.sleep(10);  

		} catch (InterruptedException e) {

			fail("Interrupted exception");
		}
		
		// We ensure that the timestamp value was correctly filled by the constructor
		assertTrue(myAction.getTimestamp()<System.currentTimeMillis());
	}
	
	@Test
	public void testFourParameterContructorAndValueRetrieval() {
		
		long currentTimeStamp = System.currentTimeMillis();
		
		StockTradeAction myAction = new StockTradeAction(currentTimeStamp, 1000, StockTradeAction.actionTypes.SELL, new BigDecimal(6.6585));
		
		// Test if we have the correct type
		assertEquals("Action is correct ", myAction.getAction().name(), "SELL");
		
		// Test if the quantity is correct
		assertEquals("Quantity is correct ", myAction.getQuantity(), 1000);

		// This sleep is only to ensure that some time has passed and we are not using a wrong value for the timestamp in the next test
		try {
			
			Thread.sleep(10);  

		} catch (InterruptedException e) {

			fail("Interrupted exception");
		}
		
		// We ensure that the timestamp value was correctly filled by the constructor
		assertTrue(myAction.getTimestamp()==currentTimeStamp);
	}

}

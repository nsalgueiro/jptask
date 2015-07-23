import java.math.BigDecimal;
import java.util.Vector;

public class StockElement {

	public static enum StockTypes {COMMON, PREFERRED}; 

	// Both of the following values could be read from a configuration file, I'm using this values as I  knowledge regarding stocks is really limited and I don't know what it is usually used
	private static final int _PRECISION = 3;	// The precision of the division we will use with the BigDecimal operations in this exercise, it could be adjusted
	private static final int _ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;	// The rounding type of BigDecimal operations

	private String stockSymbol;
	private StockTypes stockType;
	private BigDecimal lastDividend;
	private BigDecimal fixedDividend;
	private BigDecimal parValue;
	private BigDecimal tickerPrice;

	private Object stockValuesChangeLock;

	private Vector<StockTradeAction> tradeActions;

	public StockElement(String stockSymbol, StockTypes stockType,
			BigDecimal lastDividend, BigDecimal fixedDividend, BigDecimal parValue) {
		super();

		this.stockSymbol = stockSymbol;

		this.stockType = stockType;
		this.fixedDividend = fixedDividend;
		this.lastDividend = lastDividend;
		this.parValue = parValue;

		this.tickerPrice = parValue;	// This assumption to start the ticker with the par value of the share must not be correct but I'm not familiar with stock trading so this seemed the logical value
		// Beyond this point the ticket price will be updated with the value of the last trade action, once again I assume that this is the correct behaviour

		this.tradeActions = new Vector<StockTradeAction>();

		this.stockValuesChangeLock = new Object(); // Lock object that will be used when changes are being made to stockType, lastDividend, fixedDividend or parValue
		//ticker price will only be changed internally when we call one of the addTradeAction methods and both of them will have a lock on the tradeActions object so a lock in this object will not be required
		//Stock symbol is just a text reference so any changes to it won't have any impact as well
	}

	/**
	 * Getter for the stock symbol, for this variable I assume that the stock symbol never changes so we provide no setter method
	 * @return String with the current Stock Symbol
	 */
	public String getStockSymbol() {
		return stockSymbol;
	}

	/**
	 * Getter method for retrieving the stock type
	 * @return current stock type
	 */
	public StockTypes getStockType() {
		return stockType;
	}

	/**
	 * Setter method for setting the stock type
	 * @param pStockType New stock type
	 */
	public void setStockType(StockTypes pStockType) {

		synchronized (stockValuesChangeLock) {
			this.stockType = pStockType;
		}
	}


	/**
	 * Getter method for retrieving the LastDividend value
	 * @return BigDecimal containing the last dividend value
	 */
	public BigDecimal getLastDividend() {
		return lastDividend;
	}

	/**
	 * Setter method to set the LastDividend value
	 * @param pLastDividend BigDecimal containing the new lastDividend value
	 */
	public void setLastDividend(BigDecimal pLastDividend) {

		synchronized (stockValuesChangeLock) {
			this.lastDividend = pLastDividend;	
		}
	}


	/**
	 * Getter method for retrieving the fixedDividend value
	 * @return BigDecimal containing the fixed dividend value
	 */
	public BigDecimal getFixedDividend() {
		return fixedDividend;
	}

	/**
	 * Setter method to set the fixedDividend value
	 * @param pFixedDividend BigDecimal containing the new fixedDividend value
	 */
	public void setFixedDividend(BigDecimal pFixedDividend) {

		synchronized (stockValuesChangeLock) {
			this.fixedDividend = pFixedDividend;
		}
	}


	/**
	 * Getter method for retrieving the getParValue value
	 * @return BigDecimal containing the current par value
	 */
	public BigDecimal getParValue() {
		return parValue;
	}

	/**
	 * Setter method to set the parValue value
	 * @param pParValue BigDecimal containing the new par value
	 */
	public void setParValue(BigDecimal pParValue) {

		synchronized (stockValuesChangeLock) {
			this.parValue = pParValue;
		}
	}


	/**
	 * Getter method for the tradeActions variable
	 * @return Vector of TradeAction objects containing all the trade actions for this stock
	 */
	public Vector<StockTradeAction> getTradeActions() {

		return this.tradeActions;
	}

	/**
	 * Getter method for the tickerPrice variable
	 * @return BigDecimal containing the current ticker price
	 */
	public BigDecimal getTickerPrice() {

		return tickerPrice;	
	}

	/**
	 * Method to retrieve the dividend yield
	 * @return BigDecimal representing the current dividend yield of the share or null if the ticker price is 0 or if the stockType is different from COMMON or PREFERRED
	 */
	public BigDecimal getDividendYield() {

		synchronized (stockValuesChangeLock) {	// We use this lock to prevent changes to the variables used in this method for calculations so we will always have coherent values

			switch(this.stockType) {

			case COMMON : {

				if(this.tickerPrice.floatValue()>0) {	// We must ensure that the value is not zero to avoid an Arithmetic exception

					return lastDividend.divide(tickerPrice, _PRECISION, _ROUNDING_MODE);

				} else {

					System.err.println("Error, the tickerPrice value must not be 0");
					return null;
				}
			} 
			case PREFERRED : {

				if(this.tickerPrice.floatValue()>0) {	// We must ensure that the value is not zero to avoid an Arithmetic exception

					return fixedDividend.divide(new BigDecimal(100), _PRECISION, _ROUNDING_MODE).multiply(parValue).divide(tickerPrice, _PRECISION, _ROUNDING_MODE);	// Since we accept the fixed dividend as a percentage we have to divide it's value by 100 for the calculation

				} else {

					System.err.println("Error, the tickerPrice value must not be 0");
					return null;
				}
			}

			default : {	// We should never get here since the value of stockType has to be one from the enumeration

				System.err.println("Incorrect stock type found, it should be (C)ommon or (P)referred");

				return null;
			}
			}
		}
	}

	/**
	 * Method to get the P/E Ratio of the share
	 * @return BigDecimal representing the current P/E Ration of the hare or null if the lastDividend value is 0
	 */
	public BigDecimal getPERatio() {

		synchronized (stockValuesChangeLock) {	// We use this lock to prevent changes to the variables used in this method for calculations so we will always have coherent values

			if(this.lastDividend.floatValue()!=0) {	// We must ensure that the value is not zero to avoid an Arithmetic exception

				return tickerPrice.divide(lastDividend, _PRECISION, _ROUNDING_MODE);

			} else {

				System.err.println("Error, the lastDividend value must not be 0");
				return null;
			}
		}
	}

	/**
	 * Method to add a new trade action for this share
	 * @param timestamp
	 * @param quantity
	 * @param actionType
	 * @param value
	 * @return Boolean value indicating success or failure adding the trade action
	 */
	public boolean addTradeAction(long timestamp, long quantity, StockTradeAction.actionTypes actionType, BigDecimal value) {

		boolean validationsOK = true;

		if(timestamp<=0) {

			System.err.println("Invalid argument timestamp, it must be greater than 0");
			validationsOK = false;
		}

		if(quantity<=0) {
			System.err.println("Invalid argument quantity, it must be greater than 0");
			validationsOK = false;			
		}

		if(actionType==null) {

			System.err.println("Invalid parameter actionType, it cannot be null");
		}

		if(value.doubleValue()<=0) {
			System.err.println("Invalid argument quantity, it must be greater than 0");
			validationsOK = false;			
		}

		if(validationsOK) {
			
			synchronized (tradeActions) {	// we will place a lock in this object to prevent it from being changed during this operation
			
				tradeActions.addElement(new StockTradeAction(timestamp, quantity, actionType, value));
				this.tickerPrice = value;
			}
		} 

		return validationsOK;
	}

	/**
	 * Method similar to the previous one but it uses system time for filling the time stamp data instead of using a parameter
	 * @param quantity
	 * @param actionType
	 * @param value
	 * @return Boolean value indicating success or failure adding the trade action
	 */
	public boolean addTradeAction(long quantity, StockTradeAction.actionTypes actionType, BigDecimal value) {

		boolean validationsOK = true;

		if(quantity<=0) {
			System.err.println("Invalid argument quantity, it must be greater than 0");
			validationsOK = false;			
		}

		if(actionType==null) {

			System.err.println("Invalid parameter actionType, it cannot be null");
		}

		if(value.doubleValue()<=0) {
			System.err.println("Invalid argument quantity, it must be greater than 0");
			validationsOK = false;			
		}

		if(validationsOK) {
			
			synchronized (tradeActions) {	// we will place a lock in this object to prevent it from being changed during this operation
				
				tradeActions.addElement(new StockTradeAction(quantity, actionType, value));
				this.tickerPrice = value;
				
			}
		} 

		return validationsOK;
	}

	/**
	 * Method to calculate the stock price based in the trades of the last 15 minutes
	 * @return BigDecimal with the current stock price or null if there has been no transactions for this stock
	 */
	public BigDecimal calculateStockPrice() {

		// We will use these two variables to store the values for the operation
		long stockQuantity = 0;
		BigDecimal priceTimesQuantitySum = new BigDecimal(0);

		synchronized (tradeActions) {	// we will place a lock in this object to prevent it from being changed during this operation

			// We will go through all trade records
			for (int i=0; i<tradeActions.size(); i++) {

				// Check if they are newer than 15 minutes and if so add their value to both variables
				if (((System.currentTimeMillis()-tradeActions.get(i).getTimestamp())/1000/60)<=15) {

					stockQuantity += tradeActions.get(i).getQuantity();
					priceTimesQuantitySum = priceTimesQuantitySum.add(tradeActions.get(i).getValue().multiply(new BigDecimal(tradeActions.get(i).getQuantity())));
				}
			}

		}

		// here we will check if we have any stocks traded or give an error otherwise
		if (stockQuantity>0) {

			return priceTimesQuantitySum.divide(new BigDecimal(stockQuantity), _PRECISION, _ROUNDING_MODE);

		} else {

			System.err.println("No actions for this stock");
			return null;
		}

	}

	@Override
	public String toString() {
		return "StockElement [stockSymbol=" + stockSymbol 
				+ ", stockType=" + stockType.name() 
				+ ", lastDividend=" + lastDividend.setScale(_PRECISION, _ROUNDING_MODE)
				+ ", fixedDividend=" + fixedDividend.setScale(_PRECISION, _ROUNDING_MODE)
				+ ", parValue=" + parValue.setScale(_PRECISION, _ROUNDING_MODE)
				+ ", tickerPrice=" + tickerPrice.setScale(_PRECISION, _ROUNDING_MODE) 
				+ ", tradeActions=" + tradeActions.size()
				+ ", stockPrice=" + calculateStockPrice()
				+ "]";
	}
}

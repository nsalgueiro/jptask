import java.math.BigDecimal;

/**
 * This is the class defining the trade operations for the shares, 
 * it only has implemented the getter methods since it would make no sense in changing an action of this type after it has been created
 * @author nsalgueiro
 *
 */
public class StockTradeAction {
	
	public static enum actionTypes {BUY, SELL};
	
	private long timestamp;		// Timestamp of operation
	private long quantity;		// Trade quantity
	private actionTypes action; // Trade type - B or S for respectively buy or sell
	private BigDecimal value;	// Trade value
	
	/**
	 * Constructor for the class that takes in all of the four values for the trade action
	 * @param timestamp	- timestamp of the operation
	 * @param quantity	- Quantity of shares bought or sold
	 * @param action	- Action performed (B or S for respectively buy or sale)
	 * @param value		- Share value
	 */
	public StockTradeAction(long timestamp, long quantity, actionTypes action,	BigDecimal value) {
		super();
		this.timestamp = timestamp;
		this.quantity = quantity;
		this.action = action;
		this.value = value;
	}
	
	/**
	 * This is another constructor for the class that relies on the system time to automatically fill the timestamp field
	 * @param quantity	- Quantity of shares bought or sold
	 * @param action	- Action performed (B or S for respectively buy or sale)
	 * @param value		- Share value
	 */
	public StockTradeAction(long quantity, actionTypes action, BigDecimal value) {
		
		this.timestamp = System.currentTimeMillis();
		
		this.quantity = quantity;
		this.action = action;
		this.value = value;
	}
	
	/**
	 * Method to retrieve the time stamp for this trade action
	 * @return
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Method to retrieve the quantity of shares bought or sold
	 * @return
	 */
	public long getQuantity() {
		return quantity;
	}

	/**
	 * Method to retrieve the action type
	 * @return
	 */
	public actionTypes getAction() {
		return action;
	}
	
	/**
	 * Method to retrieve the value of the shares
	 * @return
	 */
	public BigDecimal getValue() {
		return value;
	}

	/**
	 * Method to return the content of this trade action on a readable format
	 * @return String containing this trade action properties
	 */
	@Override
	public String toString() {
		return "StockTradeAction [timestamp=" + timestamp + ", action=" + action + ", quantity=" + quantity + ", value=" + value + "]";
	}
}

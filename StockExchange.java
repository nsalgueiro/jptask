import java.math.BigDecimal;
import java.util.Hashtable;

/**
 * Class representing a very simple stock exchange that contains a table with the available stocks and some methods to add, remove, get and Calculacte the GBCE All Share index.
 * @author nsalgueiro
 *
 */
public class StockExchange {

	// Both of the following values could be read from a configuration file, I'm using this values as I  knowledge regarding stocks is really limited and I don't know what it is usually used
	private static final int _PRECISION = 3;	// The precision of the division we will use with the BigDecimal operations in this exercise, it could be adjusted
	private static final int _ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;	// The rounding type of BigDecimal operations

	// Hashtable that will contain the stocks
	private Hashtable<String, StockElement> stocks;

	/**
	 * Constructor for the StockExchange class
	 */
	public StockExchange() {

		stocks = new Hashtable<String, StockElement>();
	}

	/**
	 * Method to add some stock to the stock exchange
	 * @param stockSymbol	- The symbol of the stock
	 * @param stockType		- The stock type (Common or Preferred)
	 * @param lastDividend	- Value of the last dividend
	 * @param fixedDividend	- Value of the fixed dividend (in percentage) only used for preferred stocks
	 * @param parValue		- The par value of the stock
	 * @return boolean value indicating success or failure of the action
	 */
	public boolean addStock(String stockSymbol, StockElement.StockTypes stockType, BigDecimal lastDividend, BigDecimal fixedDividend, BigDecimal parValue) {

		// Validation of the stock symbol, we will not allow null or empty values
		if(stockSymbol==null || stockSymbol.equals("")) {

			System.err.println("Stock symbol cannot be empty");
			return false;
		}

		// Validation of the other parameters to ensure we have values for each of them
		if(stockType==null || lastDividend==null || fixedDividend==null || parValue==null) {

			System.err.println("No null values allowed");
			return false;			
		}

		// Check if a stock with that key already exists, if it doesn't exist we will create it and return true, if it does we will show an error message and return false
		
		synchronized (stocks) {	// it may look that we don't need to synchronize the add operation but we could have problems of two threads trying to add the same stock (with the same stock symbol) at the same time and we wouldn't know which of them was successful 
			
			if(!stocks.containsKey(stockSymbol)) {

				stocks.put(stockSymbol, new StockElement(stockSymbol, stockType, lastDividend, fixedDividend, parValue));
				return true;

			} else {

				System.err.println("Stock already exists");
				return false;
			}
		}

	}

	/**
	 * Method to remove a stock from the stock exchange
	 * @param stockSymbol - Symbol of the stock to be removed
	 * @return boolean value indicating success or failure of the action
	 */
	public boolean removeStock(String stockSymbol) {

		// Check if the symbol is null or empty
		if(stockSymbol==null || stockSymbol.equals("")) {

			System.err.println("Stock symbol cannot be empty");
			return false;

		} else {

			synchronized (stocks) {	// we will ensure that no other method is changing the stocks while we do this operation

				if(stocks.containsKey(stockSymbol)) {	// If the stock symbol exists we will remove it and return true

					stocks.remove(stockSymbol);
					return true;

				} else {	// Otherwise we show the error message and return false

					System.err.println("No stock found");
					return false;
				}
			}
		}
	}

	/**
	 * Method to retrieve a stock element based on the Stock Symbol
	 * @param stockSymbol
	 * @return the stock element or null if the element was not found
	 */
	public StockElement getStock(String stockSymbol) {

		// Check if the symbol is null or empty
		if(stockSymbol==null || stockSymbol.equals("")) {

			System.err.println("Stock symbol cannot be empty");
			return null;

		} else {

			synchronized (stocks) {	// we will ensure that no other method is changing the stocks while we do this operation

				if(stocks.containsKey(stockSymbol)) { // If the stock symbol exists we will return the corresponding object

					return (stocks.get(stockSymbol));

				} else { // Otherwise we will show an error message and return null 

					System.err.println("No stock found");
					return null;
				}				
			}

		}
	}

	/**
	 * Method to calculate the GBCE All share index
	 * @return BigDecimal containing the index value or null if there are no trades for any of the stocks
	 */
	public BigDecimal calculateGBCEAllShareIndex() {

		// Variables to store both temporary values for later be used in the nth root calculation
		BigDecimal aux = new BigDecimal(1);	// This is initially set to 1 since will be used to store the result of the multiplication of all elements
		int shareSymbolNumber = 0; 			// This variable will hold the number of different stocks to be used in the calculation

		synchronized (stocks) {	// we will ensure that no other method is changing the stocks while we do this operation

			// now we will loop through all of the stocks and store the respective values if there is a stock price for that given stock
			for(String key: stocks.keySet()) {

				if(stocks.get(key).calculateStockPrice()!=null) {

					aux = aux.multiply(stocks.get(key).calculateStockPrice());
					shareSymbolNumber ++;

				} else {

					System.err.println("No stock value for " + key);
				}
			}

		} // Now that we have the values we can release this lock

		// Variable were the result of the operation will be stored
		BigDecimal result = null;

		// We must ensure that we have at least the stock value for one kind of stock and if we do we can calculate the value
		if(shareSymbolNumber>0) {

			result = new BigDecimal(Math.pow(aux.doubleValue(), 1.0/shareSymbolNumber)).setScale(_PRECISION, _ROUNDING_MODE);
		}

		return result;
	}
	
	public void displayStatusForAllStocks() {

		System.out.println("*****************************");
		System.out.println("*** Status for all stocks ***");
		System.out.println("*****************************");
		
		synchronized (stocks) {	// we will ensure that no other method is changing the stocks while we do this operation
			
			// now we will loop through all of the stocks and show it's contents
			for(String key: stocks.keySet()) {

				System.out.println(stocks.get(key));
			}

		} // Now that we have the values we can release this lock
		
		System.out.println();
	}

}

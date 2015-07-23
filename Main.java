import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This is a simple multi-threading class that uses the StockExchange we will launch a series of threads that will randomly perform some actions in the class
 * @author nsalgueiro
 *
 */
public class Main implements Runnable {

	private static final int _NUMBER_OF_THREADS = 20000;
	private static final String[] stockSymbols = {"TEA", "POP", "ALE", "GIN", "JOE", "ABC", "BCD", "CDE", "DEF", "EFG"};
	
	private StockExchange myStockExchange;
	
	private void execute() {
		
		long startTime = System.currentTimeMillis();
		
		// First we will create an instance of our stock exchange class
		myStockExchange = new StockExchange();
		
		ThreadGroup tg = new ThreadGroup("StocksThreadGroup");
		
		// we will launch a given number of threads
		for(int i = 0; i<_NUMBER_OF_THREADS; i++) {
			
			new Thread(tg, this).start();
		}
		
		// now we will wait for all of them to finish
		while (tg.activeCount()>0) {
			
			try {
			
				Thread.sleep(200);

			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			
		}
		
		// Show the final status
		myStockExchange.displayStatusForAllStocks();
		
		// finally we will calculate the GBCE all share index
		System.out.println("The final GBCE all share index is : " + myStockExchange.calculateGBCEAllShareIndex());
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("Took " + ((endTime-startTime)/1000) + " seconds and " + ((endTime-startTime)%1000) + " miliseconds to process the results for " + _NUMBER_OF_THREADS + " threads");
	}
	
	public static void main(String[] args) {
		
		Main stockExchangeMain = new Main();
		
		stockExchangeMain.execute();
		
		System.exit(0);
	}

	@Override
	public void run() {
		
		ThreadLocalRandom random = ThreadLocalRandom.current();
		
		System.out.println("[" + Thread.currentThread().getName() + "] has started");
		
		// before starting each thread will sleep for a random number of miliseconds just to make things more real and not all of them starting at the same time
		try {
			Thread.sleep(random.nextInt(50));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		int rnd = random.nextInt(stockSymbols.length);
		
		// Each thread will initially try to add a new stock from a stock symbol in the test array
		System.out.println("[" + Thread.currentThread().getName() + "] creating stock " + stockSymbols[rnd]);
		
		Boolean creationResult = myStockExchange.addStock(stockSymbols[rnd], StockElement.StockTypes.COMMON, new BigDecimal(random.nextInt(10)), new BigDecimal(0), new BigDecimal(random.nextInt(100)));
		
		System.out.println("[" + Thread.currentThread().getName() + "] " + stockSymbols[rnd] + " creation " + (creationResult?" was successful": " failed"));
		
		// Now we will try to create a trade action for this stock (even if it failed it should have no problem)
		
		Boolean addActionResult = myStockExchange.getStock(stockSymbols[rnd]).addTradeAction(random.nextInt(100), StockTradeAction.actionTypes.BUY, new BigDecimal(random.nextDouble(200d)));
		
		System.out.println("[" + Thread.currentThread().getName() + "] " + stockSymbols[rnd] + " - adding trade action " + (addActionResult?" was successful": " failed"));
		
		// Finally we will check for this stock value and dividend yield
		System.out.println("[" + Thread.currentThread().getName() + "] value for stock " + stockSymbols[rnd] + " is " + myStockExchange.getStock(stockSymbols[rnd]).calculateStockPrice());
		System.out.println("[" + Thread.currentThread().getName() + "] dividend yield for stock " + stockSymbols[rnd] + " is " + myStockExchange.getStock(stockSymbols[rnd]).getDividendYield());
		
		System.out.println("[" + Thread.currentThread().getName() + "] has finished");
	}

}

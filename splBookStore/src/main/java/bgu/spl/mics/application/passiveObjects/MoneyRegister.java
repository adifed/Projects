package bgu.spl.mics.application.passiveObjects;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {

	private static MoneyRegister instance = null;
	private List<OrderReceipt> receipts = new LinkedList<>();

	private MoneyRegister(){}

	private static class MoneyRegisterHolder {
		private static MoneyRegister instance = new MoneyRegister();
	}
	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance() {
		return MoneyRegisterHolder.instance;
	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		receipts.add(r);
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		int totalEarning=0; //the total earning of the prices
		for(int i =0; i < receipts.size(); i++){
			totalEarning = totalEarning + receipts.get(i).getPrice(); //add the price of the receipt
		}
		return totalEarning;
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
		c.setAmount(amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		filename = "receipts.bin";
		try {
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));
			os.writeObject(receipts);
			os.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void printMoneyRegister(String fileName) {
		fileName = "money-register.bin";
		try {
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(fileName));
			os.writeObject(this);
			os.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

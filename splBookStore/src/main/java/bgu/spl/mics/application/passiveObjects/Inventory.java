package bgu.spl.mics.application.passiveObjects;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

/*** Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * You can add ONLY private fields and methods to this class as you see fit.*/

public class Inventory implements Serializable {
	private static Inventory instance = null;
	private BookInventoryInfo [] allBooks;
	Object object = new Object();


	private Inventory() {}

	private static class InventoryHolder {
		private static Inventory instance = new Inventory();
	}


	/*** Retrieves the single instance of this class. */
	public static Inventory getInstance() { //Ron's Code
		return InventoryHolder.instance;
	}

	/*** Initializes the store inventory. This method adds all the items given to the store
	 * inventory.
	 * <p>
	 * @param inventory Data structure containing all data necessary for initialization of the inventory. */
	public void load (BookInventoryInfo[] inventory) {
		allBooks = new BookInventoryInfo [inventory.length];
		for (int i=0; i<inventory.length; i++) {
			allBooks[i] = inventory[i];
		}

	}

	/**
	 * Attempts to take one book from the store.
	 * <p>
	 * @param book 		Name of the book to take from the store
	 * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
	 * 			The first should not change the state of the inventory while the
	 * 			second should reduce by one the number of books of the desired type.
	 */
	public  OrderResult take (String book) {
		synchronized (object) {
			for (BookInventoryInfo b : allBooks) {
				if ((b.getBookTitle().equals(book)) & (b.getAmountInInventory() > 0)) {
					b.takeOneCopy();
					return OrderResult.values()[0];
				}
			}

			return OrderResult.values()[1];
		}
	}

	/**
	 * Checks if a certain book is available in the inventory.
	 * <p>
	 * @param book 		Name of the book.
	 * @return the price of the book if it is available, -1 otherwise.
	 */
	public int checkAvailabiltyAndGetPrice(String book) {
		for (BookInventoryInfo b: allBooks) {
			if ((b.getBookTitle().equals(book)) & (b.getAmountInInventory() != 0)) {
				return b.getPrice();
			}
		}
		return -1;
	}

	/** Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
	 * should be the titles of the books while the values (type {@link Integer}) should be
	 * their respective available amount in the inventory.
	 * This method is called by the main method in order to generate the output.*/

	public void printInventoryToFile(String filename){ // Ron's Code (not sure)
		filename = "inventory.bin";
		HashMap<String, Integer>  allBooksAsHash = new HashMap<> ();
		for (BookInventoryInfo b: allBooks) {
			allBooksAsHash.put(b.getBookTitle(), b.getAmountInInventory());
		}
		try {
			FileOutputStream fileOut = new FileOutputStream(filename);
			ObjectOutputStream os = new ObjectOutputStream(fileOut);
			os.writeObject(allBooksAsHash);
			os.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
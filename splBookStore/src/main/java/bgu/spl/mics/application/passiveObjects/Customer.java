package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {

	private String name;
	private  int id;
	private String adress;
	private int distance;
	private List <OrderReceipt> listOfReceipt;
	private int amountOfMoney;
	private int creditCardNumber;
	private LinkedList<OrderSchedule> orderSchedules; //list of books the customer buy with their tick


	public Customer(String name, int id, String adress, int distance,int creditCardNumber,int amountOfMoney,LinkedList<OrderSchedule>orderSchedules) {//constructor to customer
		this.name = name;
		this.id = id;
		this.adress = adress;
		this.distance = distance;
		this.amountOfMoney = amountOfMoney;
		this.creditCardNumber = creditCardNumber;
		this.listOfReceipt = new LinkedList<OrderReceipt>();
		this.orderSchedules = orderSchedules;
	}
	/**
	 * Retrieves the name of the customer.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves the ID of the customer  .
	 */
	public int getId() {
		return id;
	}

	/**
	 * Retrieves the address of the customer.
	 */
	public String getAddress() {
		return adress;
	}

	/**
	 * Retrieves the distance of the customer from the store.
	 */
	public int getDistance() {
		return distance;
	}


	/**
	 * Retrieves a list of receipts for the purchases this customer has made.
	 * <p>
	 * @return A list of receipts.
	 */
	public List<OrderReceipt> getCustomerReceiptList() {
		return listOfReceipt;
	}

	/**
	 * Retrieves the amount of money left on this customers credit card.
	 * <p>
	 * @return Amount of money left.
	 */
	public synchronized int getAvailableCreditAmount() {
		return amountOfMoney;
	}

	/**
	 * Retrieves this customers credit card serial number.
	 */
	public int getCreditNumber() {
		return creditCardNumber;
	}

	public LinkedList<OrderSchedule> getOrderSchedules(){
		return orderSchedules;
	}

	public synchronized void addReciept(OrderReceipt receipt){ // JUST ADDED SYNCHRONIZED
		getCustomerReceiptList().add(receipt);
	}

	public void setAmount(int amount){
		this.amountOfMoney = this.amountOfMoney - amount;
	}

}


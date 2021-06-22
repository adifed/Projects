

import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class InventoryTest {
    BookInventoryInfo[] someBooks = new BookInventoryInfo[3];
    Inventory myInstance = Inventory.getInstance();

    @Before
    public void setUp() throws Exception {
        someBooks[0] = new BookInventoryInfo("The Diary", 20, 3);
        someBooks[1] = new BookInventoryInfo("How to Make Money", 30, 0);
        someBooks[2] = new BookInventoryInfo("The Secret", 22, 1);
        load();
    }

    @Test
    public void getInstance() {
        assertNotEquals(null, myInstance);
    }

    @Test
    public void load() {
        myInstance.load(someBooks);
        assertEquals(someBooks[0], myInstance.justForTesterGetBooks()[0]);
        assertEquals(someBooks[1], myInstance.justForTesterGetBooks()[1]);
        assertEquals(someBooks[2], myInstance.justForTesterGetBooks()[2]);
    }

    @Test
    public void take() {
        assertEquals(OrderResult.SUCCESSFULLY_TAKEN, myInstance.take("The Diary"));
        assertEquals(OrderResult.NOT_IN_STOCK, myInstance.take("How to Make Money"));
        assertEquals(OrderResult.NOT_IN_STOCK, myInstance.take("Harry Potter"));

        //checks the amount
        assertEquals(2, someBooks[0].getAmountInInventory());
        assertEquals(0, someBooks[1].getAmountInInventory());

        // check if the book hasn't been found in stock after the amount is changed to 0
        assertEquals(OrderResult.SUCCESSFULLY_TAKEN, myInstance.take("The Secret"));
        assertEquals(OrderResult.NOT_IN_STOCK, myInstance.take("The Secret"));
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        assertEquals(20, myInstance.checkAvailabiltyAndGetPrice("The Diary"));
        assertEquals(22, myInstance.checkAvailabiltyAndGetPrice("The Secret"));
        assertEquals(-1, myInstance.checkAvailabiltyAndGetPrice("AM I?"));
        assertEquals(-1, myInstance.checkAvailabiltyAndGetPrice("How to Make Money"));
    }

    @Test
    public void printInventoryToFile() {
        String s = "";
        myInstance.printInventoryToFile(s);

        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream("data.bin"));
            HashMap <String,Integer> m = (HashMap<String, Integer>) is.readObject();
            for (Map.Entry<String,Integer> e: m.entrySet()) {
                System.out.println("Book Name: " + e.getKey() + ", Book Amount: " + e.getValue());
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}
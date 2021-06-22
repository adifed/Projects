package bgu.spl.mics.application;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
      //  System.out.println("Welcome to our store!");
        LinkedList<Thread> threads = new LinkedList<>();  // contains all the threads
        JsonParser jsonParser = new JsonParser();  //will contain the JsonFile
        JsonObject jsonObject = null;

        //  HashMap<Integer, LinkedList<String>> toApi = new HashMap<>();
        HashMap<Integer, Customer> map = new HashMap<>();

        try {
            jsonObject = (JsonObject) jsonParser.parse(new FileReader(args[0])); //Take the JsonFile from args[0]
            JsonArray array = jsonObject.get("initialInventory").getAsJsonArray(); //take the object in initialInventory
            BookInventoryInfo[] books = new BookInventoryInfo[array.size()]; //check the array size
            int index = 0;

            for (int i = 0; i < array.size(); i++) { //create a new object from type - BookInventoryInfo
                String title = (array.get(i)).getAsJsonObject().get("bookTitle").getAsString();
                int amount = array.get(i).getAsJsonObject().get("amount").getAsInt();
                int price = array.get(i).getAsJsonObject().get("price").getAsInt();
                books[index] = new BookInventoryInfo(title, amount, price); //insert the new object
                index++;
            }
            Inventory.getInstance().load(books); //load the books to the inventory

            /////////////////////////////////////////////////////////////////////////////////////////////Inventory

            JsonArray array2 = jsonObject.get("initialResources").getAsJsonArray().get(0).getAsJsonObject().get("vehicles").getAsJsonArray();
            DeliveryVehicle[] vehicles = new DeliveryVehicle[array2.size()]; //check the array size
            int index2 = 0;

            for (int i = 0; i < array2.size(); i++) {// create a new object from type - DeliveryVehicle
                int license = array2.get(i).getAsJsonObject().get("license").getAsInt();
                int speed = array2.get(i).getAsJsonObject().get("speed").getAsInt();
                vehicles[index2] = new DeliveryVehicle(license, speed); //insert the new object
                index2++;
            }
            ResourcesHolder.getInstance().load(vehicles); //load the vehicle to the resourceHolder

            ////////////////////////////////////////////////////////////////////////////////////////////ResourcesHolder

            JsonObject object = jsonObject.get("services").getAsJsonObject();
            JsonObject objectTime = object.get("time").getAsJsonObject();
            int speed = objectTime.get("speed").getAsInt();
            int duration = objectTime.get("duration").getAsInt();

            //////////////////////////////////////////////////////////////////////////////////////////TimeService

            int resourcesService = object.get("resourcesService").getAsInt();
            int logistics = object.get("logistics").getAsInt();
            int inventoryService = object.get("inventoryService").getAsInt();
            int selling = object.get("selling").getAsInt();

            int size = resourcesService + logistics + inventoryService + selling; // checking the size of the servicesList
            CountDownLatch countDownLatch = new CountDownLatch(size + 1); // counter + 1 of the API

            for (int i = 0; i < selling; i++) {
                Thread thread = new Thread(new SellingService(i, countDownLatch)); //create a new selling service
                threads.add(thread);
            }

            //  int inventoryService = object.get("inventoryService").getAsInt();
            for (int i = 0; i < inventoryService; i++) {
                Thread thread = new Thread(new InventoryService(countDownLatch)); //create a new inventory service
                threads.add(thread);
            }

            //  int logistics = object.get("logistics").getAsInt();
            for (int i = 0; i < logistics; i++) {
                Thread thread = new Thread(new LogisticsService(countDownLatch));  //create a new logistics service
                threads.add(thread);
            }

            // int resourcesService = object.get("resourcesService").getAsInt();
            for (int i = 0; i < resourcesService; i++) {
                Thread thread = new Thread(new ResourceService(countDownLatch));  //create a new resources service
                threads.add(thread);
            }

            JsonArray customersArr = jsonObject.get("services").getAsJsonObject().get("customers").getAsJsonArray();
            for (int i = 0; i < customersArr.size(); i++) {
                int id = customersArr.get(i).getAsJsonObject().get("id").getAsInt();
                String name = customersArr.get(i).getAsJsonObject().get("name").getAsString();
                String address = customersArr.get(i).getAsJsonObject().get("address").getAsString();
                int distance = customersArr.get(i).getAsJsonObject().get("distance").getAsInt();
                JsonObject creditCard = customersArr.get(i).getAsJsonObject().get("creditCard").getAsJsonObject();
                int creditCardNumber = creditCard.get("number").getAsInt();
                int creditCardAmount = creditCard.get("amount").getAsInt();
                JsonArray orderScheduleArray = customersArr.get(i).getAsJsonObject().get("orderSchedule").getAsJsonArray();
                LinkedList<OrderSchedule> l = new LinkedList<>();

                //happens for each customer --- order list(tick, book name)
                for (int j = 0; j < orderScheduleArray.size(); j++) {
                    JsonObject orderSchedule = orderScheduleArray.get(j).getAsJsonObject();
                    String s = orderSchedule.getAsJsonObject().get("bookTitle").getAsString();
                    int t = orderSchedule.getAsJsonObject().get("tick").getAsInt();
                    OrderSchedule o = new OrderSchedule(s, t);
                    l.add(o);


                }

                //creating the customer
                Customer c = new Customer(name, id, address, distance, creditCardNumber, creditCardAmount, l);
                Thread thread = new Thread(new APIService(countDownLatch, c));
                threads.add(thread);
                map.put(id, c);
            }

            for (Thread t : threads) { //we will give all the threads to start (without time)
                t.start();
            }

            countDownLatch.await(); // wait until all the services will finish to initialize
            Thread thread = new Thread(new TimeService(speed, duration));
            threads.add(thread); //add the TimeService
            thread.start();

            for (Thread t : threads) {
                t.join();
            }


            FileOutputStream fos = new FileOutputStream(args[1]);
            ObjectOutput oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
            fos.close();

            Inventory.getInstance().printInventoryToFile(args[2]);
            MoneyRegister.getInstance().printOrderReceipts(args[3]);
            MoneyRegister.getInstance().printMoneyRegister(args[4]);


           // System.out.println("amount book1:" + Inventory.getInstance().books()[0].getAmountInInventory());
           // System.out.println("amount book2:" + Inventory.getInstance().books()[1].getAmountInInventory());
          //  System.out.println("amount book3:" + Inventory.getInstance().books()[2].getAmountInInventory());
          //  System.out.println("total: " + MoneyRegister.getInstance().getTotalEarnings());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}



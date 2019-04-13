package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = null;
        try {
            jsonObject = (JsonObject) parser.parse(new FileReader(args[0]) {
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JsonArray jsonArrayBooks = jsonObject.get("initialInventory").getAsJsonArray();
        BookInventoryInfo[] booksinfo = new BookInventoryInfo[jsonArrayBooks.size()];
        for (int i = 0; i < booksinfo.length; i++) {
            String name = jsonArrayBooks.get(i).getAsJsonObject().get("bookTitle").getAsString();
            int amount = jsonArrayBooks.get(i).getAsJsonObject().get("amount").getAsInt();
            int price = jsonArrayBooks.get(i).getAsJsonObject().get("price").getAsInt();
            booksinfo[i] = new BookInventoryInfo(name, amount, price);
        }
        Inventory.getInstance().load(booksinfo);
        JsonArray jsonResources = jsonObject.get("initialResources").getAsJsonArray();
        for (int j = 0; j < jsonResources.size(); j++) {
            JsonArray jsonArrayVehicles = jsonResources.get(j).getAsJsonObject().get("vehicles").getAsJsonArray();
            DeliveryVehicle[] vehicles = new DeliveryVehicle[jsonArrayVehicles.size()];
            for (int i = 0; i < vehicles.length; i++) {
                int license = jsonArrayVehicles.get(i).getAsJsonObject().get("license").getAsInt();
                int speed = jsonArrayVehicles.get(i).getAsJsonObject().get("speed").getAsInt();
                vehicles[i] = new DeliveryVehicle(license, speed);
            }
            ResourcesHolder.getInstance().load(vehicles);
        }
        JsonObject jsonServices = jsonObject.get("services").getAsJsonObject();
        JsonObject jsonTimeService = jsonServices.get("time").getAsJsonObject();
        int speed = jsonTimeService.getAsJsonObject().get("speed").getAsInt();
        int duration = jsonTimeService.getAsJsonObject().get("duration").getAsInt();
        TimeService timeService = new TimeService(duration, speed);
        Thread t = new Thread(timeService);
        int numOfServices = jsonServices.get("selling").getAsInt() + jsonServices.get("inventoryService").getAsInt()
                + jsonServices.get("logistics").getAsInt() + jsonServices.get("resourcesService").getAsInt();
        CountDownLatch countDownLatch = new CountDownLatch(numOfServices);

        ArrayList<Thread> threads = new ArrayList<>();
        threads.add(t);


        SellingService[] sellingServices = new SellingService[jsonServices.get("selling").getAsInt()];
        for (int i = 0; i < sellingServices.length; i++) {
            sellingServices[i] = new SellingService(i, countDownLatch);
            Thread t1 = new Thread(sellingServices[i]);
            threads.add(t1);
            t1.start();
        }
        InventoryService[] inventoryServices = new InventoryService[jsonServices.get("inventoryService").getAsInt()];
        for (int i = 0; i < inventoryServices.length; i++) {
            inventoryServices[i] = new InventoryService(i, countDownLatch);
            Thread t1 = new Thread(inventoryServices[i]);
            threads.add(t1);
            t1.start();
        }
        LogisticsService[] logisticsServices = new LogisticsService[jsonServices.get("logistics").getAsInt()];
        for (int i = 0; i < logisticsServices.length; i++) {
            logisticsServices[i] = new LogisticsService(i, countDownLatch);
            Thread t1 = new Thread(logisticsServices[i]);
            threads.add(t1);
            t1.start();
        }
        ResourceService[] resourcesServices = new ResourceService[jsonServices.get("resourcesService").getAsInt()];
        for (int i = 0; i < resourcesServices.length; i++) {
            resourcesServices[i] = new ResourceService(i, countDownLatch);
            Thread t1 = new Thread(resourcesServices[i]);
            threads.add(t1);
            t1.start();
        }
        HashMap<Integer, Customer> customerHashMap = new HashMap<>();
        JsonArray jsonCustomers = jsonServices.get("customers").getAsJsonArray();
        Customer[] customers = new Customer[jsonCustomers.size()];
        for (int i = 0; i < customers.length; i++) {
            int customerId = jsonCustomers.get(i).getAsJsonObject().get("id").getAsInt();
            String customerName = jsonCustomers.get(i).getAsJsonObject().get("name").getAsString();
            String customerAddress = jsonCustomers.get(i).getAsJsonObject().get("address").getAsString();
            int distance = jsonCustomers.get(i).getAsJsonObject().get("distance").getAsInt();
            JsonObject creditCard = jsonCustomers.get(i).getAsJsonObject().get("creditCard").getAsJsonObject();
            int creditCardNum = creditCard.get("number").getAsInt();
            int creditCardBalance = creditCard.get("amount").getAsInt();
            JsonArray orderSchedule = jsonCustomers.get(i).getAsJsonObject().get("orderSchedule").getAsJsonArray();
            customers[i] = new Customer(customerName, customerAddress, distance, creditCardNum, creditCardBalance, customerId, orderSchedule);
            Thread t1 = new Thread(new APIService(i, customers[i], i, countDownLatch));
            threads.add(t1);
            t1.start();
            customerHashMap.put(customers[i].getId(), customers[i]);
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t.start();

        for (Thread t2 : threads) {
            try {
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos =
                    new FileOutputStream(args[1]);
            ObjectOutputStream outputStream = new ObjectOutputStream(fos);
            outputStream.writeObject(customerHashMap);
            outputStream.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Inventory.getInstance().printInventoryToFile(args[2]);
        MoneyRegister.getInstance().printOrderReceipts(args[3]);

        try {
            FileOutputStream fos =
                    new FileOutputStream(args[4]);
            ObjectOutputStream outputStream = new ObjectOutputStream(fos);
            outputStream.writeObject(MoneyRegister.getInstance());
            outputStream.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

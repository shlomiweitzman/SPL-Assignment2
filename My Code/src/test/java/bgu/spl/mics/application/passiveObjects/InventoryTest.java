package bgu.spl.mics.application.passiveObjects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import static bgu.spl.mics.application.passiveObjects.OrderResult.NOT_IN_STOCK;
import static bgu.spl.mics.application.passiveObjects.OrderResult.SUCCESSFULLY_TAKEN;
import static org.junit.Assert.assertEquals;

public class InventoryTest {

    private Inventory inventory;

    @Before
    public void setUp() throws Exception {
        this.inventory=Inventory.getInstance();
    }

    @After
    public void tearDown() throws Exception {}

    @Test
    public void load() {
        BookInventoryInfo bookInventoryInfo=new BookInventoryInfo("A Book", 5,5);
        BookInventoryInfo[] arr={bookInventoryInfo};
        assertEquals(0,bookInventoryInfo.getAmountInInventory());
        inventory.load(arr);
        assertEquals(inventory.checkAvailabiltyAndGetPrice("A Book"),5);
    }

    @Test
    public void take() {
        BookInventoryInfo bookInventoryInfo=new BookInventoryInfo("A Book", 5,5);
        BookInventoryInfo[] arr={bookInventoryInfo};
        inventory.load(arr);
        assertEquals(NOT_IN_STOCK, inventory.take("doesnt exist")); // Testing the query
        assertEquals(SUCCESSFULLY_TAKEN,inventory.take("A Book")); // Testing the take query
        assertEquals(0,bookInventoryInfo.getAmountInInventory()); // Testing the command (remove)
    }

    @Test
    public void checkAvailabilityAndGetPrice() {
        BookInventoryInfo bookInventoryInfo=new BookInventoryInfo("A Book", 5,5);
        BookInventoryInfo[] arr={bookInventoryInfo};
        inventory.load(arr);
        assertEquals(-1,inventory.checkAvailabiltyAndGetPrice("doesnt exist"));
        assertEquals(5,inventory.checkAvailabiltyAndGetPrice("A Book"));
    }

    @Test
    public void printInventoryToFile() throws IOException {}
}
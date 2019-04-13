package bgu.spl.mics;
public class gsonObject {
    Book[] initialInventory;//#

    public class Book{
        String bookTitle;
        int amount;
        int price;
    }
    Resource[] initialResources;//#
    public class Resource{
        Vehicle[] vehicles;
    }
    public class Vehicle{
        int license;
        int speed;
    }
    BigObject services;//#
    public class BigObject{
        Timer time;
        public class Timer{
            int speed;
            int duration;
        }
        int selling;
        int inventoryService;
        int logistics;
        int resourcesService;
        CustomerObj[] customers;
        public class CustomerObj{
            int id;
            String name;
            String address;
            int distance=33;
            CreditCard creditCard;
            public class CreditCard{
                int number;
                int amount;
            }
            OrderEventObj[] orderSchedule;
            public class OrderEventObj{
                String bookTitle;
                int tick;
            }
        }
    }
}

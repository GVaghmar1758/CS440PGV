package com.squeeze;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;

// Class for handling basic business
public class Business {
    //TO-DO:
    //in a loop, sell a desired drink to the customer;
    //in a loop, make more drinks;
    //customer comes, according to nextLoopSales +- random number;
    //after a loop, adjust price;
    //after a loop, modify menu;
    //after a loop, buy ingredients;
    //after a loop, make more drinks;
    private LemonadeStand lemonadeStand;
    private int day; // 1 for Monday, 2 for Tuesday, ...
    private double sales;
    private HashMap<Integer, Double> weeklySales;
    private HashMap<Integer, Integer> customerNumbers;

    // Constructor
    public Business() {
        Random rand = new Random();
        lemonadeStand = new LemonadeStand();
        day = 1;
        sales = 0.0;
        weeklySales = new HashMap<>();
        customerNumbers = new HashMap<>();
        for (Product product : lemonadeStand.getProducts()) {
            int rand_int = rand.nextInt(5) - 2;
            customerNumbers.put(product.getID(), product.getNextDaySales() + rand_int);
        }
    }

    public String getProductName(int productID) {
        return lemonadeStand.getProductName(productID);
    }

    public double getProductPrice(int productID) {
        return lemonadeStand.getProductPrice(productID);
    }

    // Getter for lemonade stand
    public LemonadeStand getLemonadeStand() {
        return lemonadeStand;
    }

    public ArrayList<Product> getProducts() {
        return lemonadeStand.getProducts();
    }

    public HashMap<String, Integer> getIngredients() {
        return lemonadeStand.getIngredients();
    }

    // Getter for weekly sales
    public HashMap<Integer, Double> getWeeklySales() {
        return weeklySales;
    }

    public double getCash() {
        return lemonadeStand.getMoney();
    }

    public int getDay() {
        return day;
    }

    public boolean hasRemainingCustomers() {
        int remainingPredictedSales = 0;
        for (HashMap.Entry<Integer, Integer> entry : customerNumbers.entrySet()) {
            remainingPredictedSales += entry.getValue();
        }
        if (remainingPredictedSales == 0) {
            return false;
        }
        else {
            return true;
        }
    }

    // Method to sell one type of drinks to a customer
    public void sell(int productID, int quantity) {
        for (int i = 0; i < quantity; i++) {
            sales += lemonadeStand.sellProduct(productID);
        }
//        int oldValue = customerNumbers.get(productID);
//        customerNumbers.put(productID, Math.max(oldValue - quantity, 0));
    }

    // Method to take one customer's order
    public HashMap<Integer, Integer> customerOrder() {
        HashMap<Integer, Integer> customerOrder = new HashMap<>();
        ArrayList<Integer> keys = new ArrayList<Integer>(customerNumbers.keySet());
        Random rand = new Random();
        int buyTypeNumber = Math.max(1, Math.min(rand.nextInt(lemonadeStand.getMenuCount()) + 1, lemonadeStand.getMenuCount() - 1));
        for (int i = 0; i < buyTypeNumber; i++) {
            Integer productID = keys.get(rand.nextInt(keys.size()));
            int remainingNumber = customerNumbers.get(productID);
            if (remainingNumber > 0) {
                int buyQuantity = Math.min(rand.nextInt(remainingNumber) + 1, 3);
                remainingNumber -= buyQuantity;
                customerOrder.put(productID, buyQuantity);
                customerNumbers.put(productID, remainingNumber);
            }
        }
        return customerOrder;
    }

    // Call makeDrink function in lemonadeStand
    // because game interface only interact with Business class
    public void makeDrink(int productID, int quantity) {
        lemonadeStand.makeDrink(productID, quantity);
    }

    // Method to adjust drink price
    public void adjustPrice(int productID, double newPrice) {
        lemonadeStand.changeProductPrice(productID, newPrice);
    }

    // Method to add new product to menu
    public void addNewProduct(String productName, double price) {
        ArrayList<String> ingredient = new ArrayList<>();
        ingredient.add("lemon");
        lemonadeStand.setProduct(productName, price, ingredient);
    }

    // Method to buy ingredients
    public void buyIngredient(String ingredientName, int quantity) {
        ArrayList<String> keys = new ArrayList<String>(lemonadeStand.getIngredients().keySet());
        for (String key : keys) {
            if (ingredientName.equals(key)) {
                lemonadeStand.refillIngredient(ingredientName, quantity);
                return;
            }
        }
        lemonadeStand.setIngredient(ingredientName);
        lemonadeStand.refillIngredient(ingredientName, quantity);
    }

    // Method to pass a day
    // return true if this is the end of the week
    // otherwise false
    public boolean endToday() {
        Random rand = new Random();
        for (Product product : lemonadeStand.getProducts()) {
            product.setOldPrice(product.getPrice());
            product.setOldSales((int) (sales / product.getPrice()));
            int rand_int = rand.nextInt(5) - 2;
            customerNumbers.put(product.getID(), product.getNextDaySales() + rand_int);
        }
        weeklySales.put(day, sales);
        day++;
        sales = 0.0;
        if (day > 7) {
            day = 1;
            return true;
        }
        return false;
    }
}

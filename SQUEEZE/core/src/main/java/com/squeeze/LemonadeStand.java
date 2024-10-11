package com.squeeze;

import java.util.ArrayList;
import java.util.HashMap;

// One lemonade stand, including cashier, inventory, and your juice maker
public class LemonadeStand {
    private ArrayList<Product> products;
    private HashMap<String, Integer> ingredients;
    private double money;
    private HashMap<Integer, ArrayList<String>> recipe;
    public static final HashMap<String, Double> ingredientPrice = new HashMap<>();

    // Defining prices of ingredients and make them final
    static {
        ingredientPrice.put("lemon", 0.5);
    }

    // Constructor
    public LemonadeStand() {
        // Initializing menu
        // Only have "Classic Lemonade" (id: 1)
        Product lemonade = new Product(1,"Classic Lemonade", 1.0, 10);
        products = new ArrayList<>();
        products.add(lemonade);
        // Initializing ingredients you have
        ingredients = new HashMap<>();
        ingredients.put("lemon", 100);
        // Initializing money
        money = 0.0;
        // Initializing recipe
        // Only have "Classic Lemonade" (id: 1), its recipe is purely "lemon"
        recipe = new HashMap<>();
        ArrayList<String> listOfIngredients = new ArrayList<>();
        listOfIngredients.add("lemon");
        recipe.put(1, listOfIngredients);
    }

    public double getMoney() {
        return money;
    }

    // Get number of product names in the menu
    public int getMenuCount() {
        return products.size();
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public String getProductName (int index) {
        for (Product product : products) {
            if (product.getID() == index) {
                return product.getName();
            }
        }
        return "UNKNOWN";
    }

    public double getProductPrice (int index) {
        for (Product product : products) {
            if (product.getID() == index) {
                return product.getPrice();
            }
        }
        return 0.0;
    }

    // Add a new product to menu
    public void setProduct(String name, double price, ArrayList<String> newRecipe) {
        int newID = products.get(products.size() - 1).getID() + 1;
        Product product = new Product(newID, name, price, 0);
        products.add(product);
        recipe.put(newID, newRecipe);
    }

    // Refill the quantity of a product by a certain number
    public void refillProduct(int id, int numberToAdd) {
        for (Product product : products) {
            if (product.getID() == id) {
                product.setQuantity(product.getQuantity() + numberToAdd);
            }
        }
    }

    // Change price of a product
    public void changeProductPrice(int id, double newPrice) {
        for (Product product : products) {
            if (product.getID() == id) {
                product.setPrice(newPrice);
            }
        }
    }

    // Remove one type of product from menu and discard all left-overs
    public void removeProduct(int id) {
        products.removeIf(product -> product.getID() == id);
    }

    // Method to sell 1 product and update money
    public double sellProduct(int id) {
        for (Product product : products) {
            if ((product.getID() == id) && (product.getQuantity() > 0)) {
                product.setQuantity(product.getQuantity() - 1);
                money += product.getPrice();
                return product.getPrice();
            }
        }
        return 0;
    }

    public HashMap<String, Integer> getIngredients() {
        return ingredients;
    }

    // Add a new ingredient to inventory
    public void setIngredient(String ingredient) {
        ingredients.put(ingredient, 0);
    }

    // Refill the quantity of a product by a certain number
    public void refillIngredient(String ingredient, int quantity) {
        double spent = ingredientPrice.get(ingredient) * quantity;
        money -= spent;
        Integer oldQuantity = ingredients.get(ingredient);
        ingredients.put(ingredient, quantity + oldQuantity);
    }

    // Method to make some number of a drink
    // Returning success status
    public boolean makeDrink(int id, int quantity) {
        ArrayList<String> listOfIngredients = recipe.get(id);
        // Check if ingredients availability first
        for (String ingredient : listOfIngredients) {
            if (ingredients.get(ingredient) < quantity) {
                return false;
            }
        }
        // Make drink
        refillProduct(id, quantity);
        for (String ingredient : listOfIngredients) {
            ingredients.put(ingredient, ingredients.get(ingredient) - quantity);
        }
        return true;
    }
}

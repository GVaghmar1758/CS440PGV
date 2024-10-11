package com.squeeze;

// One product type, probably lemonade
public class Product {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private int nextDaySales;
    private double oldPrice;
    private int oldSales;

    // Constructor
    public Product(int id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.nextDaySales = quantity;
        this.oldPrice = price;
        this.oldSales = quantity;
    }

    // Getter for id
    public int getID() {
        return id;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for price
    public double getPrice() {
        return price;
    }

    // Setter for price
    public void setPrice(double price) {
        this.nextDaySales = predictSales(price);
        this.price = price;
    }

    // Getter for quantity
    public int getQuantity() {
        return quantity;
    }

    // Setter for quantity
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getNextDaySales() {
        return nextDaySales;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public void setOldSales(int oldSales) {
        this.oldSales = oldSales;
    }

    public int getOldSales() {
        return oldSales;
    }

    // Method to predict sales based on price change
    public int predictSales(double newPrice) {
        // According to BMC Public Health, price-elasticity of soft drinks is -1.37
        double expectedChange = (-1.37) * ((newPrice - oldPrice) / oldPrice);
        int predictedSales = (int) (oldSales + expectedChange * oldSales);
        return Math.max(predictedSales, 0); // Cannot sell negative number of products
    }
}

package model;


import java.util.Date;
import java.util.Objects;

public class Product {
    private static int counter = 0;

    private final int id;
    private String name;
    private Date expirationDate;
    private ProductCategory category;
    private int quantity;

    public Product(int id, String name, Date expirationDate, ProductCategory category, int quantity) {
        this.id = id;
        this.name = name;
        this.expirationDate = expirationDate;
        this.category = category;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        // If necessary, implement setter logic
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Define the isUsable method
    public boolean isUsable() {
        return expirationDate.after(new Date()); // Check if the expiration date is in the future
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && quantity == product.quantity && Objects.equals(name, product.name) && Objects.equals(expirationDate, product.expirationDate) && category == product.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, expirationDate, category, quantity);
    }

    public ProductCategory getType() {
        return category;
    }

}
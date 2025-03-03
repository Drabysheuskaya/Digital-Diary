package management;


import model.Product;
import model.ProductCategory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InMemoryProductStorage implements IProductStorage {
    private static int counter = 1;
    private static final InMemoryProductStorage INSTANCE = new InMemoryProductStorage();

    private final List<Product> products = new ArrayList<>();

    private InMemoryProductStorage() {
        initializeProducts();
    }

    public static InMemoryProductStorage getInstance() {
        return INSTANCE;
    }

    private void initializeProducts() {
        products.add(new Product(getNextId(), "Milk", new Date(), ProductCategory.FOOD_PRODUCTS, 2));
        products.add(new Product(getNextId(), "Bread", new Date(), ProductCategory.FOOD_PRODUCTS, 3));
        products.add(new Product(getNextId(), "Toothpaste", new Date(), ProductCategory.COSMETICS, 1));
        products.add(new Product(getNextId(), "Shampoo", new Date(), ProductCategory.COSMETICS, 2));
        products.add(new Product(getNextId(), "Aspirin", new Date(), ProductCategory.MEDICINES, 1));
        products.add(new Product(getNextId(), "Apples", new Date(), ProductCategory.FOOD_PRODUCTS, 5));
        products.add(new Product(getNextId(), "Lotion", new Date(), ProductCategory.COSMETICS, 3));
        products.add(new Product(getNextId(), "Pasta", new Date(), ProductCategory.FOOD_PRODUCTS, 2));
    }

    private int getNextId() {
        return counter++;
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    @Override
    public List<Product> getProductsByCategory(ProductCategory category) {
        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.getCategory() == category) {
                filteredProducts.add(product);
            }
        }
        return filteredProducts;
    }

    @Override
    public Product getProductById(int id) {
        for (Product product : products) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }

    @Override
    public void saveProduct(Product product) {
        product.setId(getNextId());
        products.add(product);
    }

    @Override
    public void updateProduct(int id, Product newProduct) {
        for (Product product : products) {
            if (product.getId() == id) {
                product.setName(newProduct.getName());
                product.setExpirationDate(newProduct.getExpirationDate());
                product.setCategory(newProduct.getCategory());
                product.setQuantity(newProduct.getQuantity());
                break;
            }
        }
    }

    @Override
    public void deleteProduct(int id) {
        products.removeIf(product -> product.getId() == id);
    }

    // Method to retrieve valid products
    public List<Product> getAllValidProducts() {
        List<Product> validProducts = new ArrayList<>();
        Date currentDate = new Date();
        for (Product product : products) {
            if (product.getExpirationDate().after(currentDate)) {
                validProducts.add(product);
            }
        }
        return validProducts;
    }
}


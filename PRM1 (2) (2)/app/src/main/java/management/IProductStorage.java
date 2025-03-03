package management;


import model.Product;
import model.ProductCategory;

import java.util.List;

public interface IProductStorage {
    List<Product> getAllProducts();

    List<Product> getProductsByCategory(ProductCategory category);

    Product getProductById(int id);

    void saveProduct(Product product);

    void updateProduct(int id, Product newProduct);

    void deleteProduct(int id);
}

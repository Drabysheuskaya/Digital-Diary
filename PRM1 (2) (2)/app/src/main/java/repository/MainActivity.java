package repository;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm1.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import adapter.IProductUICallbacks;
import adapter.ProductAdapter;
import management.IProductStorage;
import management.InMemoryProductStorage;
import model.Product;
import model.ProductCategory;


public class MainActivity extends AppCompatActivity implements IProductUICallbacks {
    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private final IProductStorage productStorage = InMemoryProductStorage.getInstance();
    private String currentCategory = "All";
    private String currentStatusFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        setupRecyclerView();
        setupCategoryDropdown();
        setupStatusDropdown();
        loadProducts();
    }

    private final ActivityResultLauncher<Intent> editItemLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadProducts();
                }
            }
    );

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductEditActivity.class);
            editItemLauncher.launch(intent);
        });
    }

    private void setupCategoryDropdown() {
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.typesFilter);
        List<String> categories = new ArrayList<>();
        categories.add("All");
        for (ProductCategory category : ProductCategory.values()) {
            categories.add(category.getCategoryName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            currentCategory = adapter.getItem(position);
            applyFilters();
        });
    }

    private void setupStatusDropdown() {
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.statusFilter);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                Arrays.asList("All", "Expired", "Valid"));
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            currentStatusFilter = adapter.getItem(position);
            applyFilters();
        });
    }

    private void applyFilters() {
        Stream<Product> filteredProductsStream = productStorage.getAllProducts().stream();
        if (!"All".equals(currentCategory)) {
            ProductCategory category = ProductCategory.getCategoryByName(currentCategory);
            filteredProductsStream = filteredProductsStream.filter(
                    prod -> prod.getCategory() == category);
        }
        switch(currentStatusFilter) {
            case "Valid":
                filteredProductsStream = filteredProductsStream.filter(Product::isUsable);
                break;
            case "Expired":
                filteredProductsStream = filteredProductsStream.filter(
                        prod -> !prod.isUsable());
                break;
            default:
                break;
        }

        List<Product> filteredProducts = filteredProductsStream.collect(Collectors.toList());

        productAdapter.updateData(filteredProducts);
        updateItemCount(filteredProducts.size());
    }

    private void loadProducts() {
        List<Product> products = productStorage.getAllProducts();
        productAdapter.updateData(products);
        updateItemCount(products.size());
    }

    private void updateItemCount(int count) {
        TextView itemCountTextView = findViewById(R.id.itemCountTextView);
        itemCountTextView.setText("Items count: " + count);
    }


    private void setupRecyclerView() {
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        productAdapter = new ProductAdapter(new ArrayList<>(), this, productStorage, this);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productsRecyclerView.setAdapter(productAdapter);
    }

    @Override
    public void showProductEditActivity(Product product) {
        Intent intent = new Intent(this, ProductEditActivity.class);
        intent.putExtra("productId", product.getId());
        editItemLauncher.launch(intent);
    }
}

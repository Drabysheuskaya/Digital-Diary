package adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm1.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import management.IProductStorage;
import model.Product;
import model.ProductCategory;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private Context context;
    private final IProductStorage productStorage;
    private final IProductUICallbacks callbacks;

    public ProductAdapter(List<Product> productList,
                          Context context,
                          IProductStorage productStorage,
                          IProductUICallbacks callbacks) {
        this.productList = productList;
        this.context = context;
        this.productStorage = productStorage;
        this.callbacks = callbacks;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
        holder.itemView.setOnClickListener(v -> {
            if (product.isUsable()) {
                // Launch detail activity for editing
                callbacks.showProductEditActivity(product);
            } else {
                // Show message that editing is not allowed for expired products
                Toast.makeText(context, "Cannot edit expired product.", Toast.LENGTH_SHORT).show();
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (product.isUsable()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Product");
                builder.setMessage("Are you sure you want to delete this product?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    productStorage.deleteProduct(product.getId());
                    productList.remove(product);
                    notifyDataSetChanged();
                    updateItemCount(productList.size());
                });
                builder.setNegativeButton("No", null);
                builder.show();
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Method to update the data in the adapter
    public void updateData(List<Product> newData) {
        productList.clear();
        productList.addAll(newData);
        // Sort the productList by expiration dates
        Collections.sort(productList, (p1, p2) -> p1.getExpirationDate().compareTo(p2.getExpirationDate()));
        notifyDataSetChanged();
    }

    private void updateItemCount(int count) {
        // Update the item count as needed
    }

    public void sortListByType(ProductCategory type) {
        List<Product> sortedList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getType() == type) {
                sortedList.add(product);
            }
        }
        // Sort the sortedList by expirationDate
        Collections.sort(sortedList, (p1, p2) -> p1.getExpirationDate().compareTo(p2.getExpirationDate()));
        productList.clear();
        productList.addAll(sortedList);
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView productNameTextView;
        private TextView productCategoryTextView;
        private TextView expirationDateTextView;
        private TextView quantityTextView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productName);
            productCategoryTextView = itemView.findViewById(R.id.productCategory);
            expirationDateTextView = itemView.findViewById(R.id.expirationDate);
            quantityTextView = itemView.findViewById(R.id.quantity);
        }

        public void bind(Product product) {
            productNameTextView.setText(product.getName());
            productCategoryTextView.setText(product.getCategory().getCategoryName());
            expirationDateTextView.setText("Expiration Date: " + formatDate(product.getExpirationDate()));
            quantityTextView.setText("Quantity: " + product.getQuantity());
        }

        private String formatDate(Date date) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.format(date);
        }
    }
}


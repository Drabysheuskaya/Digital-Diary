package repository;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm1.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import management.IProductStorage;
import management.InMemoryProductStorage;
import model.Product;
import model.ProductCategory;

public class ProductEditActivity extends AppCompatActivity {

    private final IProductStorage productStorage = InMemoryProductStorage.getInstance();
    private EditText productName, productExpirationDate, productQuantity;
    private Spinner productCategory;
    private Product editedProduct;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        int productId = getIntent().getIntExtra("productId", -1);
        editedProduct = null;
        if (productId != -1) {
            editedProduct = productStorage.getProductById(productId);
        }

        productName = findViewById(R.id.productName);
        productExpirationDate = findViewById(R.id.productExpirationDate);
        productQuantity = findViewById(R.id.productQuantity);
        productCategory = findViewById(R.id.productCategory);

        setupCategorySpinner();

        if (editedProduct != null) {
            productName.setText(editedProduct.getName());
            productExpirationDate.setText(dateFormat.format(editedProduct.getExpirationDate()));
            productQuantity.setText(Integer.toString(editedProduct.getQuantity()));
            productCategory.setSelection(editedProduct.getCategory().ordinal());
        }

        productExpirationDate.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) return;

            Calendar curDate = Calendar.getInstance();
            curDate.add(Calendar.DATE, 1);
            curDate.set(Calendar.HOUR_OF_DAY, 0);
            curDate.set(Calendar.MINUTE, 0);
            curDate.set(Calendar.SECOND, 0);
            curDate.set(Calendar.MILLISECOND, 0);

            DatePickerDialog datePicker = new DatePickerDialog(
                    this,
                    (view_, year, month, dayOfMonth) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth, 0, 0, 0);
                        productExpirationDate.setText(dateFormat.format(calendar.getTime()));
                    },
                    curDate.get(Calendar.YEAR),
                    curDate.get(Calendar.MONTH),
                    curDate.get(Calendar.DAY_OF_MONTH));

            datePicker.getDatePicker().setMinDate(curDate.getTimeInMillis());
            datePicker.show();
        });
    }

    private void setupCategorySpinner() {
        ArrayAdapter<ProductCategory> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,
                ProductCategory.values());
        productCategory.setAdapter(adapter);
    }

    public void saveProduct(View view) {
        String name = productName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        String expirationDateString = productExpirationDate.getText().toString();
        Date expirationDate;
        try {
            expirationDate = dateFormat.parse(expirationDateString);
            if (new Date().after(expirationDate)) {
                Toast.makeText(this, "Date cannot be in the past.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format. Please use YYYY-MM-DD.", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(productQuantity.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Quantity must be a numerical value.", Toast.LENGTH_SHORT).show();
            return;
        }

        ProductCategory category = (ProductCategory) productCategory.getSelectedItem();

        if (editedProduct != null) {
            editedProduct.setName(name);
            editedProduct.setExpirationDate(expirationDate);
            editedProduct.setQuantity(quantity);
            editedProduct.setCategory(category);
        } else {
            Product newProduct = new Product(0, name, expirationDate, category, quantity);
            productStorage.saveProduct(newProduct);
        }

        setResult(RESULT_OK);
        finish();
    }

    public void cancelCreateActivity(View view) {
        finish();
    }
}

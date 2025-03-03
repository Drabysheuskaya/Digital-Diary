package model;


import androidx.annotation.NonNull;

public enum ProductCategory {
    FOOD_PRODUCTS("Food Products"),
    MEDICINES("Medicines"),
    COSMETICS("Cosmetics");

    private final String categoryName;

    ProductCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public static ProductCategory getCategoryByName(String categoryName) {
        for (ProductCategory category : ProductCategory.values()) {
            if (category.getCategoryName().equals(categoryName)) {
                return category;
            }
        }
        return null;
    }


    @NonNull
    @Override
    public String toString() {
        return getCategoryName();
    }
}
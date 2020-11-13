package com.need.mymall;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductDatabase implements Serializable {
    private String itemId;
    private ArrayList<String> categories;
    private String title;
    private ArrayList<String> imagesUrl;
    private String price;
    private double weight;
    private String description;
    private String shortDescription;
    private int reviewCount;
    private String averageRating;
    private CategoryDatabase categoryDatabase;
    private ArrayList<ProductSpecificationModel> localizedAspects;
    private String name;
    private String category;

    private String transportFeePrice;
    private String surchagePrice;
    private String totalPrice;

    public ProductDatabase() {
        imagesUrl = new ArrayList<>();
        categories = new ArrayList<>();
        this.reviewCount = 27;
        this.averageRating = "4.5";
        this.weight = 0.5;
        this.categoryDatabase = new CategoryDatabase();
        localizedAspects = new ArrayList<ProductSpecificationModel>();
        this.category = "";
    }

    public ProductDatabase(String title, ArrayList<String> imagesUrl, String price, String description, String shortDescription) {
        this.title = title;
        this.imagesUrl = imagesUrl;
        this.price = price;
        this.description = description;
        this.shortDescription = shortDescription;
    }

    public void caculatePrice (final int quantity) {

        double transportFee = getCategoryDatabase().getTransportFee(getWeight()) * quantity;
        double surchage = getCategoryDatabase().getSurchagePrice(getPrice());
        double total = 0;
        total = (Double.parseDouble(price) + surchage ) * quantity;
        total = Math.ceil(total * 100) / 100;

        transportFeePrice = String.valueOf(transportFee);
        surchagePrice = String.valueOf(surchage);
        totalPrice = String.valueOf(total);

    }
    public CategoryDatabase getCategoryDatabase() {
        return categoryDatabase;
    }

    public void setCategoryDatabase(CategoryDatabase categoryDatabase) {
        this.categoryDatabase = categoryDatabase;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getImagesUrl() {
        return imagesUrl;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setImagesUrl(ArrayList<String> imagesUrl) {
        this.imagesUrl = imagesUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public ArrayList<ProductSpecificationModel> getLocalizedAspects() {
        return localizedAspects;
    }

    public void setLocalizedAspects(ArrayList<ProductSpecificationModel> localizedAspects) {
        this.localizedAspects = localizedAspects;
    }

    public String getTransportFeePrice() {
        return transportFeePrice;
    }

    public void setTransportFeePrice(String transportFeePrice) {
        this.transportFeePrice = transportFeePrice;
    }

    public String getSurchagePrice() {
        return surchagePrice;
    }

    public void setSurchagePrice(String surchagePrice) {
        this.surchagePrice = surchagePrice;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

package com.need.mymall;

public class CartItemModel {
    public static final int CART_ITEM = 0;
    public static final int ORDER_ITEN = 1;
    public static final int TOTAL_AMOUNT = 2;

    private int type;

    private String productId;
    private String productImageUrl;
    private String productTitle;
    private String productPrice;
    private String productTransportFee;
    private int productQuantity;
    private String productDes;
    private String categoryId;

    private int totalItem;
    private String totalAmount;

    public CartItemModel(int type,String productId, String productImageUrl, String productTitle, String productPrice,String productTransportFee, int productQuantity,String productDes,String categoryId) {
        this.type = type;
        this.productId = productId;
        this.productImageUrl = productImageUrl;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.productTransportFee = productTransportFee;
        this.productQuantity = productQuantity;
        this.productDes = productDes;
        this.categoryId = categoryId;
    }

    public CartItemModel(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductTransportFee() {
        return productTransportFee;
    }

    public void setProductTransportFee(String productTransportFee) {
        this.productTransportFee = productTransportFee;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public int getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getProductDes() {
        return productDes;
    }

    public void setProductDes(String productDes) {
        this.productDes = productDes;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}

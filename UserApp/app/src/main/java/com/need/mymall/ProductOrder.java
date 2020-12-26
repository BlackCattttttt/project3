package com.need.mymall;

public class ProductOrder {
    String productImg;
    String productTitle;
    int productQuantity;
    String productPrice;
    double productWeight;
    double productTransportfee;

    public ProductOrder(String productImg, String productTitle, int productQuantity, String productPrice, double productWeight, double productTransportfee) {
        this.productImg = productImg;
        this.productTitle = productTitle;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
        this.productWeight = productWeight;
        this.productTransportfee = productTransportfee;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public double getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(double productWeight) {
        this.productWeight = productWeight;
    }

    public double getProductTransportfee() {
        return productTransportfee;
    }

    public void setProductTransportfee(double productTransportfee) {
        this.productTransportfee = productTransportfee;
    }
}

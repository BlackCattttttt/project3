package com.need.mymall;

import java.io.Serializable;

public class CategoryDatabase implements Serializable {

    public static final int NO_SURCHARGE = 0;
    public static final int SURCHARGE_BY_PRICE = 1;
    public static final int SURCHARGE_BY_QUANTITY = 2;
    public static final int SURCHARGE = 3;

    private String id;
    private String name;
    private int price;
    private int surchargeType;

    private int percentSurcharge;
    private int priceSurcharge;

    public CategoryDatabase()  {
    }

    public CategoryDatabase(String id, String name, int price, int surchargeType, int percentSurcharge, int priceSurcharge) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.surchargeType = surchargeType;
        this.percentSurcharge = percentSurcharge;
        this.priceSurcharge = priceSurcharge;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSurchargeType() {
        return surchargeType;
    }

    public void setSurchargeType(int surchargeType) {
        this.surchargeType = surchargeType;
    }

    public int getPercentSurcharge() {
        return percentSurcharge;
    }

    public void setPercentSurcharge(int percentSurcharge) {
        this.percentSurcharge = percentSurcharge;
    }

    public int getPriceSurcharge() {
        return priceSurcharge;
    }

    public void setPriceSurcharge(int priceSurcharge) {
        this.priceSurcharge = priceSurcharge;
    }

    public String getSurcharge () {
        if (surchargeType == NO_SURCHARGE) {
            return "";
        } else if (surchargeType == SURCHARGE_BY_PRICE){
            return ">$" + String.valueOf(priceSurcharge) + ": phụ thu" + String.valueOf(percentSurcharge) + "%";
        } else if (surchargeType == SURCHARGE_BY_QUANTITY){
            return "$" + String.valueOf(priceSurcharge) + "/cái";
        } else if (surchargeType == SURCHARGE){
            return String.valueOf(percentSurcharge) + "%";
        }
        return "";
    }

    public double getTransportFee (double weight) {
        return weight * this.price;
    }

    public double getSurchagePrice(String price) {
        switch (surchargeType) {
            case CategoryDatabase.NO_SURCHARGE:
                return 0;
            case CategoryDatabase.SURCHARGE:
                return Double.parseDouble(price) * this.percentSurcharge / 100;
            case CategoryDatabase.SURCHARGE_BY_PRICE:
                if (Double.parseDouble(price)>this.priceSurcharge) {
                    return Double.parseDouble(price) * this.percentSurcharge / 100;
                } else {
                    return 0;
                }
            case CategoryDatabase.SURCHARGE_BY_QUANTITY:
                return this.priceSurcharge;
            default:
                return 0;
        }
    }
}

package com.need.mymall;

public class AddressesModel {

    private String fullname;
    private String phonenumber;
    private String addressFull;
    private Boolean selected;

    private String provinces;
    private String distric;
    private String address;
    private String description;

    public AddressesModel(String fullname, String phonenumber, String addressFull, boolean selected) {
        this.fullname = fullname;
        this.phonenumber = phonenumber;
        this.addressFull = addressFull;
        this.selected = selected;
    }

    public String getFullname() {
        return fullname;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressFull() {
        return addressFull;
    }

    public void setAddressFull(String addressFull) {
        this.addressFull = addressFull;
    }

    public String getProvinces() {
        return provinces;
    }

    public void setProvinces(String provinces) {
        this.provinces = provinces;
    }

    public String getDistric() {
        return distric;
    }

    public void setDistric(String distric) {
        this.distric = distric;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

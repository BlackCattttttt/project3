package com.need.mymall;

import java.util.ArrayList;
import java.util.Date;

public class MyOrderItemModel {

    private String orderStatus;
    private int orderTotalItem;
    private String orderTotalPrice;
    private String deposit;
    private String orderId;

    private int orderTotalProduct;
    private ArrayList<ProductOrder> productOrderArrayList;
    private Date orderedDate;
    private Date payedDate;
    private Date packedDate;
    private Date shipedUsaDate;
    private Date shipedVnDate;
    private Date deliveriedDate;
    private Date cancelledDate;

    private String fullName;
    private String phoneNum;
    private String address;

    private String userId;

    public MyOrderItemModel( String orderStatus, int orderTotalItem, String orderTotalPrice,String deposit, String orderId, int orderTotalProduct, ArrayList<ProductOrder> productOrderArrayList, Date orderedDate, Date payedDate, Date pacedDate, Date shipedUsaDate, Date shipedVnDate, Date deliveriedDate, Date cancelledDate,String userId) {
        this.orderStatus = orderStatus;
        this.orderTotalItem = orderTotalItem;
        this.orderTotalPrice = orderTotalPrice;
        this.deposit = deposit;
        this.orderId = orderId;
        this.orderTotalProduct = orderTotalProduct;
        this.productOrderArrayList = productOrderArrayList;
        this.orderedDate = orderedDate;
        this.payedDate = payedDate;
        this.packedDate = pacedDate;
        this.shipedUsaDate = shipedUsaDate;
        this.shipedVnDate = shipedVnDate;
        this.deliveriedDate = deliveriedDate;
        this.cancelledDate = cancelledDate;
        this.userId = userId;
    }

    public void setDelivery(String fullName,String phoneNum,String address) {
        this.fullName = fullName;
        this.phoneNum = phoneNum;
        this.address = address;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getOrderTotalItem() {
        return orderTotalItem;
    }

    public void setOrderTotalItem(int orderTotalItem) {
        this.orderTotalItem = orderTotalItem;
    }

    public String getOrderTotalPrice() {
        return orderTotalPrice;
    }

    public void setOrderTotalPrice(String orderTotalPrice) {
        this.orderTotalPrice = orderTotalPrice;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getOrderTotalProduct() {
        return orderTotalProduct;
    }

    public void setOrderTotalProduct(int orderTotalProduct) {
        this.orderTotalProduct = orderTotalProduct;
    }

    public ArrayList<ProductOrder> getProductOrderArrayList() {
        return productOrderArrayList;
    }

    public void setProductOrderArrayList(ArrayList<ProductOrder> productOrderArrayList) {
        this.productOrderArrayList = productOrderArrayList;
    }

    public Date getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(Date orderedDate) {
        this.orderedDate = orderedDate;
    }

    public Date getPayedDate() {
        return payedDate;
    }

    public void setPayedDate(Date payedDate) {
        this.payedDate = payedDate;
    }

    public Date getPackedDate() {
        return packedDate;
    }

    public void setPackedDate(Date pacedDate) {
        this.packedDate = pacedDate;
    }

    public Date getShipedUsaDate() {
        return shipedUsaDate;
    }

    public void setShipedUsaDate(Date shipedUsaDate) {
        this.shipedUsaDate = shipedUsaDate;
    }

    public Date getShipedVnDate() {
        return shipedVnDate;
    }

    public void setShipedVnDate(Date shipedVnDate) {
        this.shipedVnDate = shipedVnDate;
    }

    public Date getDeliveriedDate() {
        return deliveriedDate;
    }

    public void setDeliveriedDate(Date deliveriedDate) {
        this.deliveriedDate = deliveriedDate;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

package com.example.tattomobile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Response_b implements Serializable {

    @SerializedName("booking_id")
    @Expose
    private Integer bookingId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("service_id")
    @Expose
    private Integer serviceId;
    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("slot_id")
    @Expose
    private Integer slotId;
    @SerializedName("booking_date")
    @Expose
    private String bookingDate;
    @SerializedName("product_amount")
    @Expose
    private Integer productAmount;
    @SerializedName("amount_paid")
    @Expose
    private Integer amountPaid;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("booking_img")
    @Expose
    private String booking_img;

    public String getBooking_img() {
        return booking_img;
    }

    public void setBooking_img(String booking_img) {
        this.booking_img = booking_img;
    }

    @SerializedName("payment_id")
    @Expose
    private String paymentId;
    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("fail_reason")
    @Expose
    private String failReason;
    @SerializedName("booking_status")
    @Expose
    private String bookingStatus;

    public String getService_Status() {
        return Service_Status;
    }

    public void setService_Status(String service_Status) {
        Service_Status = service_Status;
    }

    @SerializedName("service_status")
    @Expose
    private String Service_Status;
    @SerializedName("created_by")
    @Expose
    private Object createdBy;
    @SerializedName("visibility")
    @Expose
    private Integer visibility;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("slot")
    @Expose
    private Slot slot;
    @SerializedName("product")
    @Expose
    private Product product;

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getSlotId() {
        return slotId;
    }

    public void setSlotId(Integer slotId) {
        this.slotId = slotId;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Integer getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(Integer productAmount) {
        this.productAmount = productAmount;
    }

    public Integer getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Integer amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Object getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getVisibility() {
        return visibility;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }


}

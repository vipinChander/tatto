package com.example.tattomobile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Slot implements Serializable {

    @SerializedName("slot_id")
    @Expose
    private Integer slotId;
    @SerializedName("slot_time")
    @Expose
    private String slotTime;
    @SerializedName("service_id")
    @Expose
    private Integer serviceId;
    @SerializedName("slot_status")
    @Expose
    private Integer slotStatus;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Slot(String slotTime,String slotId) {
        this.slotTime = slotTime;
        this.slotId=Integer.parseInt(slotId);
    }

    public Integer getSlotId() {
        return slotId;
    }

    public void setSlotId(Integer slotId) {
        this.slotId = slotId;
    }

    public String getSlotTime() {
        return slotTime;
    }

    public void setSlotTime(String slotTime) {
        this.slotTime = slotTime;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getSlotStatus() {
        return slotStatus;
    }

    public void setSlotStatus(Integer slotStatus) {
        this.slotStatus = slotStatus;
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
}

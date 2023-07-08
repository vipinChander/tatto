package com.example.tattomobile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TimefetchData implements Serializable {

    @Expose
    private Boolean error;
    @SerializedName("response")
    @Expose
    private List<Slot> response = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public List<Slot> getResponse() {
        return response;
    }

    public void setResponse(List<Slot> response) {
        this.response = response;
    }

}

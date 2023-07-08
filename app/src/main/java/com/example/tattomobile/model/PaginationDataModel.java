package com.example.tattomobile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PaginationDataModel implements Serializable {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("response")
    @Expose
    private List<Response__> response = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public List<Response__> getResponse() {
        return response;
    }

    public void setResponse(List<Response__> response) {
        this.response = response;
    }
}

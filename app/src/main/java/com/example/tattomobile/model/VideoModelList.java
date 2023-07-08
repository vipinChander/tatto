package com.example.tattomobile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class VideoModelList implements Serializable {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("response")
    @Expose
    private List<Response_vlist> response = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public List<Response_vlist> getResponse() {
        return response;
    }

    public void setResponse(List<Response_vlist> response) {
        this.response = response;
    }

}

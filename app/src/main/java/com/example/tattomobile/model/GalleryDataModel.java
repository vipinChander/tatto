package com.example.tattomobile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GalleryDataModel implements Serializable {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("response")
    @Expose
    private Response_Gallery response;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public Response_Gallery getResponse() {
        return response;
    }

    public void setResponse(Response_Gallery response) {
        this.response = response;
    }
}

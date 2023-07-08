package com.example.tattomobile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginMainModel implements Serializable {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("response")
    @Expose
    private LoginSubModel loginSubModel;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public LoginSubModel getLoginSubModel() {
        return loginSubModel;
    }

    public void setResponse(LoginSubModel loginSubModel) {
        this.loginSubModel = loginSubModel;
    }
}

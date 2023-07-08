package com.example.tattomobile.login;

import java.util.List;

import androidx.lifecycle.MutableLiveData;

public class MobileLoginRepository {
    private MutableLiveData<List<String>> mutableLiveData = new MutableLiveData<>();

    public MutableLiveData<List<String>> getMutableLiveData() {

        return mutableLiveData;
    }
}

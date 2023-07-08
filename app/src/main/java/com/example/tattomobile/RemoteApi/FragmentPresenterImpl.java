package com.example.tattomobile.RemoteApi;

import android.content.Context;

public class FragmentPresenterImpl implements Callback {
  Context v;
  Callback callback;

    public FragmentPresenterImpl(Context context) {
        v=context;
    }

    @Override
    public void callBackPayment(boolean result) {

    }
}

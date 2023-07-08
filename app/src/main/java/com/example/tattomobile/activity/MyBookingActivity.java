package com.example.tattomobile.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tattomobile.Adapter.MyBookListAdapter;
import com.example.tattomobile.Home.HomeActivity;
import com.example.tattomobile.R;
import com.example.tattomobile.RemoteApi.RestApiService;
import com.example.tattomobile.RemoteApi.RetrofitInstance;
import com.example.tattomobile.databinding.ActivityMyBookingBinding;
import com.example.tattomobile.model.Response_b;
import com.example.tattomobile.utility.Prefs;
import com.example.tattomobile.utility.UtilityClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import io.github.rupinderjeet.kprogresshud.KProgressHUD;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBookingActivity extends AppCompatActivity {

    private ActivityMyBookingBinding binding;
    private UtilityClass utilityClass;
    private KProgressHUD pDialog;
    private List<Response_b> responseModelList;
boolean isFromPaymentPage=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        utilityClass = UtilityClass.getInstance();
        pDialog = utilityClass.getProgressDialog(this);
        try {
Bundle bundle=getIntent().getExtras();
isFromPaymentPage=bundle.getBoolean("isFrompayment");
        } catch (Exception e) {
            e.printStackTrace();
        }
        getTestimonialsList();
        binding.ibBack.setOnClickListener(view ->
        {
            onBackPressed();
        });
    }

    private void bookingRecycleViewSetUp() {
        binding.rvBookList.setLayoutManager(new LinearLayoutManager(MyBookingActivity.this));
        MyBookListAdapter adapter = new MyBookListAdapter(MyBookingActivity.this, responseModelList);
        binding.rvBookList.setAdapter(adapter);
    }

    private void getTestimonialsList() {
        if (!UtilityClass.getInstance().checkInternetConnection(this)) {
            utilityClass.showAlertDialog(this, getResources().getString(R.string.internet_connection));
            return;
        }
        binding.progressBookingActivityPage.setVisibility(View.VISIBLE);
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).fetchBookingData("booking/get/" + Prefs.getUserId() + "/payments", "Bearer " + Prefs.getToken())
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                boolean error = jsonObject.optBoolean("error");
                                Gson gson = new Gson();
                                binding.progressBookingActivityPage.setVisibility(View.GONE);
                                if (!error) {
                                    Type listType = new TypeToken<List<Response_b>>() {
                                    }.getType();

                                    responseModelList = gson.fromJson(String.valueOf(jsonObject.optJSONArray("response")), listType);
                                    if (!(responseModelList.size() == 0)) {
                                        bookingRecycleViewSetUp();
                                    } else {
                                        binding.correptedFiles.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    binding.correptedFiles.setVisibility(View.VISIBLE);
                                    utilityClass.showAlertDialog(MyBookingActivity.this, getResources().getString(R.string.something_went_wrong));
                                }
                            } else {
                                binding.correptedFiles.setVisibility(View.VISIBLE);
                                utilityClass.showAlertDialog(MyBookingActivity.this, getResources().getString(R.string.something_went_wrong));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            binding.progressBookingActivityPage.setVisibility(View.GONE);
                            binding.correptedFiles.setVisibility(View.VISIBLE);
                            utilityClass.showAlertDialog(MyBookingActivity.this, getResources().getString(R.string.something_went_wrong));
                        }
                        binding.progressBookingActivityPage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        binding.progressBookingActivityPage.setVisibility(View.GONE);
                        binding.correptedFiles.setVisibility(View.VISIBLE);
                        utilityClass.showAlertDialog(MyBookingActivity.this, getResources().getString(R.string.something_went_wrong));
                    }
                });


    }

    @Override
    public void onBackPressed() {
        if (isFromPaymentPage) {
            Intent instant = new Intent(this, HomeActivity.class);

        startActivity(instant);
        finishAffinity();
        }else {
            super.onBackPressed();
        }
    }

}
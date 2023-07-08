package com.example.tattomobile.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.tattomobile.BuildConfig;
import com.example.tattomobile.Home.HomeActivity;
import com.example.tattomobile.R;
import com.example.tattomobile.RemoteApi.RestApiService;
import com.example.tattomobile.RemoteApi.RetrofitInstance;
import com.example.tattomobile.databinding.ActivityPasswordBinding;
import com.example.tattomobile.model.LoginSubModel;
import com.example.tattomobile.model.ProfileResponse;
import com.example.tattomobile.model.TimeSave;
import com.example.tattomobile.utility.Prefs;
import com.example.tattomobile.utility.UtilityClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.rupinderjeet.kprogresshud.KProgressHUD;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordActivity extends AppCompatActivity {

    private ActivityPasswordBinding binding;
    private String mobileNo = "";
    private KProgressHUD pDialog;
    private UtilityClass utilityClass;
    private boolean isShowing = false;
    private ProfileResponse responseModelList;
    TimeSave timeSave = TimeSave.getInstance();

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_password);

        Bundle b = getIntent().getExtras();
        if (b != null)
            mobileNo = b.getString("MOBILE_NUMBER");

        utilityClass = UtilityClass.getInstance();
        pDialog = utilityClass.getProgressDialog(this);
        binding.ivPwIcon.setOnClickListener(view -> {
            if (isShowing) {
                binding.ivPwIcon.setImageResource(R.drawable.password_hide_icon);
                binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                isShowing = false;
            } else {
                binding.ivPwIcon.setImageResource(R.drawable.password_show_icon);
                binding.etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                isShowing = true;
            }

        });

        binding.button.setOnClickListener(view -> {
            String password = binding.etPassword.getText().toString();
            if (!password.isEmpty())
                checkLogin(mobileNo, password);
            else
                utilityClass.showAlertDialog(PasswordActivity.this, "Please Enter Password");
        });

    }

    private void checkLogin(String mobileNumber, String password) {
        if (!UtilityClass.getInstance().checkInternetConnection(PasswordActivity.this)) {
            utilityClass.showAlertDialog(PasswordActivity.this, getResources().getString(R.string.internet_connection));
            return;
        }
        binding.proPasswordPage.setVisibility(View.VISIBLE);
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).checkLoginUser(BuildConfig.BASE_URL + "user/login?mobile_number=" + mobileNumber + "&password=" + password)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                Gson gson = new Gson();
                                boolean error = jsonObject.optBoolean("error");
                                if (!error) {
                                    LoginSubModel loginSubModel = gson.fromJson(jsonObject.optString("response"), LoginSubModel.class);
                                    Prefs.saveUser(loginSubModel);
                                    Prefs.saveToken(loginSubModel.getAuthToken());
                                    loadHomePageScreen(loginSubModel);
                                } else {
                                    String msg = jsonObject.optString("msg");
                                    utilityClass.showAlertDialog(PasswordActivity.this, msg);
                                }
                            } else {
                                utilityClass.showAlertDialog(PasswordActivity.this, getResources().getString(R.string.something_went_wrong));
                            }
                        } catch (JSONException e) {
                            binding.proPasswordPage.setVisibility(View.GONE);
                            utilityClass.showAlertDialog(PasswordActivity.this, getResources().getString(R.string.something_went_wrong));
                            e.printStackTrace();
                        }
                        binding.proPasswordPage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
               binding.proPasswordPage.setVisibility(View.VISIBLE);
                        utilityClass.showAlertDialog(PasswordActivity.this, getResources().getString(R.string.something_went_wrong));
                    }
                });
    }

    private void loadHomePageScreen(LoginSubModel loginSubModel) {
        fetchProfiledata();
        Intent homeIntent = new Intent(PasswordActivity.this, HomeActivity.class);
        homeIntent.putExtra("Login_Sub_Model", loginSubModel);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
    }
    public void fetchProfiledata() {
        if (!UtilityClass.getInstance().checkInternetConnection(this)) {
            utilityClass.showAlertDialog(this, getResources().getString(R.string.internet_connection));
            return;
        }
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).fetchProfileData("user/get/"+Prefs.getUserId()+"/details","Bearer " + Prefs.getToken())
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                boolean error = jsonObject.optBoolean("error");
                                Gson gson = new Gson();

                                if (!error) {
                                    responseModelList = gson.fromJson(jsonObject.optString("response"), ProfileResponse.class);
                                    timeSave.setCoinsEarn(responseModelList.getEarnedPoints().toString());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {


                    }
                });
    }
}
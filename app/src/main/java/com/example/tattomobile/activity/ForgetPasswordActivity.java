package com.example.tattomobile.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.example.tattomobile.BuildConfig;
import com.example.tattomobile.R;
import com.example.tattomobile.RemoteApi.RestApiService;
import com.example.tattomobile.RemoteApi.RetrofitInstance;
import com.example.tattomobile.databinding.ActivityForgetPasswordBinding;
import com.example.tattomobile.databinding.ActivityMyBookingBinding;
import com.example.tattomobile.login.MobileLoginActivity;
import com.example.tattomobile.utility.Prefs;
import com.example.tattomobile.utility.UtilityClass;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {
    ActivityForgetPasswordBinding binding;
    private boolean isShowing = false;
    private boolean isShowingConf = false;
    private UtilityClass utilityClass;
    private String mobileNo = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        utilityClass = UtilityClass.getInstance();
        Bundle b = getIntent().getExtras();
        if (b != null)
            mobileNo = b.getString("MOBILE_NUMBER");

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
        binding.ivPwIconConfirm.setOnClickListener(view -> {
            if (isShowingConf) {
                binding.ivPwIconConfirm.setImageResource(R.drawable.password_hide_icon);
                binding.etConformPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                isShowingConf = false;
            } else {
                binding.ivPwIconConfirm.setImageResource(R.drawable.password_show_icon);
                binding.etConformPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                isShowingConf = true;
            }

        });
        binding.ibBack.setOnClickListener(v -> {
       onBackPressed();
        });
        binding.SubmitButton.setOnClickListener(v -> {
            if (mobileNo != null && !mobileNo.isEmpty()) {
                if (binding.etPassword.getText().toString().equalsIgnoreCase(binding.etConformPassword.getText().toString())) {
                    updatedPassword(mobileNo, binding.etPassword.getText().toString());
                }else {
                    Toast.makeText(this, "Password and conform password are not matched", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
        private void updatedPassword(String mobileNo,String password) {
            if (!UtilityClass.getInstance().checkInternetConnection(ForgetPasswordActivity.this)) {
                utilityClass.showAlertDialog(ForgetPasswordActivity.this, getResources().getString(R.string.internet_connection));
                return;
            }
            binding.proMobileLogin.setVisibility(View.VISIBLE);
            RetrofitInstance.getRetrofitInstance().create(RestApiService.class).passwordUpdated(BuildConfig.BASE_URL + "user/forgot/password?mobile_number=" + mobileNo+"&password="+password)
                    .enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    JSONObject jsonObject = new JSONObject(response.body().toString());
                                    boolean error = jsonObject.optBoolean("error");
                                    String  mge = "Password updated successfully!";
                                    binding.proMobileLogin.setVisibility(View.GONE);
                                    if (!error) {

                                        Toast.makeText(ForgetPasswordActivity.this, ""+mge, Toast.LENGTH_SHORT).show();

                     finish();
                                    } else {
                                    }
                                } else {
                                    binding.proMobileLogin.setVisibility(View.GONE);
                                    utilityClass.showAlertDialog(ForgetPasswordActivity.this, getResources().getString(R.string.something_went_wrong));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                binding.proMobileLogin.setVisibility(View.GONE);
                                utilityClass.showAlertDialog(ForgetPasswordActivity.this, getResources().getString(R.string.something_went_wrong));
                            }
                            binding.proMobileLogin.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable throwable) {
                            binding.proMobileLogin.setVisibility(View.GONE);
                            utilityClass.showAlertDialog(ForgetPasswordActivity.this, getResources().getString(R.string.something_went_wrong));
                        }
                    });
    }

}
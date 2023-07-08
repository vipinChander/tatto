package com.example.tattomobile.signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tattomobile.BuildConfig;
import com.example.tattomobile.Home.HomeActivity;
import com.example.tattomobile.R;
import com.example.tattomobile.RemoteApi.RestApiService;
import com.example.tattomobile.RemoteApi.RetrofitInstance;
import com.example.tattomobile.databinding.ActivityRegisterBinding;
import com.example.tattomobile.model.UserModel;
import com.example.tattomobile.utility.UtilityClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.rupinderjeet.kprogresshud.KProgressHUD;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FragmentManager fragmentManager;
    private KProgressHUD pDialog;
    private UtilityClass utilityClass;
    private String mobileNo = "";
    private boolean isPasswordShowing = false,isConfirmPasswordShowing=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        Bundle b = getIntent().getExtras();
        if (b != null)
            mobileNo = b.getString("MOBILE_NUMBER");

        fragmentManager = getSupportFragmentManager();
        utilityClass = UtilityClass.getInstance();
        pDialog = utilityClass.getProgressDialog(this);

        binding.ivPwIcon.setOnClickListener(view -> {
            if (isPasswordShowing) {
                binding.ivPwIcon.setImageResource(R.drawable.password_hide_icon);
                binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                isPasswordShowing = false;
            } else {
                binding.ivPwIcon.setImageResource(R.drawable.password_show_icon);
                binding.etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                isPasswordShowing = true;
            }
        });

        binding.ivConfirmPwIcon.setOnClickListener(view -> {
            if (isConfirmPasswordShowing) {
                binding.ivConfirmPwIcon.setImageResource(R.drawable.password_hide_icon);
                binding.etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                isConfirmPasswordShowing = false;
            } else {
                binding.ivConfirmPwIcon.setImageResource(R.drawable.password_show_icon);
                binding.etConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                isConfirmPasswordShowing = true;
            }
        });

        binding.ibBack.setOnClickListener(view -> onBackPressed());

        binding.btnSaveAndNext.setOnClickListener(view -> {
            String name = binding.etName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
            String referralCode = binding.etReferralCode.getText().toString().trim();
            if (name.isEmpty())
                utilityClass.showAlertDialog(RegisterActivity.this, "Please Enter FullName");
            else if (email.isEmpty())
                utilityClass.showAlertDialog(RegisterActivity.this, "Please Enter Email");
            else if (password.isEmpty())
                utilityClass.showAlertDialog(RegisterActivity.this, "Please Enter Password");
            else if (confirmPassword.isEmpty())
                utilityClass.showAlertDialog(RegisterActivity.this, "Please Enter Confirm Password");
            else if (!password.equals(confirmPassword))
                utilityClass.showAlertDialog(RegisterActivity.this, "Password and Confirm Password Does Not Match");
            else {
                createUser(name, email, mobileNo, password, referralCode);
            }
        });
    }

    private void createUser(String fullName, String emailId, String mobileNumber, String password, String referalCode) {
        if (!UtilityClass.getInstance().checkInternetConnection(RegisterActivity.this)) {
            utilityClass.showAlertDialog(RegisterActivity.this, getResources().getString(R.string.internet_connection));
            return;
        }
      binding.proRegPage.setVisibility(View.VISIBLE);
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).checkLoginUser(BuildConfig.BASE_URL + "user/create?full_name=" + fullName + "&mobile_number="
                        + mobileNumber + "&email_id=" + emailId + "&password=" + password + "&referal_code=" + referalCode)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                Log.d("REGTAG", "onResponse ->  " + response.body().toString());
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                Gson gson = new Gson();
                                boolean error = jsonObject.optBoolean("error");
                                binding.proRegPage.setVisibility(View.GONE);
                                if (!error) {
                                    UserModel userModel = gson.fromJson(jsonObject.optString("user"), UserModel.class);
                                    loadFragment(SkipRegFragment.newInstance(userModel));
                                } else {
                                    String msg = jsonObject.optString("msg");
                                    utilityClass.showAlertDialog(RegisterActivity.this, msg);
                                }
                            } else {
                                utilityClass.showAlertDialog(RegisterActivity.this, getResources().getString(R.string.something_went_wrong));
                            }
                        } catch (JSONException e) {
                            binding.proRegPage.setVisibility(View.GONE);
                            utilityClass.showAlertDialog(RegisterActivity.this, getResources().getString(R.string.something_went_wrong));
                            e.printStackTrace();
                        }
                        binding.proRegPage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        Log.d("REGTAG", "onFailure ->  " + throwable.getMessage());
                        binding.proRegPage.setVisibility(View.GONE);

                        utilityClass.showAlertDialog(RegisterActivity.this, getResources().getString(R.string.something_went_wrong));
                    }
                });
    }

    private void loadFragment(Fragment fragment) {
        binding.flHomeContainer.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_home_container, fragment);
        fragmentTransaction.commit();
    }

    private void loadHomePageScreen(UserModel userModel) {
        Intent homeIntent = new Intent(RegisterActivity.this, HomeActivity.class);
        homeIntent.putExtra("user_model", userModel);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
    }

}

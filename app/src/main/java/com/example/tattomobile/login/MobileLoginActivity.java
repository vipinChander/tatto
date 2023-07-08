package com.example.tattomobile.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.chaos.view.PinView;
import com.example.tattomobile.BuildConfig;
import com.example.tattomobile.R;
import com.example.tattomobile.RemoteApi.RestApiService;
import com.example.tattomobile.RemoteApi.RetrofitInstance;
import com.example.tattomobile.activity.ForgetPasswordActivity;
import com.example.tattomobile.activity.PaymentActivity;
import com.example.tattomobile.model.ProfileResponse;
import com.example.tattomobile.model.TimeSave;
import com.example.tattomobile.signup.RegisterActivity;
import com.example.tattomobile.utility.MyCallbackResponse;
import com.example.tattomobile.utility.Prefs;
import com.example.tattomobile.utility.UtilityClass;
import com.example.tattomobile.databinding.ActivityMobileLoginBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Random;

import io.github.rupinderjeet.kprogresshud.KProgressHUD;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileLoginActivity extends AppCompatActivity implements MyCallbackResponse {

    private ActivityMobileLoginBinding loginBinding;
    private String mobileNo = "";
    private KProgressHUD pDialog;
    private UtilityClass utilityClass;
private boolean isFromForget=false;
    private ProfileResponse responseModelList;
    TimeSave timeSave=TimeSave.getInstance();
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_mobile_login);

        utilityClass = UtilityClass.getInstance();
        pDialog = utilityClass.getProgressDialog(this);
        Random rand = new Random();
       String otpgen=String.valueOf(rand.nextInt(10000));

        if (!(otpgen.length() ==4)){
            Log.d("TAG","value of not otp"+otpgen+"1");
            Prefs.OtpSaved(otpgen);
        }else {
            Log.d("TAG","value of otp"+otpgen);
            Prefs.OtpSaved(otpgen);
        }
//        Prefs.OtpSaved();
        loginBinding.button.setOnClickListener(view -> {
            mobileNo = loginBinding.mobilenumber.getText().toString().trim();
            if (!mobileNo.isEmpty())
                checkMobileNumberExist(mobileNo);
            else
                utilityClass.showAlertDialog(MobileLoginActivity.this, "Please Enter Mobile Number");
        });
        loginBinding.forgetbutton.setOnClickListener(v -> {
            mobileNo = loginBinding.mobilenumber.getText().toString().trim();
            if (!mobileNo.isEmpty()){
                isFromForget=true;
                loginBinding.proMobileLogin.setVisibility(View.VISIBLE);
                verifyPin(mobileNo,Prefs.getOTPNumber());
        }else{
            Toast.makeText(this, "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }
        });
    }

    @Override
    protected void onResume() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        super.onResume();
    }

    private void checkMobileNumberExist(String mobileNo) {
        if (!UtilityClass.getInstance().checkInternetConnection(MobileLoginActivity.this)) {
            utilityClass.showAlertDialog(MobileLoginActivity.this, getResources().getString(R.string.internet_connection));
            return;
        }
        loginBinding.proMobileLogin.setVisibility(View.VISIBLE);
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).isMobileNumberExist2(BuildConfig.BASE_URL + "user/mobile/exist?mobile_number=" + mobileNo)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                boolean error = jsonObject.optBoolean("error");
                                boolean exist = jsonObject.optBoolean("exist");
                                loginBinding.proMobileLogin.setVisibility(View.GONE);
                                if (!error) {
                                    if (exist)
                                        loadPasswordScreen();
                                    else
                                        verifyPin(loginBinding.mobilenumber.getText().toString(),Prefs.getOTPNumber());

                                } else {
                                    utilityClass.showAlertDialog(MobileLoginActivity.this, getResources().getString(R.string.something_went_wrong));
                                }
                            } else {
                                loginBinding.proMobileLogin.setVisibility(View.GONE);
                                utilityClass.showAlertDialog(MobileLoginActivity.this, getResources().getString(R.string.something_went_wrong));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loginBinding.proMobileLogin.setVisibility(View.GONE);
                            utilityClass.showAlertDialog(MobileLoginActivity.this, getResources().getString(R.string.something_went_wrong));
                        }
                        loginBinding.proMobileLogin.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        loginBinding.proMobileLogin.setVisibility(View.GONE);
                        utilityClass.showAlertDialog(MobileLoginActivity.this, getResources().getString(R.string.something_went_wrong));
                    }
                });
    }

    private void loadPasswordScreen() {
        Intent passIntent = new Intent(MobileLoginActivity.this, PasswordActivity.class);
        passIntent.putExtra("MOBILE_NUMBER", mobileNo);
        startActivity(passIntent);
    }

    private void loadRegistrationScreen() {
        Intent passIntent = new Intent(MobileLoginActivity.this, RegisterActivity.class);
        passIntent.putExtra("MOBILE_NUMBER", mobileNo);
        startActivity(passIntent);
    }

    public void showOTPDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.otp_dialog);
        PinView pinView = dialog.findViewById(R.id.pinView);
        Button okBtn = dialog.findViewById(R.id.btnRetry);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView line = dialog.findViewById(R.id.verticalLinePopup);
        line.setVisibility(View.GONE);
        btnCancel.setVisibility(View.VISIBLE);
        okBtn.setOnClickListener(v -> {
           loginBinding.proMobileLogin.setVisibility(View.GONE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (Prefs.getOTPNumber().equalsIgnoreCase(pinView.getText().toString())) {
                if (!isFromForget) {
                    loadRegistrationScreen();
                }else {
                    loadOTPScreenScreen();
                }
                dialog.dismiss();
            } else {
                Toast.makeText(activity, "Enter Correct OTP", Toast.LENGTH_SHORT).show();
            }


        });
        dialog.show();
        btnCancel.setOnClickListener(v->{
            dialog.dismiss();
            loginBinding.proMobileLogin.setVisibility(View.GONE);
        });
    }

    private void verifyPin(String number,String otp) {
            if (!UtilityClass.getInstance().checkInternetConnection(MobileLoginActivity.this)) {
                utilityClass.showAlertDialog(MobileLoginActivity.this, getResources().getString(R.string.internet_connection));
                return;
            }
            RetrofitInstance.getRetrofitInstance().create(RestApiService.class).checkotp(BuildConfig.BASE_URL + "admin/send/otp?mobile_number="+number+"&otp="+otp)
                    .enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    JSONObject jsonObject = new JSONObject(response.body().toString());
                                    Gson gson = new Gson();
                                    String error = jsonObject.getString("ErrorMessage");

                                    if (error.equalsIgnoreCase("Done")) {
                                     showOTPDialog(MobileLoginActivity.this);
                                        loginBinding.proMobileLogin.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    } else {
                                        String msg = jsonObject.optString("msg");
                                    }
                                } else {
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    loginBinding.proMobileLogin.setVisibility(View.GONE);
                                    utilityClass.showAlertDialog(MobileLoginActivity.this, getResources().getString(R.string.something_went_wrong));
                                }
                            } catch (JSONException e) {
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                loginBinding.proMobileLogin.setVisibility(View.GONE);
                                utilityClass.showAlertDialog(MobileLoginActivity.this, getResources().getString(R.string.something_went_wrong));
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable throwable) {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            loginBinding.proMobileLogin.setVisibility(View.GONE);
                            utilityClass.showAlertDialog(MobileLoginActivity.this, getResources().getString(R.string.something_went_wrong));
                        }
                    });
        }

    @Override
    public void okButton(int tag) {

    }

    @Override
    public void cancelButton(int tag) {

    }
    private void loadOTPScreenScreen() {
        Intent passIntent = new Intent(MobileLoginActivity.this, ForgetPasswordActivity.class);
        passIntent.putExtra("MOBILE_NUMBER", mobileNo);
        startActivity(passIntent);
    }
}


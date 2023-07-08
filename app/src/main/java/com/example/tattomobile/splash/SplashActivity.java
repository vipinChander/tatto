package com.example.tattomobile.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tattomobile.Home.HomeActivity;
import com.example.tattomobile.R;
import com.example.tattomobile.RemoteApi.RestApiService;
import com.example.tattomobile.RemoteApi.RetrofitInstance;
import com.example.tattomobile.activity.ProfileActivity;
import com.example.tattomobile.login.MobileLoginActivity;
import com.example.tattomobile.model.ProfileResponse;
import com.example.tattomobile.model.TimeSave;
import com.example.tattomobile.utility.Prefs;
import com.example.tattomobile.utility.UtilityClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    UtilityClass utilityClass;
    private ProfileResponse responseModelList;
    TimeSave timeSave=TimeSave.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        utilityClass = UtilityClass.getInstance();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();


//                        String msg = "hh";
//                        Log.d("TAG", msg);
//                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                });
        fetchProfiledata();
        // Start long running operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 10;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void loadHomePageScreen() {
        if (!UtilityClass.getInstance().checkInternetConnection(this)) {
            utilityClass.showAlertDialog(this, getResources().getString(R.string.internet_connection));
            return;
        }
        Intent homeIntent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }

    private void loadLoginPageScreen() {
        if (!UtilityClass.getInstance().checkInternetConnection(this)) {
            utilityClass.showAlertDialog(this, getResources().getString(R.string.internet_connection));
            return;
        }
        Intent homeIntent = new Intent(SplashActivity.this, MobileLoginActivity.class);
        startActivity(homeIntent);
        finish();
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
                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        if (Prefs.isLoggedIn())
                                            loadHomePageScreen();
                                        else
                                            loadLoginPageScreen();
                                    }, 1000);
                                } else {
                                    logoutAllScreen();                                }
                            } else {
                                logoutAllScreen();                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            logoutAllScreen();                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        logoutAllScreen();

                    }
                });
    }
    public void logoutAllScreen(){
            Prefs.logoutUser();
            Intent loginIntent = new Intent(SplashActivity.this, MobileLoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
        }

    }

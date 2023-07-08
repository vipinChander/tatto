package com.example.tattomobile.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import io.github.rupinderjeet.kprogresshud.KProgressHUD;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.tattomobile.BuildConfig;
import com.example.tattomobile.Home.HomeActivity;
import com.example.tattomobile.R;
import com.example.tattomobile.RemoteApi.RestApiService;
import com.example.tattomobile.RemoteApi.RetrofitInstance;

import com.example.tattomobile.databinding.ActivityProfileBinding;
import com.example.tattomobile.login.MobileLoginActivity;
import com.example.tattomobile.login.PasswordActivity;
import com.example.tattomobile.model.ProfileResponse;
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

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private UtilityClass utilityClass;
    private KProgressHUD pDialog;
    private ProfileResponse responseModelList;
    private int REQUEST_PHONE_CALL=100;
    private String url = "https://sunnytattoostudio.com/privacy_policy.html";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        utilityClass = UtilityClass.getInstance();
        pDialog = utilityClass.getProgressDialog(this);
        fetchProfiledata();
        binding.tvUserName.setText(Prefs.getUserName());
        binding.tvEmail.setText(Prefs.getUserEmail());
        binding.ourofficestext.setOnClickListener(view -> {
            if (!UtilityClass.getInstance().checkInternetConnection(this)) {
                utilityClass.showAlertDialog(this, getResources().getString(R.string.internet_connection));
                return;
            }
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/place/Sunny+Tattoo+Studio%2F+Best+Tattoo+artist%2F+Tattoo+Parlor+In+Allahabad/@27.2361643,74.3011656,6z/data=!4m19!1m13!4m12!1m4!2m2!1d75.7255187!2d29.1684289!4e1!1m6!1m2!1s0x399acac340537667:0x43856dff82899362!2sSunny+Tattoo+Studio+P+Square+Mall,+Beside+KFC,+Civil+Lines,+Prayagraj,+Uttar+Pradesh+211006!2m2!1d81.836098!2d25.456172!3m4!1s0x399acac340537667:0x43856dff82899362!8m2!3d25.456172!4d81.836098"));
            startActivity(intent);
        });
        binding.ibBack.setOnClickListener(view -> onBackPressed());
        binding.btnLogout.setOnClickListener(view -> logoutUser());
        binding.copyimge.setOnClickListener(view -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("label", responseModelList.getUserReferalCode());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(this, "The Refferal code has been Copied ", Toast.LENGTH_SHORT).show();
        });
binding.sharereffertext.setOnClickListener(view->{
    try {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
        String shareMessage= "\nLet me recommend you this application\n";
        String shareMessageEnd= "\nUse referal code" +responseModelList.getUserReferalCode() + " to get 25 free coins\n\n";
        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n"  +shareMessageEnd ;
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "choose one"));
    } catch(Exception e) {
        //e.toString();
    }
        });
        binding.folowinsttext.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://instagram.com/sunnytattoostudio?igshid=YmMyMTA2M2Y=");
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

            likeIng.setPackage("com.instagram.android");

            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/xxx")));
            }
        });
        binding.youtubetext.setOnClickListener(v -> {
            getOpenFacebookIntent();
        });
        binding.youtubetext.setOnClickListener(v -> {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://youtube.com/channel/UCSVeCehkk1sGg5rhxxBYefA"));
            try {
                ProfileActivity.this.startActivity(webIntent);
            } catch (ActivityNotFoundException ex) {
            }
        });
        binding.contactustext.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
            }
            else
            {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:8318115565"));

                startActivity(callIntent);
            }


        });
    }
    public Intent getOpenFacebookIntent() {
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/426253597411506"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/channel/UCSVeCehkk1sGg5rhxxBYefA"));
        }
    }
    private void setDataToView() {
        binding.tvUserName.setText(responseModelList.getFullName());
        binding.tvEmail.setText(responseModelList.getMobileNumber() + "," + responseModelList.getEmailId());
        binding.rewardEarn.setText("Earned Coin: " + responseModelList.getEarnedPoints());
        binding.refferEarn.setText("Reffer Code  " + responseModelList.getUserReferalCode());

    }

    private void logoutUser() {
        Prefs.logoutUser();
        Intent loginIntent = new Intent(ProfileActivity.this, MobileLoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

    public void fetchProfiledata() {
        if (!UtilityClass.getInstance().checkInternetConnection(this)) {
            utilityClass.showAlertDialog(this, getResources().getString(R.string.internet_connection));
            return;
        }
//        binding.progressProfilePage.setVisibility(View.VISIBLE);
        RetrofitInstance.getRetrofitInstance().create(RestApiService.class).fetchProfileData("user/get/"+Prefs.getUserId()+"/details","Bearer " + Prefs.getToken())
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                boolean error = jsonObject.optBoolean("error");
                                Gson gson = new Gson();
//                                binding.progressProfilePage.setVisibility(View.GONE);
                                if (!error) {
                                    responseModelList = gson.fromJson(jsonObject.optString("response"), ProfileResponse.class);
                                    setDataToView();
                                } else {
                                    utilityClass.showAlertDialog(ProfileActivity.this, getResources().getString(R.string.something_went_wrong));
                                }
                            } else {
                                utilityClass.showAlertDialog(ProfileActivity.this, getResources().getString(R.string.something_went_wrong));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            binding.progressProfilePage.setVisibility(View.GONE);
                            utilityClass.showAlertDialog(ProfileActivity.this, getResources().getString(R.string.something_went_wrong));
                        }
//                        binding.progressProfilePage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
//                        binding.progressProfilePage.setVisibility(View.GONE);
                        utilityClass.showAlertDialog(ProfileActivity.this, getResources().getString(R.string.something_went_wrong));
                    }
                });

binding.privatePolicy.setOnClickListener(v -> {
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(url));
    startActivity(i);
});
    }
}